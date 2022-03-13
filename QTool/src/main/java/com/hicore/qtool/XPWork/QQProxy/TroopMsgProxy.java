package com.hicore.qtool.XPWork.QQProxy;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isRunInAllProc = false,isDelayInit = false)
public class TroopMsgProxy extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {
        Method m = getMethod();
        XPBridge.HookBefore(m, param -> {
            Object ChatMsg = param.args[2];

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
        Method m = MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy","a", void.class,new Class[]{
                String.class,
                int.class,
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                boolean.class
        });
        return m;
    }
}
