package cc.hicore.qtool.SubLoader;

import android.provider.Settings;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import cc.hicore.Utils.DataUtils;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SubIniter {
    public static boolean initSubModule(IXposedHookZygoteInit.StartupParam initZygote, XC_LoadPackage.LoadPackageParam sParam){
        String subName = DataUtils.getStrMD5(Settings.Secure.ANDROID_ID);
        String path = HookEnv.AppPath + "/files/"+subName;
        String modulePath = initZygote.modulePath;
        if (!new File(path).exists())return false;
        try{
            SubClassLoader subLoader = new SubClassLoader(path,"",SubIniter.class.getClassLoader());
            Method findClass = ClassLoader.class.getDeclaredMethod("findClass", String.class);
            findClass.setAccessible(true);
            Class<?> clz = (Class<?>) findClass.invoke(subLoader,"cc.hicore.qtool.XposedInit.HookEntry$FixSubLoadClass");
            Method m1 = clz.getMethod("loadPackage",subLoader.loadClass("de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam"),ClassLoader.class);
            Method m2 = clz.getMethod("loadZygote",subLoader.loadClass("de.robv.android.xposed.IXposedHookZygoteInit$StartupParam"));
            initZygote.modulePath = path;
            m2.invoke(null,initZygote);
            sParam.classLoader = new SubBridgeClassLoader();
            m1.invoke(null,sParam,sParam.classLoader);
            return true;
        }catch (Throwable e){
            initZygote.modulePath = modulePath;
            XposedBridge.log("SubModule_Load_Failed:"+ Log.getStackTraceString(e));
            HookEnv.loadFailed.append(Log.getStackTraceString(e));
            return false;
        }
    }
}
