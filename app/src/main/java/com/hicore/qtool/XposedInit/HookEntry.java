package com.hicore.qtool.XposedInit;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.tencent.mobileqq")){
            HookEnv.IsMainProcess = lpparam.processName.equals("com.tencent.mobileqq");
            HookEnv.ProcessName = lpparam.processName;
            HookEnv.mLoader = lpparam.classLoader;
            HookEnv.AppApkPath = lpparam.appInfo.processName;

            EnvHook.HookForContext();
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        HookEnv.ToolApkPath = startupParam.modulePath;
    }
}
