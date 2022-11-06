package cc.hicore.Tracker;

import com.microsoft.appcenter.crashes.Crashes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.hicore.qtool.BuildConfig;
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
