package com.hicore.qtool.XposedInit;

import static com.hicore.qtool.HookEnv.moduleLoader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.qtool.BuildConfig;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.ingestion.models.Device;
import com.microsoft.appcenter.ingestion.models.WrapperSdk;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class EnvHook {
    private static final String TAG = "EnvHook";
    public static void HookForContext(){
        //由于很多环境的初始化都需要Context来进行,所有这里选择直接Hook获取Context再进行初始化
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", HookEnv.mLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (HookEnv.IsMainProcess){
                    XposedBridge.log("[QTool]BaseHook Start");
                }

                long timeStart = System.currentTimeMillis();
                try{
                    HookEnv.Application = (Application) param.thisObject;
                    HookEnv.AppContext = (Context) param.args[0];

                    //取代QQ的classLoader防止有一些框架传递了不正确的classLoader
                    HookEnv.mLoader = param.thisObject.getClass().getClassLoader();

                    ClassLoader fixLoader = EnvHook.class.getClassLoader().getParent();
                    if (fixLoader instanceof HookEntry.FixSubClassLoader){
                        HookEnv.fixLoader = (HookEntry.FixSubClassLoader) fixLoader;
                        HookEnv.fixLoader.addHostLoader(HookEnv.mLoader);
                    }
                    moduleLoader = EnvHook.class.getClassLoader();

                    //优先初始化Path
                    ExtraPathInit.InitPath();


                    //然后注入资源
                    EzXHelperInit.INSTANCE.initAppContext(HookEnv.AppContext,false,true);
                    ResUtils.StartInject(HookEnv.AppContext);
                    //然后进行延迟Hook,同时如果目录未设置的时候能弹出设置界面
                    HookForDelay();
                    if (HookEnv.ExtraDataPath != null){

                        HostInfo.Init();
                        InitActivityProxy();
                        //在外部数据路径不为空且有效的情况下才加载Hook,防止意外导致的设置项目全部丢失
                        HookLoader.SearchAndLoadAllHook();
                    }
                }finally {
                    if (HookEnv.IsMainProcess){
                        XposedBridge.log("[QTool]BaseHook Init End,time cost:"+(System.currentTimeMillis() - timeStart)+"ms");
                    }

                }



            }
        });
    }
    private static void patchAppCenter(){

    }
    private static void InitAppCenter(){
        try {
            if (!HookEnv.IsMainProcess)return;
            AppCenter.start((Application) HookEnv.Application, "6f119935-286d-4a6b-b9e4-c9f18513dbf8",
                    Analytics.class, Crashes.class);
        } catch (Exception e) {
            LogUtils.error("AppCenter","Init Failed:\n"+ Log.getStackTraceString(e));
        }


    }
    private static void InitActivityProxy(){
        EzXHelperInit.INSTANCE.initActivityProxyManager(BuildConfig.APPLICATION_ID,"com.tencent.mobileqq.activity.AboutActivity", moduleLoader, HookEnv.mLoader);
        EzXHelperInit.INSTANCE.initSubActivity();
    }
    private static void HookForDelay(){
        if (HookEnv.IsMainProcess){
            XPBridge.HookBeforeOnce(XposedHelpers.findMethodBestMatch(MClass.loadClass("com.tencent.mobileqq.startup.step.LoadData"),"doStep"),param -> {
                long timeStart = System.currentTimeMillis();

                if (HookEnv.ExtraDataPath == null) ExtraPathInit.ShowPathSetDialog();
                else HookLoader.CallAllDelayHook();
                InitAppCenter();

                XposedBridge.log("[QTool]Delay Hook End,time cost:"+(System.currentTimeMillis() - timeStart)+"ms");


            });
        }

    }
}
