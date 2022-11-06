package cc.hicore.HookItemLoader.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.DexFinder.DexHelperFinder;
import cc.hicore.DexFinder.DexKitFinder;
import cc.hicore.DexFinder.IDexFinder;
import cc.hicore.HookItemLoader.bridge.BaseFindMethodInfo;
import cc.hicore.HookItemLoader.bridge.BaseMethodInfo;
import cc.hicore.HookItemLoader.bridge.CommonMethodInfo;
import cc.hicore.HookItemLoader.bridge.FindMethodByName;
import cc.hicore.HookItemLoader.bridge.FindMethodInvokingMethod;
import cc.hicore.HookItemLoader.bridge.FindMethodsWhichInvokeToTargetMethod;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.Initer.CommonHookLoaderDialog;
import de.robv.android.xposed.XposedBridge;

public class MethodScannerWorker {
    public static class ScannerLink{
        public CoreLoader.XPItemInfo item;
        public String ID;
        public BaseMethodInfo Info;
        public ArrayList<ScannerLink> LinkingID;
        public ScannerLink LinkToID;
    }
    static AtomicBoolean result = new AtomicBoolean();
    static volatile boolean isInit = false;
    public static boolean checkIsAvailable(){
        if (isInit) {
            return result.get();
        }
        result.set(checkIsAvailableInner());
        return result.get();
    }
    public static boolean checkIsAvailableInner(){
        String cacheVer = HostInfo.getVersion() + "."+HostInfo.getVerCode() + "->" + BuildConfig.VERSION_CODE;
        if (GlobalConfig.Get_String("cacheVer").equals(cacheVer)){
            preLoadMethod();
            return true;
        }
        for (CoreLoader.XPItemInfo info : CoreLoader.clzInstance.values()){
            if (info.isVersionAvailable && info.NeedMethodInfo != null){
                for (BaseMethodInfo methodInfo : info.NeedMethodInfo.values()){
                    if (methodInfo instanceof CommonMethodInfo) continue;
                    return false;
                }
            }
        }
        preLoadMethod();
        return true;
    }
    private static final AtomicBoolean isLoaded = new AtomicBoolean();
    private static void preLoadMethod(){
        if (!isLoaded.getAndSet(true)){
            for (CoreLoader.XPItemInfo item : CoreLoader.clzInstance.values()){
                for (BaseMethodInfo info : item.NeedMethodInfo.values()){
                    if (info instanceof CommonMethodInfo){
                        item.scanResult.put(info.id, ((CommonMethodInfo) info).methods);
                    }else {
                        item.scanResult.put(info.id,getMethodFromCache(item.id+"_"+info.id));
                    }
                }
            }
        }
    }
    private static final ArrayList<ScannerLink> rootNode = new ArrayList<>();

    private static void CollectLinkInfo(){
        ArrayList<BaseMethodInfo> allFindMethodInfo = new ArrayList<>();
        for (CoreLoader.XPItemInfo item : CoreLoader.clzInstance.values()){
            if (item.NeedMethodInfo != null){
                allFindMethodInfo.addAll(item.NeedMethodInfo.values());
            }
        }
        //编号查找的Link
        int restLink = 0;
        while (restLink != allFindMethodInfo.size()){
            restLink = allFindMethodInfo.size();
            allFindMethodInfo.removeIf(MethodScannerWorker::checkAndAddItemToTree);

        }
    }
    private static boolean checkAndAddItemToTree(BaseMethodInfo info){
        if (info instanceof CommonMethodInfo){
            ScannerLink newNode = new ScannerLink();
            newNode.ID = info.bandToInfo.ItemName+"->"+info.id;
            newNode.LinkingID = new ArrayList<>();
            newNode.Info = info;
            newNode.item = info.bandToInfo;
            rootNode.add(newNode);
            return true;
        }
        if (info instanceof BaseFindMethodInfo){
            if (((BaseFindMethodInfo) info).LinkedToMethodID == null){
                ScannerLink newNode = new ScannerLink();
                newNode.ID = info.bandToInfo.ItemName+"->"+info.id;
                newNode.LinkingID = new ArrayList<>();
                newNode.Info = info;
                newNode.item = info.bandToInfo;
                rootNode.add(newNode);
                return true;
            }else {
                String LinkedID = ((BaseFindMethodInfo) info).LinkedToMethodID;
                for (ScannerLink lnk : rootNode){
                    ScannerLink searchResult = searchNode(lnk,info.bandToInfo.ItemName+"->"+LinkedID);
                    if (searchResult != null){
                        ScannerLink newNode = new ScannerLink();
                        newNode.ID = info.bandToInfo.ItemName+"->"+info.id;
                        newNode.LinkingID = new ArrayList<>();
                        newNode.Info = info;
                        newNode.LinkToID = searchResult;
                        newNode.item = info.bandToInfo;
                        searchResult.LinkingID.add(newNode);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private static ScannerLink searchNode(ScannerLink link,String ID){
        if (link.ID.equals(ID))return link;
        if (link.LinkingID.size() > 0){
            for (ScannerLink childNode : link.LinkingID){
                ScannerLink result = searchNode(childNode,ID);
                if (result != null)return result;
            }
        }
        return null;
    }
    static ArrayList<IDexFinder> IFinders = new ArrayList<>();
    static IDexFinder dexKitFinder;

    @SuppressLint({"ResourceType", "SetTextI18n"})
    public static void doFindMethod(){
        cleanAllCache();
        CollectLinkInfo();

        IDexFinder finder = new DexHelperFinder();
        finder.init(HookEnv.AppApkPath,HookEnv.mLoader);
        IFinders.add(finder);
        IDexFinder dexKitFinder = new DexKitFinder();
        dexKitFinder.init(HookEnv.AppApkPath,HookEnv.mLoader);
        IFinders.add(dexKitFinder);




        new Handler(Looper.getMainLooper()).post(()->{
            Context context = Utils.getTopActivity();
            CommonHookLoaderDialog findDialog = new CommonHookLoaderDialog(context);


            ArrayList<ScannerLink> sortedLinkScannerInfo = new ArrayList<>();
            for (ScannerLink node : rootNode){
                getSortedLinkInfo(sortedLinkScannerInfo,node);
            }
            findDialog.showDialog();
            findDialog.updateProgress(1,sortedLinkScannerInfo.size());

            new Thread(()->{
                int i = 0;
                for (ScannerLink node : sortedLinkScannerInfo){
                    try{
                        findDialog.updateProgress(++i,sortedLinkScannerInfo.size());

                        BaseMethodInfo info = node.Info;
                        Member findResult = findMethod(info);
                        if (findResult == null){

                        }else {
                            info.bandToInfo.scanResult.put(info.id,findResult);
                            if (!(info instanceof CommonMethodInfo)){
                                writeMethodToCache(info.bandToInfo.id+"_"+info.id, (Method) findResult);
                            }
                        }
                    }catch (Throwable e){
                        Utils.ShowToastL(Log.getStackTraceString(e));
                    }
                }
                String cacheVer = HostInfo.getVersion() + "."+HostInfo.getVerCode() + "->" + BuildConfig.VERSION_CODE;
                GlobalConfig.Put_String("cacheVer",cacheVer);
                Utils.PostToMainDelay(()-> Utils.restartSelf(context),500);
            },"QTool_Method_Finder").start();
        });

        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private static Member findMethod(BaseMethodInfo info){
        if (info instanceof CommonMethodInfo){
            return ((CommonMethodInfo) info).methods;
        }
        if (info instanceof FindMethodByName){
            FindMethodByName newNode = (FindMethodByName) info;
            for (IDexFinder finder : IFinders){
                Method[] findResult = finder.findMethodByString(newNode.name);
                for (Method m : findResult){
                    try{
                        Object ret = newNode.checker.onMethod(m);
                        if (ret instanceof Boolean){
                            if ((Boolean) ret)return m;
                        }
                        if (ret instanceof Member){
                            return (Member) ret;
                        }
                    }catch (Throwable th){

                    }
                }
            }

        }
        if (info instanceof FindMethodInvokingMethod){
            FindMethodInvokingMethod newNode = (FindMethodInvokingMethod) info;
            Method linkNode;
            if (newNode.checkMethod != null){
                linkNode = (Method) newNode.checkMethod;
            }else if (newNode.LinkedToMethodID != null){
                linkNode = getMethodFromCache(info.bandToInfo.id + "_" + newNode.LinkedToMethodID);
            }else {
                return null;
            }
            for (IDexFinder finder : IFinders){
                Method[] findResult = finder.findMethodInvoking(linkNode);
                for (Method m : findResult){
                    try{
                        Object ret = newNode.checker.onMethod(m);
                        if (ret instanceof Boolean){
                            if ((Boolean) ret)return m;
                        }
                        if (ret instanceof Member){
                            return (Member) ret;
                        }
                    }catch (Throwable th){

                    }
                }
            }

        }
        if (info instanceof FindMethodsWhichInvokeToTargetMethod){
            FindMethodsWhichInvokeToTargetMethod newNode = (FindMethodsWhichInvokeToTargetMethod) info;
            Method linkNode;
            if (newNode.checkMethod != null){
                linkNode = (Method) newNode.checkMethod;
            }else if (newNode.LinkedToMethodID != null){
                linkNode = getMethodFromCache(info.bandToInfo.id + "_" + newNode.LinkedToMethodID);
            }else {
                return null;
            }
            if (linkNode != null){
                for (IDexFinder finder : IFinders){
                    Method[] findResult = finder.findMethodBeInvoked(linkNode);
                    for (Method m : findResult){
                        try{
                            Object ret = newNode.checker.onMethod(m);
                            if (ret instanceof Boolean){
                                if ((Boolean) ret)return m;
                            }
                            if (ret instanceof Member){
                                return (Member) ret;
                            }
                        }catch (Throwable th){ }
                    }
                }

            }else {
                return null;
            }
        }
        return null;
    }
    private static Method getMethodFromCache(String ID){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("method_info",Context.MODE_PRIVATE);
        String methodInfo = share.getString(ID,null);
        if (methodInfo == null)return null;
        return DescToMethod(methodInfo);
    }
    private static Method DescToMethod(String desc){
        try{
            JSONObject json = new JSONObject(desc);
            Class<?> clz = MClass.loadClass(json.getString("clz"));
            Class<?> returnType = MClass.loadClass(json.getString("retName"));

            JSONArray paramArr = json.getJSONArray("params");
            Class<?>[] paramArrClz = new Class<?>[paramArr.length()];
            for (int i=0;i<paramArr.length();i++){
                paramArrClz[i] = MClass.loadClass(paramArr.getString(i));
            }
            return MMethod.FindMethod(clz,json.getString("name"),returnType,paramArrClz);
        }catch (Exception e){
            XposedBridge.log(Log.getStackTraceString(e));
            return null;
        }
    }
    private static void cleanAllCache(){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("method_info",Context.MODE_PRIVATE);
        share.edit().clear().apply();
    }
    private static void writeMethodToCache(String ID,Method method){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("method_info",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(ID,getMethodDesc(method));
        editor.apply();
    }
    private static String getMethodDesc(Method m){
        try{
            JSONObject newJson = new JSONObject();
            newJson.put("clz",m.getDeclaringClass().getName());
            newJson.put("name",m.getName());
            newJson.put("retName",m.getReturnType().getName());

            JSONArray paramTypes = new JSONArray();
            for (Class<?> clz : m.getParameterTypes()){
                paramTypes.put(clz.getName());
            }
            newJson.put("params",paramTypes);
            return newJson.toString();
        }catch (Exception e){
            XposedBridge.log(Log.getStackTraceString(e));
            return "";
        }

    }
    private static void getSortedLinkInfo(ArrayList<ScannerLink> linkData,ScannerLink link){
        linkData.add(link);
        for (ScannerLink newNode : link.LinkingID){
            getSortedLinkInfo(linkData,newNode);
        }
    }
}
