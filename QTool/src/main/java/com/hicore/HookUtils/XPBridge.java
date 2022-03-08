package com.hicore.HookUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class XPBridge {
    public static void HookBefore(Method m,BeforeHook before){
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                before.onBefore(param);
            }
        });
    }
    public static void HookAfter(Method m,AfterHook after){
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                after.onAfter(param);
            }
        });
    }
    public static void HookBeforeOnce(Method m,BeforeHook before){
        AtomicReference<XC_MethodHook.Unhook> cacheUnHook = new AtomicReference<>();
        cacheUnHook.set(XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                before.onBefore(param);
                cacheUnHook.get().unhook();
            }
        }));
    }
    public static void HookAfterOnce(Method m,AfterHook after){
        AtomicReference<XC_MethodHook.Unhook> cacheUnHook = new AtomicReference<>();
        cacheUnHook.set(XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                after.onAfter(param);
                cacheUnHook.get().unhook();
            }
        }));
    }
    public interface BeforeHook{
        void onBefore(XC_MethodHook.MethodHookParam param) throws Throwable;
    }
    public interface AfterHook{
        void onAfter(XC_MethodHook.MethodHookParam param) throws Throwable;
    }
}
