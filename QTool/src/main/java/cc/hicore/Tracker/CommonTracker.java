package cc.hicore.Tracker;

import android.os.Build;
import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.CrashHandler.CatchInstance;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "UsageDataTracker",itemType = XPItem.ITEM_Hook,period = XPItem.Period_InitData)
public class CommonTracker {
    @VerController
    @CommonExecutor
    public void startTrack(){
        new Thread(()->{
            if (isTrackAvailable()){
                try {
                    Thread.sleep(9999);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] data = ("in="+DataUtils.ByteArrayToHex(collectInfo().getBytes(StandardCharsets.UTF_8))).getBytes(StandardCharsets.UTF_8);
                HttpUtils.PostForResult("https://qtool.haonb.cc/track/trackUsageData","2333",data,data.length);
            }
        }).start();;

    }
    private String collectInfo(){
        try {
            JSONObject trackData = new JSONObject();
            trackData.put("uin", QQEnvUtils.getCurrentUin());
            trackData.put("deviceHash", DataUtils.getStrMD5(Settings.Secure.ANDROID_ID));
            trackData.put("deviceName", Build.BRAND);
            trackData.put("xposedFlag", CatchInstance.TGetFrameName());
            trackData.put("buildVer", Build.VERSION.RELEASE);
            trackData.put("QQVersion", HostInfo.getVersion());
            trackData.put("moduleVer", BuildConfig.VERSION_NAME);
            trackData.put("items",collectBugReportInfo());

            return trackData.toString();
        }catch (Exception e){
            return "";
        }

    }
    private JSONArray collectBugReportInfo() throws JSONException {
        JSONArray items = new JSONArray();
        for (CoreLoader.XPItemInfo info : CoreLoader.allInstance.values()){
            JSONObject item = new JSONObject();
            item.put("itemName",info.ItemName);
            item.put("itemID",info.id);
            item.put("isOpen",info.isEnabled);

            JSONArray bugArr = new JSONArray();
            for (JSONObject bugItem : collectMethodFindErr(info))bugArr.put(bugItem);
            for (JSONObject bugItem : collectReportErr(info))bugArr.put(bugItem);
            item.put("bugs",bugArr);
            items.put(item);
        }
        return items;
    }
    private List<JSONObject> collectMethodFindErr(CoreLoader.XPItemInfo info) throws JSONException {
        ArrayList<JSONObject> obj = new ArrayList<>();
        for (String methodFinderName : info.scanResult.keySet()){
            if (info.scanResult.get(methodFinderName) == null){
                JSONObject bugNewItem = new JSONObject();
                bugNewItem.put("name","methodFinder->"+bugNewItem);
                bugNewItem.put("contain","method is null");
                obj.add(bugNewItem);
            }
        }
        return obj;
    }
    private List<JSONObject> collectReportErr(CoreLoader.XPItemInfo info) throws JSONException {
        ArrayList<JSONObject> newData = new ArrayList<>();

        for (String reportData : info.cacheException){
            JSONObject bugNewItem = new JSONObject();
            bugNewItem.put("name","报告异常");
            bugNewItem.put("contain",reportData);
            newData.add(bugNewItem);
        }

        for (String name : info.ExecutorException.keySet()){
            String data = info.ExecutorException.get(name);
            JSONObject bugNewItem = new JSONObject();
            bugNewItem.put("name","执行异常->"+name);
            bugNewItem.put("contain",data);
            newData.add(bugNewItem);
        }
        return newData;
    }
    public boolean isTrackAvailable(){
        String Uin = QQEnvUtils.getCurrentUin();
        long lastReportTime = HookEnv.Config.getLong("TrackEvent",Uin+"-Time",0);
        if (System.currentTimeMillis() - lastReportTime > 1000 * 60 * 60 * 24){
            HookEnv.Config.setLong("TrackEvent",Uin+"-Time",System.currentTimeMillis());
            return true;
        }else {
            return false;
        }
    }
}
