package cc.hicore.qtool.XposedInit;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;

public class MethodFinder {
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
}
