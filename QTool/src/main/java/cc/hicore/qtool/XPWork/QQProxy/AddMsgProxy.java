package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.JavaPlugin.Controller.PluginController;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

/*
Hook For AddAndSendMessage
在发送 文本,回复时会调用
 */
@HookItem(isDelayInit = false, isRunInAllProc = false)
public class AddMsgProxy extends BaseHookItem {

    @Override
    public boolean startHook() throws Throwable {
        Method hookMethod = getMethod();
        XPBridge.HookBefore(hookMethod, param -> {
            Object chatMsg = param.args[0];
            PluginController.WaitForgetMsgInvoke(chatMsg);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    public Method getMethod() {
        Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a", void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")
        });
        return InvokeMethod;
    }
}
