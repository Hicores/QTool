package cc.hicore.qtool.XposedInit;

import static cc.hicore.qtool.HookEnv.moduleLoader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import bsh.classpath.BshLoaderManager;
import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.CrashHandler.LogcatCatcher;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class EnvHook {
    private static final String TAG = "EnvHook";
    private static final AtomicBoolean IsInit = new AtomicBoolean();

    public static void HookForContext() {
        //由于很多环境的初始化都需要Context来进行,所有这里选择直接Hook获取Context再进行初始化
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", HookEnv.mLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (IsInit.getAndSet(true)) return;
                if (HookEnv.IsMainProcess) {
                    XposedBridge.log("[QTool]BaseHook Start");
                }

                long timeStart = System.currentTimeMillis();
                try {
                    HookEnv.Application = (Application) param.thisObject;
                    HookEnv.AppContext = HookEnv.Application.getApplicationContext();
                    HostInfo.Init();
                    //取代QQ的classLoader防止有一些框架传递了不正确的classLoader
                    HookEnv.mLoader = param.thisObject.getClass().getClassLoader();

                    BshLoaderManager.addClassLoader(HookEnv.mLoader);


                    moduleLoader = EnvHook.class.getClassLoader();

                    //优先初始化Path
                    ExtraPathInit.InitPath();

                    //然后注入资源
                    EzXHelperInit.INSTANCE.initAppContext(HookEnv.AppContext, false, true);
                    ResUtils.StartInject(HookEnv.AppContext);
                    //然后进行延迟Hook,同时如果目录未设置的时候能弹出设置界面
                    HookForDelay();

                    if (HostInfo.getVerCode() < QQVersion.QQ_8_8_35) return;
                    if (HostInfo.getVersion().length() > 7) return;

                    if (GlobalConfig.Get_Boolean("Prevent_Crash_In_Java", false)) {
                        LogcatCatcher.startCatcherOnce();
                    }

                    SettingInject.startInject();

                    if (HookEnv.ExtraDataPath != null) {
                        InitActivityProxy();
                        //在外部数据路径不为空且有效的情况下才加载Hook,防止意外导致的设置项目全部丢失

                        CoreLoader.onBeforeLoad();
                    }

                } finally {
                    if (HookEnv.IsMainProcess) {
                        XposedBridge.log("[QTool]BaseHook Init End,time cost:" + (System.currentTimeMillis() - timeStart) + "ms");
                    }
                }
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", HookEnv.mLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (IsInit.getAndSet(true)) return;
                if (HookEnv.IsMainProcess) {
                    XposedBridge.log("[QTool]BaseHook Start in Base HookTarget");
                }
                long timeStart = System.currentTimeMillis();
                HookEnv.Application = (Application) param.thisObject;
                HookEnv.AppContext = (Context) param.args[0];
                HostInfo.Init();
                //取代QQ的classLoader防止有一些框架传递了不正确的classLoader
                HookEnv.mLoader = param.thisObject.getClass().getClassLoader();

                BshLoaderManager.addClassLoader(HookEnv.mLoader);


                moduleLoader = EnvHook.class.getClassLoader();

                //优先初始化Path
                ExtraPathInit.InitPath();

                //然后注入资源
                EzXHelperInit.INSTANCE.initAppContext(HookEnv.AppContext, false, true);
                ResUtils.StartInject(HookEnv.AppContext);
                //然后进行延迟Hook,同时如果目录未设置的时候能弹出设置界面
                HookForDelay();
                if (HostInfo.getVerCode() < QQVersion.QQ_8_8_35) return;
                if (HostInfo.getVersion().length() > 7) return;
                if (HostInfo.checkIsGrayQQ())return;

                if (GlobalConfig.Get_Boolean("Prevent_Crash_In_Java", false)) {
                    LogcatCatcher.startCatcherOnce();
                }

                SettingInject.startInject();

                if (HookEnv.ExtraDataPath != null) {
                    InitActivityProxy();
                    //在外部数据路径不为空且有效的情况下才加载Hook,防止意外导致的设置项目全部丢失

                    CoreLoader.onBeforeLoad();
                }

                if (HookEnv.IsMainProcess) {
                    XposedBridge.log("[QTool]BaseHook Init End,time cost:" + (System.currentTimeMillis() - timeStart) + "ms");
                }
            }
        });
    }

    private static void InitAppCenter() {
        try {
            if (!HookEnv.IsMainProcess) return;
            AppCenter.start(HookEnv.Application, "6f119935-286d-4a6b-b9e4-c9f18513dbf8",
                    Analytics.class, Crashes.class);
        } catch (Exception e) {
            LogUtils.error("AppCenter", e);
        }


    }

    private static void InitActivityProxy() {
        if (HookEnv.IsMainProcess) {
            EzXHelperInit.INSTANCE.initActivityProxyManager(BuildConfig.APPLICATION_ID, "com.tencent.mobileqq.activity.AboutActivity", moduleLoader, HookEnv.mLoader);
            EzXHelperInit.INSTANCE.initSubActivity();
        }

    }

    //在QQ完成一些初步的环境初始化后才开始执行一些代码
    private static void HookForDelay() {
        if (HookEnv.IsMainProcess) {
            new Handler(Looper.getMainLooper()).postDelayed(()->{
                long timeStart = System.currentTimeMillis();
                BeforeCheck.StartCheckAndShow();
                if (HostInfo.checkIsGrayQQ()){
                    BeforeCheck.showGrayQQTip();
                    return;
                }
                if (HostInfo.getVerCode() < QQVersion.QQ_8_8_35) {
                    CheckWrongVersion.ShowToast1(Utils.getTopActivity());
                    return;
                }
                if (HostInfo.getVersion().length() > 8) {
                    CheckWrongVersion.ShowWrongVersionDialog(Utils.getTopActivity());
                    return;
                }

                if (TextUtils.isEmpty(HookEnv.ExtraDataPath)) {
                    ExtraPathInit.ShowPathSetDialog(false);
                    return;
                }
                CoreLoader.onAfterLoad();

                InitActivityProxy();
                InitAppCenter();


                XposedBridge.log("[QTool]Delay Hook End,time cost:" + (System.currentTimeMillis() - timeStart) + "ms");
            },5000);
        }

    }

    public static void requireCachePath() {
        File cache = new File(HookEnv.ExtraDataPath, "Cache");
        if (!cache.exists()) cache.mkdirs();
    }
}
