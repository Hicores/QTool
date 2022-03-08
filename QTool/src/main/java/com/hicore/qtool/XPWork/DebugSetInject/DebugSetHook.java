package com.hicore.qtool.XPWork.DebugSetInject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.translation.UiTranslationStateCallback;

import com.hicore.HookItem;
import com.hicore.HookUtils.XPBridge;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.InjectRes;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.Utils.Utils;
import com.hicore.qtool.R;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class DebugSetHook extends BaseHookItem {
    private static final String TAG = "DEBUG_SET_INJECT_HOOK";
    @Override
    public String getTag() {
        return super.getTag();
    }

    @Override
    public boolean startHook() {
        Method hookMethod = getHookMethod();
        XPBridge.HookAfter(hookMethod,param ->{
            ViewGroup group = (ViewGroup) param.args[1];
            Context context = group.getContext();
            InjectRes.StartInject(context);

            LogUtils.debug(TAG,String.valueOf(group));


        });
        return true;
    }
    @Override
    public boolean isEnable() {
        return true;
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
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.settings.message.AssistantSettingFragment"),
                "doOnCreateView",void.class,new Class[]{
                        LayoutInflater.class, ViewGroup.class, Bundle.class
        });
        return m;
    }
}
