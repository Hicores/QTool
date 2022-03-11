package com.hicore.qtool.XPWork.QQMsgProxy;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.Classes;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isRunInAllProc = false,isDelayInit = false)
public class BaseGuildMsgProxy extends BaseHookItem {
    private static final String TAG = "BaseGuildMsgProxy";
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            PluginMessageProcessor.submit(()->PluginMessageProcessor.onMessage(param.args[0]));
        });
        XPBridge.HookBefore(m[1],param -> {
            if (!param.args[1].getClass().getSimpleName().equals("MessageRecord")){
                PluginMessageProcessor.submit(()->PluginMessageProcessor.onMessage(param.args[1]));

            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null & m[1] != null ;
    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.GuildOnlineMessageProcessor"),"c",
                void.class,new Class[]{Classes.MessageRecord()});
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.api.impl.GuildMessageUtilsApiImpl"),"handleSelfSendMsg",
                void.class,new Class[]{Classes.AppInterface(),Classes.MessageRecord(),Classes.MessageRecord(),int.class});
        return m;
    }
}
