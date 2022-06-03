package cc.hicore.qtool.XposedInit;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.DexFinder.DexFinder;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.HookEnv;

public class MethodFinder {
    private static boolean NeedFindAll = false;
    private static boolean NeedFindExtra = false;
    private static ArrayList<updateInfo> needUpdateList = new ArrayList<>();
    private static class updateInfo{
        int type;
        String tag;
        String desc;
        String containString;
        Method InvokeMethod;
        String ConnectToTag;
        MethodChecker checker;
    }
    public interface MethodChecker{
        boolean onMethodFound(Method m);
    }
    private static Method findMethodInstance(String containString,MethodChecker checker){
        Method[] findResult = DexFinder.getInstance().findMethodByString(containString);
        for (Method m : findResult){
            if (checker.onMethodFound(m)){
                return m;
            }
        }
        return null;
    }
    private static Method findInvokingMethod(Method beInvokedMethod,MethodChecker checker){
        Method[] findResult = DexFinder.getInstance().findMethodInvoking(beInvokedMethod);
        for (Method m : findResult){
            if (checker.onMethodFound(m)){
                return m;
            }
        }
        return null;
    }
    private static Method findInvokeTargetMethod(Method beInvokedMethod,MethodChecker checker){
        Method[] findResult = DexFinder.getInstance().findMethodInvokeTarget(beInvokedMethod);
        for (Method m : findResult){
            if (checker.onMethodFound(m)){
                return m;
            }
        }
        return null;
    }

    public static boolean NeedLockAndFindDex(){
        String curVer = BuildConfig.VERSION_CODE+"";
        String saveVer = GlobalConfig.Get_String("cache_qtool_ver");
        if (!curVer.equals(saveVer)){
            NeedFindExtra = true;
        }

        String qqVer = HostInfo.getVerCode()+"";
        String saveQQVer = GlobalConfig.Get_String("cache_qq_ver");
        if (!qqVer.equals(saveQQVer)){
            cleanAllCache();
            NeedFindAll = true;
        }

        return NeedFindAll || NeedFindExtra;
    }
    public static void PreLoadDexFindDialogAndLock(Context context){
        if (needUpdateList.size() == 0)return;
        AtomicBoolean locker = new AtomicBoolean();
        new Handler(Looper.getMainLooper()).post(()->{
            LinearLayout mRoot = new LinearLayout(context);
            mRoot.setOrientation(LinearLayout.VERTICAL);

            needUpdateList.sort(Comparator.comparingInt(o -> o.type));

            ArrayList<TextView> finderCacheView = new ArrayList<>();
            for (updateInfo info : needUpdateList){
                TextView findText = new TextView(context);
                findText.setTextColor(Color.BLACK);
                findText.setGravity(Gravity.CENTER);
                findText.setText(info.tag+"("+info.desc+")");
                findText.setTextSize(14);
                findText.setTag(info);
                mRoot.addView(findText);
                finderCacheView.add(findText);
            }
            AlertDialog dialog = new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("QTool方法查找")
                    .setView(mRoot)
                    .setCancelable(false)
                    .create();
            dialog.show();

            new Thread(()->{
                try{
                    for (TextView v : finderCacheView){
                        updateInfo info = (updateInfo) v.getTag();
                        try{
                            Utils.PostToMain(()->{
                                v.setText(info.tag+"("+info.desc+") - 正在查找..");
                                v.setTextColor(Color.BLUE);
                            });
                            Method m = null;
                            if (info.type == 1){
                                m = findMethodInstance(info.containString,info.checker);
                            }else if (info.type == 2){
                                m = findInvokingMethod(info.InvokeMethod,info.checker);
                            }else if (info.type == 3){
                                Method findMethod = findMethodFromCache(info.ConnectToTag);
                                if (findMethod != null){
                                    m = findInvokingMethod(findMethod,info.checker);
                                }
                            }else if (info.type == 4){
                                m = findInvokeTargetMethod(info.InvokeMethod,info.checker);
                            }else if (info.type == 5){
                                Method findMethod = findMethodFromCache(info.ConnectToTag);
                                if (findMethod != null){
                                    m = findInvokeTargetMethod(findMethod,info.checker);
                                }
                            }


                            if (m == null){
                                Utils.PostToMain(()->{
                                    v.setText(info.tag+"("+info.desc+") - 未找到");
                                    v.setTextColor(Color.RED);
                                });
                            }else {
                                Utils.PostToMain(()->{
                                    v.setText(info.tag+"("+info.desc+") - 已找到");
                                    v.setTextColor(Color.GREEN);
                                });
                            }
                            HookEnv.Config.setString("cache_dex_finder_ver",info.tag,getMethodDesc(m));
                        }catch (Exception e){
                            Utils.PostToMain(()->{
                                v.setText(info.tag+"("+info.desc+") - 未找到");
                                v.setTextColor(Color.RED);
                            });
                        }
                    }
                    GlobalConfig.Put_String("cache_qq_ver",""+HostInfo.getVerCode());
                    GlobalConfig.Put_String("cache_qtool_ver", BuildConfig.VERSION_CODE+"");
                }catch (Exception e){

                }finally {
                    locker.getAndSet(true);
                    Utils.PostToMain(()->dialog.setCancelable(true));
                }
            },"QTool_DexFinder").start();
        });

        while (!locker.get()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void NeedReportToFindMethod(String tag,String desc,String containString,MethodChecker checker){
        for (updateInfo newInfo : needUpdateList){
            if (newInfo.tag.equals(tag))return;
        }
        updateInfo newInfo= new updateInfo();
        newInfo.tag = tag;
        newInfo.desc = desc;
        newInfo.containString = containString;
        newInfo.checker = checker;
        newInfo.type = 1;
        needUpdateList.add(newInfo);
    }
    public static void NeedReportToFindMethod(String tag,String desc,Method Invoked,MethodChecker checker){
        for (updateInfo newInfo : needUpdateList){
            if (newInfo.tag.equals(tag))return;
        }
        updateInfo newInfo= new updateInfo();
        newInfo.tag = tag;
        newInfo.desc = desc;
        newInfo.InvokeMethod = Invoked;
        newInfo.checker = checker;
        newInfo.type = 2;
        needUpdateList.add(newInfo);
    }
    public static void NeedReportToFindMethodConnectTag(String tag,String desc,String InvokedMethodTag,MethodChecker checker){
        for (updateInfo newInfo : needUpdateList){
            if (newInfo.tag.equals(tag))return;
        }
        updateInfo newInfo= new updateInfo();
        newInfo.tag = tag;
        newInfo.desc = desc;
        newInfo.ConnectToTag = InvokedMethodTag;
        newInfo.checker = checker;
        newInfo.type = 3;
        needUpdateList.add(newInfo);
    }
    public static void NeedReportToFindMethodBeInvoked(String tag,String desc,Method InvokedMethod,MethodChecker checker){
        for (updateInfo newInfo : needUpdateList){
            if (newInfo.tag.equals(tag))return;
        }
        updateInfo newInfo= new updateInfo();
        newInfo.tag = tag;
        newInfo.desc = desc;
        newInfo.InvokeMethod = InvokedMethod;
        newInfo.checker = checker;
        newInfo.type = 4;
        needUpdateList.add(newInfo);
    }
    public static void NeedReportToFindMethodBeInvokedTag(String tag,String desc,String InvokedMethodTag,MethodChecker checker){
        for (updateInfo newInfo : needUpdateList){
            if (newInfo.tag.equals(tag))return;
        }
        updateInfo newInfo= new updateInfo();
        newInfo.tag = tag;
        newInfo.desc = desc;
        newInfo.ConnectToTag = InvokedMethodTag;
        newInfo.checker = checker;
        newInfo.type = 5;
        needUpdateList.add(newInfo);
    }
    public static Method findMethodFromCache(String tag){
        String desc = HookEnv.Config.getString("cache_dex_finder_ver", tag, "");
        if (TextUtils.isEmpty(desc)){
            return null;
        }
        return DescToMethod(desc);
    }
    private static void cleanAllCache(){
        if (HookEnv.IsMainProcess){
            HookEnv.Config.removeAll("MethodCache");
        }

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
            return null;
        }
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
            return "";
        }

    }
}
