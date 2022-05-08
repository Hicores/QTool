package cc.hicore.qtool.CrashHandler;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ByPassInnerHook {
    static XC_MethodHook.Unhook unhook;
    static ClassLoader mLoader = XposedBridge.class.getClassLoader();
    public static void StartByPass(){
        unhook = XposedHelpers.findAndHookMethod(Class.class, "getClassLoader", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                ClassLoader loader = (ClassLoader) param.getResult();
                if (loader == mLoader){
                    unhook.unhook();
                    unhook = null;
                    param.setResult(null);
                }
            }
        });
    }
    public static void EndBypass(){
        if (unhook != null){
            unhook.unhook();
            unhook = null;
        }
    }

}
