package com.hicore.qtool.XposedInit;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQTools.ContUtil;

import java.lang.reflect.Method;

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
        FixSubLoadClass.loadZygote(cacheParam);
        FixSubLoadClass.loadPackage(lpparam);

    }
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        cacheParam = startupParam;
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
