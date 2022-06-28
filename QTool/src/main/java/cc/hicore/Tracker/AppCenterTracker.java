package cc.hicore.Tracker;

import com.microsoft.appcenter.analytics.Analytics;

import java.util.HashMap;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@XPItem(name = "AppCenter_Init",itemType = XPItem.ITEM_Hook,period = XPItem.Period_InitData)
public class AppCenterTracker{
    public static void StartTrack() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<String, String> trackData = new HashMap<>();
        trackData.put("Uin", QQEnvUtils.getCurrentUin());
        trackData.put("UseVer", BuildConfig.VERSION_NAME);
        Analytics.trackEvent("QTLoadInMainProc", trackData);
    }
    @VerController
    @CommonExecutor
    public void startHook(){
        new Thread(AppCenterTracker::StartTrack).start();
    }

}
