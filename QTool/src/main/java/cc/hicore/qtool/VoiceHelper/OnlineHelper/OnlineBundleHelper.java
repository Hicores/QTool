package cc.hicore.qtool.VoiceHelper.OnlineHelper;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.NameUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.VoiceHelper.Panel.VoiceProvider;

public class OnlineBundleHelper {
    public static void createBundle(String Name) {
        String key = requestForRndKey();
        String URL = "https://qtool.haonb.cc/VoiceBundle/Create?name=" + URLEncoder.encode(Name) + "&key=" + key
                + "&uin=" + QQEnvUtils.getCurrentUin();
        String ret = HttpUtils.getContent(URL);
        if ("success".equals(ret)) {
            Utils.ShowToast("已创建,可以通过长按本地语音来添加语音");
        } else {
            Utils.ShowToastL("网络异常");
        }
    }

    public static void RequestUpload(String Name, String LocalPath, String ToBundleID) throws Exception {
        String req = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/RequestUpload?ID=" + ToBundleID + "&Key=" + requestForRndKey() + "&Name=" + Name);
        JSONObject newJson = new JSONObject(req);
        if (newJson.optInt("code") == 1) {
            Utils.ShowToastL("未能成功上传:" + newJson.optString("msg"));
            return;
        }
        String key = newJson.getString("key");

        HttpURLConnection conn = (HttpURLConnection) new URL("https://qtool.haonb.cc/VoiceBundle/upload").openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("key", key);
        conn.setRequestProperty("accessKey", requestForRndKey());
        OutputStream out = conn.getOutputStream();

        out.write(FileUtils.ReadFile(new File(LocalPath)));
        out.close();

        InputStream insp = conn.getInputStream();
        byte[] bArr = DataUtils.readAllBytes(insp);
        insp.close();
        JSONObject retJson = new JSONObject(new String(bArr));
        if (retJson.optInt("code") == 1) {
            Utils.ShowToastL("上传错误:" + retJson.optString("msg"));
            return;
        }
    }

    public static ArrayList<VoiceProvider.FileInfo> getAllBundle() {
        try {
            ArrayList<VoiceProvider.FileInfo> retInfo = new ArrayList<>();
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/getList");
            JSONObject mJson = new JSONObject(Content);
            JSONArray mArray = mJson.getJSONArray("data");
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject item = mArray.getJSONObject(i);
                VoiceProvider.FileInfo info = new VoiceProvider.FileInfo();
                info.type = 5;
                info.Name = item.getString("name");
                info.Path = item.getString("id");
                retInfo.add(info);
            }
            return retInfo;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<VoiceProvider.FileInfo> getBundleContent(String BundleID) {
        try {
            ArrayList<VoiceProvider.FileInfo> retInfo = new ArrayList<>();
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundleInfo?id=" + BundleID);
            JSONObject mJson = new JSONObject(Content);
            JSONArray mArray = mJson.getJSONArray("data");
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject item = mArray.getJSONObject(i);
                VoiceProvider.FileInfo info = new VoiceProvider.FileInfo();
                info.type = 6;
                info.Name = item.getString("Name");
                info.Path = "https://cdn.haonb.cc/" + item.getString("URI");
                retInfo.add(info);
            }
            return retInfo;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<VoiceProvider.FileInfo> searchForName(String Name) {
        try {
            ArrayList<VoiceProvider.FileInfo> retInfo = new ArrayList<>();
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/Search?name=" + URLEncoder.encode(Name));
            JSONObject mJson = new JSONObject(Content);
            JSONArray mArray = mJson.getJSONArray("data");
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject item = mArray.getJSONObject(i);
                VoiceProvider.FileInfo info = new VoiceProvider.FileInfo();
                info.type = 6;
                info.Name = item.getString("Name");
                info.Path = "https://cdn.haonb.cc/" + item.getString("URI");
                retInfo.add(info);
            }
            return retInfo;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static String requestForRndKey() {
        String rndKey = FileUtils.ReadFileString(HookEnv.ExtraDataPath + "配置文件目录/VoiceToken");
        if (TextUtils.isEmpty(rndKey)) {
            rndKey = NameUtils.getRandomString(64);
            FileUtils.WriteToFile(HookEnv.ExtraDataPath + "配置文件目录/VoiceToken", rndKey);
        }
        return rndKey;
    }
}

