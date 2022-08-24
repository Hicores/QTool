package cc.hicore.qtool.XposedInit;


import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;

import java.lang.reflect.Field;

import cc.hicore.ConfigUtils.BeforeConfig;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.SubLoader.SubIniter;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static StartupParam cacheParam;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.isFirstApplication) {
            if (cacheParam == null) {
                XposedBridge.log("[QTool]initZygote may not be invoke, please check your Xposed Framework!");
                return;
            }
            HookEnv.isInSubMode = false;
            HookEnv.AppPath = lpparam.appInfo.dataDir;
            HookEnv.mLoader = lpparam.classLoader;
            /*
            if (BeforeConfig.getBoolean("Enable_SubMode")){
                if (BeforeConfig.getInt("Enable_VerCode") != BuildConfig.VERSION_CODE){
                    BeforeConfig.putBoolean("Enable_SubMode",false);
                    BeforeConfig.putInt("Enable_VerCode", 0);
                }else {
                    if (SubIniter.initSubModule(cacheParam,lpparam))return;
                }
            }

             */

            XposedBridge.log("[QTool]Load from normal mode.");

            FixSubLoadClass.loadZygote(cacheParam);
            FixSubLoadClass.loadPackage(lpparam,null);
        }


    }

    @Override
    public void initZygote(StartupParam startupParam) {
        cacheParam = startupParam;
    }

    public static class FixSubLoadClass {
        public static void loadPackage(XC_LoadPackage.LoadPackageParam lpparam,ClassLoader subClassLoader) {
            if (lpparam.packageName.equals("com.tencent.mobileqq")) {
                HookEnv.AppPath = lpparam.appInfo.dataDir;
                HookEnv.SubClassLoader = subClassLoader;
                HookEnv.IsMainProcess = lpparam.processName.equals("com.tencent.mobileqq");
                HookEnv.ProcessName = lpparam.processName;
                HookEnv.mLoader = lpparam.classLoader;
                HookEnv.AppApkPath = lpparam.appInfo.sourceDir;
                XposedBridge.log("startup module from:"+HookEnv.ToolApkPath);

                EzXHelperInit.INSTANCE.initHandleLoadPackage(lpparam);

                EnvHook.HookForContext();
            }
        }
        public static void loadZygote(StartupParam startupParam) {
            HookEnv.ToolApkPath = startupParam.modulePath;
            EzXHelperInit.INSTANCE.initZygote(startupParam);
        }
    }
    private static void InjectClassLoader() {
        try {
            ClassLoader currentLoader = HookEntry.class.getClassLoader();
            Field parentF = ClassLoader.class.getDeclaredField("parent");
            parentF.setAccessible(true);
            ClassLoader parent = (ClassLoader) parentF.get(currentLoader);
            MyFixClassLoader newFixLoader = new MyFixClassLoader(parent);
            parentF.set(currentLoader, newFixLoader);
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

    private static class MyFixClassLoader extends ClassLoader {
        protected MyFixClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }
}
