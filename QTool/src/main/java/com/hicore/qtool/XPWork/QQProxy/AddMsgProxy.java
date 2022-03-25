package com.hicore.qtool.XPWork.QQProxy;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.JavaPlugin.Controller.PluginController;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

/*
Hook For AddAndSendMessage
在发送 文本,回复时会调用
 */
@HookItem(isDelayInit = false,isRunInAllProc = false)
public class AddMsgProxy extends BaseHookItem {

    @Override
    public boolean startHook() throws Throwable {
        Method hookMethod = getMethod();
        XPBridge.HookBefore(hookMethod,param -> {
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
    public Method getMethod(){
        Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade","a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")
        });
        return InvokeMethod;
    }
}
