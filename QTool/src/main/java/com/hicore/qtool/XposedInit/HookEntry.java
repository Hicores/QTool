package com.hicore.qtool.XposedInit;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.hicore.LogUtils.LogUtils;
import com.hicore.Utils.DataUtils;
import com.hicore.qtool.HookEnv;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.InMemoryDexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
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

        XposedBridge.log("[QTool]Load from "+lpparam.processName);
        FixRootClassLoader();
        FixSubLoadClass.loadZygote(cacheParam);
        FixSubLoadClass.loadPackage(lpparam);
        /*

        boolean isUseDefLoadMode = new File(lpparam.appInfo.dataDir+"/def").exists();
        if (isUseDefLoadMode){
            FixSubLoadClass.loadZygote(cacheParam);
            FixSubLoadClass.loadPackage(lpparam);
        }else {
            byte[] dexBuffer = null;
            ZipInputStream zInp = new ZipInputStream(new FileInputStream(cacheParam.modulePath));
            ZipEntry entry;
            while ((entry = zInp.getNextEntry()) != null){
                if (entry.getName().equals("classes.dex")){
                    dexBuffer = DataUtils.readAllBytes(zInp);
                    zInp.close();
                    break;
                }
            }
            if (dexBuffer != null && dexBuffer[0] == 'd' && dexBuffer[1] == 'e'&& dexBuffer[2] == 'x'){
                FixSubClassLoader subLoader = new FixSubClassLoader(HookEntry.class.getClassLoader());
                InMemoryDexClassLoader memoryLoader = new InMemoryDexClassLoader(ByteBuffer.wrap(dexBuffer),subLoader);
                subLoader.setChild(memoryLoader);

                Class<?> clzEntry = memoryLoader.loadClass("com.hicore.qtool.XposedInit.HookEntry$FixSubLoadClass");
                Method m = clzEntry.getMethod("loadZygote",subLoader.loadClass("de.robv.android.xposed.IXposedHookZygoteInit$StartupParam"));
                m.invoke(null,cacheParam);
                m = clzEntry.getDeclaredMethod("loadPackage",subLoader.loadClass("de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam"));
                m.invoke(null,lpparam);
            }else {
                FixSubLoadClass.loadZygote(cacheParam);
                FixSubLoadClass.loadPackage(lpparam);
            }
        }

         */


    }
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        cacheParam = startupParam;
    }
    public static class FixSubClassLoader extends ClassLoader{
        ClassLoader parentLoader;
        ClassLoader childLoader;
        Method findClass;

        ClassLoader bootClassLoader = ClassLoader.getSystemClassLoader();
        protected FixSubClassLoader(ClassLoader parent) {
            super(parent);
            parentLoader = parent;
        }
        private void setChild(ClassLoader child){
            childLoader = child;
            try {
                findClass = childLoader.getClass().getDeclaredMethod("findClass", String.class);
                findClass.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try{
                return bootClassLoader.loadClass(name);
            }catch (Exception e){

            }
            return tryLoad(name);
        }
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try{
                return bootClassLoader.loadClass(name);
            }catch (Exception e){

            }
            return tryLoad(name);
        }
        private Class tryLoad(String name)throws ClassNotFoundException{
            try{
                if (childLoader != null){
                    Class clz = (Class) findClass.invoke(childLoader,name);
                    if (clz != null){
                        return clz;
                    }
                }

            }catch (Exception notFound){

            }
            return parentLoader.loadClass(name);
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
    private static void FixRootClassLoader(){

        try {
            ClassLoader loader = HookEntry.class.getClassLoader();
            Method m = loader.getClass().getDeclaredMethod("findClass", String.class);
            XposedBridge.hookMethod(ClassLoader.class.getMethod("loadClass", String.class, boolean.class), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String Name = (String) param.args[0];
                    if (Name.startsWith("hct.")){
                        Class<?> clz = (Class<?>) m.invoke(loader,Name);
                        if (clz != null)param.setResult(clz);
                    }
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
