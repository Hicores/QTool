package cc.hicore.Tracker;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.ThreadUtils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.HostInfo;

public class AutoReport {
    private static final ExecutorService singleReport = Executors.newSingleThreadExecutor();
    public static void reportException(String tag,String contain){
        singleReport.execute(()-> reportExceptionThread(tag,contain));
    }
    private static void reportExceptionThread(String tag,String contain){
        try {
            JSONObject data = new JSONObject();
            data.put("tag",tag);
            data.put("contain",contain);
            data.put("version", BuildConfig.VERSION_NAME);
            data.put("QQVersion", HostInfo.getVersion());
            data.put("Uin", QQEnvUtils.getCurrentUin());
            byte[] reportData = ("d="+DataUtils.ByteArrayToHex(data.toString().getBytes())).getBytes(StandardCharsets.UTF_8);

            HttpUtils.Post("https://qtool.haonb.cc/reportError",reportData);
        }catch (Throwable e){

        }
    }
}
