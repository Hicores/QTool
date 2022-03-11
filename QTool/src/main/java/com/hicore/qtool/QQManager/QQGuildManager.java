package com.hicore.qtool.QQManager;

import android.text.TextUtils;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.qtool.HookEnv;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class QQGuildManager {
    public static void Guild_Mute(String GuildID,String UserTinyID,long Time){
        try{
            if (TextUtils.isEmpty(UserTinyID)){
                Object IGpsManager = QQEnvUtils.GetIGpsManager();
                Object ResultProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, (proxy, method, args) -> null);
                MMethod.CallMethod(IGpsManager,"setMemberShutUp",void.class,GuildID,UserTinyID, QQEnvUtils.GetServerTime()+Time,ResultProxy);
            }else {
                Object IGpsManager = QQEnvUtils.GetIGpsManager();
                Object ResultProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, (proxy, method, args) -> null);
                MMethod.CallMethod(IGpsManager,"setGuildShutUp",void.class,GuildID,Time,ResultProxy);
            }
        }catch (Exception e){
            LogUtils.error("Guild_Mute",e);
        }
    }
    public static void Guild_Kick(String GuildID,String UserTinyID,boolean isBlack){
        try{
            Object IGpsManager = QQEnvUtils.GetIGpsManager();
            Object ResultProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, (proxy, method, args) -> null);
            ArrayList<String> list = new ArrayList<>();
            list.add(UserTinyID);
            MMethod.CallMethod(IGpsManager,"kickGuildUsers",void.class,GuildID,list,isBlack,ResultProxy);
        }catch (Exception e){
            LogUtils.error("Guild_Kick",e);
        }
    }
    public static void Guild_Revoke(Object msg){
        try{
            Object RevokeHelper = QQEnvUtils.getBusinessHandler("com.tencent.mobileqq.guild.message.api.impl.GuildRevokeMessageHandler");
            MMethod.CallMethod(RevokeHelper,"a",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")},msg);
        }catch (Exception e){
            LogUtils.error("Guild_Revoke",e);
        }
    }
}
