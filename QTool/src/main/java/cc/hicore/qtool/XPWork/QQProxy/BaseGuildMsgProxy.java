package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isRunInAllProc = false, isDelayInit = false)
public class BaseGuildMsgProxy extends BaseHookItem {
    private static final String TAG = "BaseGuildMsgProxy";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0], param -> {
            PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMessage(param.args[0]));
        });
        XPBridge.HookBefore(m[1], param -> {
            if (!param.args[1].getClass().getSimpleName().equals("MessageRecord")) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMessage(param.args[1]));

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
        return m[0] != null & m[1] != null;
    }

    public Method[] getMethod() {
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.GuildOnlineMessageProcessor"), null,
                void.class, new Class[]{Classes.MessageRecord()});
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.api.impl.GuildMessageUtilsApiImpl"), "handleSelfSendMsg",
                void.class, new Class[]{Classes.AppInterface(), Classes.MessageRecord(), Classes.MessageRecord(), int.class});
        return m;
    }
}
