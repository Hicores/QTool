package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isRunInAllProc = false, isDelayInit = false)
public class BaseChatPie extends BaseHookItem {
    public static Object cacheChatPie;

    @Override
    public boolean startHook() throws Throwable {
        Method hookMethod = getMethod();
        XPBridge.HookAfter(hookMethod, param -> {
            cacheChatPie = param.thisObject;
            HookEnv.AppInterface = MField.GetFirstField(cacheChatPie, MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"));
            HookEnv.SessionInfo = MField.GetFirstField(cacheChatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));

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

    public static Method getMethod() {
        Method m;
        if (HostInfo.getVerCode() > 6440) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "s", void.class, new Class[0]);
        } else if (HostInfo.getVerCode() > 5870) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "r", void.class, new Class[0]);
        } else if (HostInfo.getVerCode() > 5570) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "q", void.class, new Class[0]);
        } else {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "f", void.class, new Class[0]);
        }
        return m;
    }
}
