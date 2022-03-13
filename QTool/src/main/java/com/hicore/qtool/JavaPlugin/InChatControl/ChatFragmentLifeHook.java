package com.hicore.qtool.JavaPlugin.InChatControl;

import android.os.Handler;
import android.os.Looper;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.Utils.Utils;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class ChatFragmentLifeHook extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {

        Method[] hookMethod = getHookMethod();
        XPBridge.HookAfter(hookMethod[0],param -> {
            onShow();
        });
        XPBridge.HookAfter(hookMethod[1],param -> {
            onHide();
        });
        return true;
    }
    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        Method[] m = getHookMethod();
        return m[0] != null && m[1] != null;
    }
    private void onShow(){
        new Handler(Looper.getMainLooper())
                .post(()->{
                   FloatWindowControl.onShowEvent(true);
                });
    }
    private void onHide(){
        new Handler(Looper.getMainLooper())
                .post(()->{
                    FloatWindowControl.onShowEvent(false);
                });
    }
    public Method[] getHookMethod(){
        Method[] m = new Method[3];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),"U",new Class[0]);
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),"R",new Class[0]);
        return m;
    }
}
