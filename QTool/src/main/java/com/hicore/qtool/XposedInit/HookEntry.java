package com.hicore.qtool.XposedInit;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static StartupParam cacheParam;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (cacheParam == null){
            XposedBridge.log("[QTool]initZygote may not be invoke, please check your Xposed Framework!");
            return;
        }
        String ModelPath = cacheParam.modulePath;



    }
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        cacheParam = startupParam;
    }
    private static class FixSubClassLoader extends ClassLoader{
        ClassLoader parentLoader;
        ClassLoader childLoader;

        Method findClass;
        {
            try {
                findClass = childLoader.getClass().getDeclaredMethod("findClass", String.class);
                findClass.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        protected FixSubClassLoader(ClassLoader parent,ClassLoader child) {
            super(parent);
            parentLoader = parent;
            childLoader = child;
        }


        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try{
                Class clz = (Class) findClass.invoke(childLoader,name);
                if (clz != null){
                    return clz;
                }
            }catch (Exception notFound){

            }
            return super.loadClass(name);
        }
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try{
                Class clz = (Class) findClass.invoke(childLoader,name);
                if (clz != null){
                    return clz;
                }
            }catch (Exception notFound){ }
            return super.loadClass(name, resolve);
        }

    }
    private static class FixSubLoadClass{
        public static void loadPackage(XC_LoadPackage.LoadPackageParam lpparam){
            if (lpparam.packageName.equals("com.tencent.mobileqq")){
                HookEnv.IsMainProcess = lpparam.processName.equals("com.tencent.mobileqq");
                HookEnv.ProcessName = lpparam.processName;
                HookEnv.mLoader = lpparam.classLoader;
                HookEnv.AppApkPath = lpparam.appInfo.processName;

                EzXHelperInit.INSTANCE.initHandleLoadPackage(lpparam);

                EnvHook.HookForContext();
            }
        }
        public static void loadZygote(StartupParam startupParam){
            HookEnv.ToolApkPath = startupParam.modulePath;
            EzXHelperInit.INSTANCE.initZygote(startupParam);
        }
    }
}
