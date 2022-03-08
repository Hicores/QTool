package com.hicore.qtool.XPWork.DebugSetInject;

import android.content.IntentFilter;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class DebugSetHook extends BaseHookItem {
    @Override
    public String getTag() {
        return super.getTag();
    }

    @Override
    public boolean startHook() {
        return super.startHook();
    }

    @Override
    public boolean isEnable() {
        return super.isEnable();
    }

    @Override
    public String getErrorInfo() {
        return "No Info";
    }

    @Override
    public boolean check() {
        return super.check();
    }

    @Override
    public boolean isLoaded() {
        return super.isLoaded();
    }
    public Method getHookMethod(){
        return null;
    }
}
