package com.hicore.qtool.XposedInit;

import static com.hicore.qtool.HookEnv.moduleLoader;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.qtool.BuildConfig;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.ingestion.models.Device;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class EnvHook {
    private static final String TAG = "EnvHook";
    public static void HookForContext(){
        //由于很多环境的初始化都需要Context来进行,所有这里选择直接Hook获取Context再进行初始化
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", HookEnv.mLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                HookEnv.AppContext = (Context) param.args[0];

                //取代QQ的classLoader防止有一些框架传递了不正确的classLoader
                HookEnv.mLoader = param.thisObject.getClass().getClassLoader();

                ClassLoader fixLoader = EnvHook.class.getClassLoader().getParent();
                if (fixLoader instanceof HookEntry.FixSubClassLoader){
                    LogUtils.debug(TAG,"Init from FixSubClassLoade");
                    HookEnv.fixLoader = (HookEntry.FixSubClassLoader) fixLoader;
                    HookEnv.fixLoader.addHostLoader(HookEnv.mLoader);
                }
                moduleLoader = EnvHook.class.getClassLoader();
                InitAppCenter();
                //优先初始化Path
                ExtraPathInit.InitPath();

                //然后注入资源
                LogUtils.debug(TAG,"BaseHook Start");
                EzXHelperInit.INSTANCE.initAppContext(HookEnv.AppContext,false,true);
                ResUtils.StartInject(HookEnv.AppContext);
                //然后进行延迟Hook,同时如果目录未设置的时候能弹出设置界面

                HookForDelayDialog();
                if (HookEnv.ExtraDataPath != null){
                    HostInfo.Init();
                    InitActivityProxy();
                    //在外部数据路径不为空且有效的情况下才加载Hook,防止意外导致的设置项目全部丢失
                    HookLoader.SearchAndLoadAllHook();
                }
                LogUtils.debug(TAG,"BaseHook End");

            }
        });
    }
    private static void InitAppCenter(){
        try {
            AppCenter.start((Application) HookEnv.AppContext, "6f119935-286d-4a6b-b9e4-c9f18513dbf8",
                    Analytics.class, Crashes.class);
            Channel objChannel = MField.GetField(AppCenter.getInstance(),"mChannel");
            objChannel.addListener(new AbstractChannelListener(){
                @Override
                public void onPreparedLog(@NonNull com.microsoft.appcenter.ingestion.models.Log log, @NonNull String groupName, int flags) {
                    Device device = log.getDevice();
                    device.setAppVersion(BuildConfig.VERSION_NAME);
                    device.setAppBuild(String.valueOf(BuildConfig.VERSION_CODE));
                    device.setAppNamespace(BuildConfig.APPLICATION_ID);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private static void InitActivityProxy(){
        EzXHelperInit.INSTANCE.initActivityProxyManager(BuildConfig.APPLICATION_ID,"com.tencent.mobileqq.activity.AboutActivity", moduleLoader, HookEnv.mLoader);
        EzXHelperInit.INSTANCE.initSubActivity();
    }
    private static void HookForDelayDialog(){
        XPBridge.HookBeforeOnce(XposedHelpers.findMethodBestMatch(MClass.loadClass("com.tencent.mobileqq.startup.step.LoadData"),"doStep"),param -> {
            LogUtils.debug(TAG,"DelayHook Start");
            if (HookEnv.ExtraDataPath == null) ExtraPathInit.ShowPathSetDialog();
            else HookLoader.CallAllDelayHook();
            LogUtils.debug(TAG,"DelayHook End");
        });
    }
}
