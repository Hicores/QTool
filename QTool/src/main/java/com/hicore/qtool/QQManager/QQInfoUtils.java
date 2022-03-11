package com.hicore.qtool.QQManager;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;

import java.util.ArrayList;

public class QQInfoUtils {
    private static final String TAG = "QQInfoUtils";
    public static String getCurrentUin(){
        try{
            Object AppRuntime = getAppRuntime();
            return MMethod.CallMethod(AppRuntime,"getCurrentAccountUin",String.class);
        }catch (Exception e){
            LogUtils.fetal_error(TAG,e);
            return "";
        }
    }
    public static String getCurrentTinyID(){
        return null;
    }
    public static ArrayList<FriendInfo> getFriendList(){
        return null;
    }
    public static String getSkey(){
        return null;
    }
    public static class FriendInfo{
        public String Uin;
        public String Name;
        public int GroupID;
        public Object source;
    }
    public static Object getAppRuntime() throws Exception {
        Object sApplication = MMethod.CallMethod(null, MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"),
                "getApplication",MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"),new Class[0]);
        return MMethod.CallMethod(sApplication,"getRuntime",MClass.loadClass("mqq.app.AppRuntime"));
    }
    public static void sendLike(String Uin){

    }
}
