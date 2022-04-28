package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isRunInAllProc = false, isDelayInit = false)
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

    public Method getMethod() {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy", "a", void.class, new Class[]{
                String.class,
                int.class,
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                boolean.class
        });
        return m;
    }
}
