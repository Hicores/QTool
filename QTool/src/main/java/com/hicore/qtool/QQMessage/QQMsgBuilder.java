package com.hicore.qtool.QQMessage;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class QQMsgBuilder {
    private static final String TAG = "QQMsgBuilder";
    public static Object build_struct(String xml){
        try{
            Method BuildStructMsg = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.structmsg.TestStructMsg"),"a",
                    MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),new Class[]{String.class});
            Object msgData = BuildStructMsg.invoke(null,new Object[]{xml});
            return msgData;
        } catch (Throwable th) {
            LogUtils.error(TAG, "build_struct:\n"+th);
            return null;
        }
    }
    public static Object build_arkapp(String json){
        try{
            Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage","fromAppXml",
                    boolean.class,new Class[]{String.class});
            Constructor<?> cons = MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage").getConstructor();
            Object _ArkAppMsg = cons.newInstance();
            med.invoke(_ArkAppMsg, json);
            return _ArkAppMsg;
        } catch (Throwable th) {
            LogUtils.error("TAG", "build_json:\n"+th);
            return null;
        }
    }
}
