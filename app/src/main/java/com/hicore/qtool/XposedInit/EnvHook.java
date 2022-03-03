package com.hicore.qtool.XposedInit;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class EnvHook {
    public static void HookForContext(){
        //由于很多环境的初始化都需要Context来进行,所有这里选择直接Hook获取Context再进行初始化
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", HookEnv.mLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                HookEnv.AppContext = (Context) param.args[0];
                //优先初始化Path
                ExtraPathInit.InitPath();
                HookForDelayDialog();
                if (HookEnv.ExtraDataPath != null){
                    //在外部数据路径不为空且有效的情况下才加载Hook,防止意外导致的设置项目全部丢失
                    HookLoader.SearchAndLoadAllHook();
                }

            }
        });
    }
    private static void HookForDelayDialog(){
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.startup.step.LoadData", HookEnv.mLoader, "doStep", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (HookEnv.ExtraDataPath == null) ExtraPathInit.ShowPathSetDialog();
                else HookLoader.CallAllDelayHook();
            }
        });
    }
}
