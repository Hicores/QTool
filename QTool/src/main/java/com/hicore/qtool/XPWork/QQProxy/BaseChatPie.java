package com.hicore.qtool.XPWork.QQProxy;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XposedInit.HostInfo;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isRunInAllProc = false,isDelayInit = false)
public class BaseChatPie extends BaseHookItem {
    public static Object cacheChatPie;
    @Override
    public boolean startHook() throws Throwable {
        Method hookMethod = getMethod();
        XPBridge.HookAfter(hookMethod,param -> {
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
    public Method getMethod(){
        Method m;
        if (HostInfo.getVerCode() > 6440){
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "s",void.class,new Class[0]);
        }else if (HostInfo.getVerCode() > 5870){
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "r",void.class,new Class[0]);
        }else if (HostInfo.getVerCode() > 5570){
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "q",void.class,new Class[0]);
        }else{
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "f",void.class,new Class[0]);
        }
        return m;
    }
}
