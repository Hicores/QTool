package cc.hicore.ReflectUtils;

import java.lang.reflect.Member;
import java.util.concurrent.atomic.AtomicReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class XPBridge {
    public static void HookBefore(Member m, BeforeHook before,int pro) {
        XposedBridge.hookMethod(m, new XC_MethodHook(pro) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                before.onBefore(param);
            }
        });
    }
    public static void HookBefore(Member m, BeforeHook before) {
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                before.onBefore(param);
            }
        });
    }
    public static void HookAfter(Member m, AfterHook after) {
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                after.onAfter(param);
            }
        });
    }

    public static void HookBeforeOnce(Member m, BeforeHook before) {
        AtomicReference<XC_MethodHook.Unhook> cacheUnHook = new AtomicReference<>();
        cacheUnHook.set(XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Unhook unhook = cacheUnHook.getAndSet(null);
                if (unhook != null) {
                    unhook.unhook();
                    before.onBefore(param);
                }
            }
        }));
    }

    public static void HookAfterOnce(Member m, AfterHook after) {
        AtomicReference<XC_MethodHook.Unhook> cacheUnHook = new AtomicReference<>();
        cacheUnHook.set(XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Unhook unhook = cacheUnHook.getAndSet(null);
                if (unhook != null) {
                    unhook.unhook();
                    after.onAfter(param);
                }

            }
        }));
    }

    public interface BeforeHook {
        void onBefore(XC_MethodHook.MethodHookParam param) throws Throwable;
    }

    public interface AfterHook {
        void onAfter(XC_MethodHook.MethodHookParam param) throws Throwable;
    }
}
