package com.hicore.qtool.QQManager;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;

public class QQTicketManager {
    public static Object GetTicketManager() throws Exception {
        return MMethod.CallMethod(QQEnvUtils.getAppRuntime(),"getManager", MClass.loadClass("mqq.manager.Manager"),new Class[]{int.class},2);
    }
    public static String getSkey()
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getSkey",String.class,new Class[]{String.class}, QQEnvUtils.getCurrentUin());
        } catch (Exception e) {
            LogUtils.error("GetSkey",e);
            return "";
        }
    }
    public static String getPsKey(String Domain)
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getPskey",String.class,new Class[]{String.class,String.class}, QQEnvUtils.getCurrentUin(),Domain);
        } catch (Exception e) {
            LogUtils.error("GetPsKey",e);
            return "";
        }
    }
    public static String getG_TK(String Url){
        String p_skey = getPsKey(Url);
        int hash = 5381;
        for(int i = 0; i < p_skey.length(); ++i)
        {
            hash += (hash << 5) + p_skey.charAt(i);
        }
        return String.valueOf(hash & 0x7fffffff);
    }
    public static String getSuperKey()
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getSuperkey",String.class,new Class[]{String.class}, QQEnvUtils.getCurrentUin());
        } catch (Exception e) {
            LogUtils.error("GetSuperKey",e);
            return "";
        }
    }
    public static String getPt4Token(String Domain)
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getPt4Token",String.class,new Class[]{String.class,String.class}, QQEnvUtils.getCurrentUin(),Domain);
        } catch (Exception e) {
           LogUtils.error("getPt4Token",e);
           return "";
        }
    }
    public static String getBKN()
    {
        int hash = 5381;
        String Skey = getSkey();
        byte[] b = Skey.getBytes();
        for (int i = 0, len = b.length; i < len; ++i)
            hash += (hash << 5) + (int)b[i];
        return ""+(hash & 2147483647);
    }
}
