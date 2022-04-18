package cc.hicore.Tracker;

import cc.hicore.HookItem;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.microsoft.appcenter.analytics.Analytics;

import java.util.HashMap;

@HookItem(isDelayInit = true,isRunInAllProc = false)
public class AppCenterTracker extends BaseHookItem {
    public static void StartTrack(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<String,String> trackData = new HashMap<>();
        trackData.put("Uin", QQEnvUtils.getCurrentUin());
        trackData.put("UseVer", BuildConfig.VERSION_NAME);
        Analytics.trackEvent("QTLoadInMainProc",trackData);
    }

    @Override
    public boolean startHook() throws Throwable {
        new Thread(AppCenterTracker::StartTrack).start();
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return true;
    }
}
