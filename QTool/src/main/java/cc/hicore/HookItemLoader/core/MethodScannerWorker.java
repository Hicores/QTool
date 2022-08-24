package cc.hicore.HookItemLoader.core;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.DexFinder.DexFinder;
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
import cc.hicore.qtool.XposedInit.HookEntry;
import cc.hicore.qtool.XposedInit.HostInfo;
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
    @SuppressLint({"ResourceType", "SetTextI18n"})
    public static void doFindMethod(){
        cleanAllCache();
        CollectLinkInfo();
        new Handler(Looper.getMainLooper()).postDelayed(()->{
            Context context = Utils.getTopActivity();

            ScrollView sc = new ScrollView(context);
            sc.setBackgroundColor(Color.WHITE);
            LinearLayout mRoot = new LinearLayout(context);
            mRoot.setOrientation(LinearLayout.VERTICAL);
            sc.addView(mRoot);
            Dialog dialog = new Dialog(context,3);
            dialog.setCancelable(false);
            dialog.setContentView(sc);


            ArrayList<ScannerLink> sortedLinkScannerInfo = new ArrayList<>();
            for (ScannerLink node : rootNode){
                getSortedLinkInfo(sortedLinkScannerInfo,node);
            }
            ArrayList<TextView> nodeList = new ArrayList<>();
            for (ScannerLink node : sortedLinkScannerInfo){
                TextView view = new TextView(context);
                if (node.Info instanceof CommonMethodInfo){
                    view.setVisibility(View.GONE);
                }
                view.setGravity(Gravity.CENTER);
                view.setText(node.ID);
                view.setTextSize(16);
                view.setTextColor(Color.BLACK);
                view.setTag(node);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                param.topMargin = Utils.dip2px(context,12);
                mRoot.addView(view,param);
                nodeList.add(view);

            }
            dialog.show();
            new Thread(()->{
                for (TextView nodeView : nodeList){
                    ScannerLink node = (ScannerLink) nodeView.getTag();
                    Utils.PostToMain(()->nodeView.setTextColor(Color.BLUE));
                    try{
                        BaseMethodInfo info = node.Info;
                        Member findResult = findMethod(info);
                        if (findResult == null){
                            Utils.PostToMain(()->nodeView.setTextColor(Color.RED));
                        }else {
                            info.bandToInfo.scanResult.put(info.id,findResult);
                            Utils.PostToMain(()->{
                                nodeView.setTextColor(Color.GREEN);
                                SpannableString text = new SpannableString(node.ID + "\n"+
                                        "("+findResult.getDeclaringClass().getName()+"."+findResult.getName()+")");
                                text.setSpan(new AbsoluteSizeSpan(Utils.dip2px(context,12)),node.ID.length()+1,text.length(),0);
                                text.setSpan(new ForegroundColorSpan(Color.GRAY),node.ID.length()+1,text.length(),0);
                                nodeView.setText(text);
                            });
                            if (!(info instanceof CommonMethodInfo)){
                                writeMethodToCache(info.bandToInfo.id+"_"+info.id, (Method) findResult);
                            }
                        }
                    }catch (Throwable e){
                        Utils.ShowToastL(Log.getStackTraceString(e));
                        Utils.PostToMain(()->nodeView.setTextColor(Color.RED));
                    }
                }
                String cacheVer = HostInfo.getVersion() + "."+HostInfo.getVerCode() + "->" + BuildConfig.VERSION_CODE;
                GlobalConfig.Put_String("cacheVer",cacheVer);
                Utils.ShowToastL("重启QQ以正常使用模块");
            },"QTool_Method_Finder").start();
        },3000);

    }
    private static Member findMethod(BaseMethodInfo info){
        if (info instanceof CommonMethodInfo){
            return ((CommonMethodInfo) info).methods;
        }
        if (info instanceof FindMethodByName){
            FindMethodByName newNode = (FindMethodByName) info;
            Method[] findResult = DexFinder.getInstance(HookEnv.AppApkPath).findMethodByString_DexKit(newNode.name);
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
            //如果DexKit未找到,则尝试使用DexFinder来进行查找
            findResult = DexFinder.getInstance(HookEnv.AppApkPath).findMethodByString(newNode.name);
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
            Method[] findResult = DexFinder.getInstance(HookEnv.AppApkPath).findMethodInvoking(linkNode);
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
                Method[] findResult = DexFinder.getInstance(HookEnv.AppApkPath).findMethodBeInvoked_DexKit(linkNode);
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
                findResult = DexFinder.getInstance(HookEnv.AppApkPath).findMethodBeInvoked(linkNode);
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
            }else {
                return null;
            }
        }
        return null;
    }
    private static Method getMethodFromCache(String ID){
        String s = HookEnv.Config.getString("method_info",ID,null);
        if (s == null)return null;
        return DescToMethod(s);
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
        HookEnv.Config.removeAll("method_info");
    }
    private static void writeMethodToCache(String ID,Method method){
        HookEnv.Config.setString("method_info",ID,getMethodDesc(method));
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
