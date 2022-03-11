package com.hicore.qtool.HookInvokeEarly;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

//修复有时挂钩不生效的问题
@HookItem(isDelayInit = false,isRunInAllProc = false)
public class WidgetInvoker extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeMenuConfigBean");
        Method mFound = null;
        for(Method m : clz.getDeclaredMethods())
        {
            if(m.getReturnType().isArray() && m.getParameterCount()==0) {
                mFound = m;
            }
        }
        if (mFound != null){
            mFound.invoke(clz.newInstance());
        }
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return true;
    }
}
