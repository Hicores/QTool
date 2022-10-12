package cc.hicore.Tracker;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.crashes.utils.ErrorLogHelper;
import com.microsoft.appcenter.utils.AppCenterLog;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import de.robv.android.xposed.XposedBridge;

public class AutoReport {
    private static final ExecutorService singleReport = Executors.newSingleThreadExecutor();
    private static final HashSet<String> reportCache = new HashSet<>();
    public static void reportException(String tag,Throwable contain,String extra){
        if (reportCache.contains(tag+"->"+contain))return;
        reportCache.add(tag+"->"+contain);
        singleReport.execute(()-> reportExceptionThread(tag,contain,extra));
    }
    private static void reportExceptionThread(String tag,Throwable contain,String extra){
        try {
            Map<String, String> properties = new HashMap<String, String>() {{
                put("ver", BuildConfig.VERSION_NAME);
                put("tag",tag);
                put("extra",extra);
            }};
            Crashes.trackError(contain, properties, null);
        }catch (Throwable e){
            XposedBridge.log(e);
        }
    }
}
