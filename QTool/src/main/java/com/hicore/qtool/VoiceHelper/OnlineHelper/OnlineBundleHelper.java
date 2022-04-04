package com.hicore.qtool.VoiceHelper.OnlineHelper;

import android.text.TextUtils;

import com.hicore.Utils.FileUtils;
import com.hicore.Utils.HttpUtils;
import com.hicore.Utils.NameUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.VoiceHelper.Panel.VoiceProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OnlineBundleHelper {
    public static void createBundle(String Name){
        String key = requestForRndKey();
        String URL = "https://qtool.haonb.cc/VoiceBundle/Create?name="+ URLEncoder.encode(Name)+"&key="+key
                +"&uin="+ QQEnvUtils.getCurrentUin();
        String ret = HttpUtils.getContent(URL);
        if ("success".equals(ret)){
            Utils.ShowToast("已创建,名字审核通过后即可在其他用户显示");
        }else {
            Utils.ShowToastL("网络异常");
        }
    }
    public static ArrayList<VoiceProvider.FileInfo> getAllBundle(){
        try{
            ArrayList<VoiceProvider.FileInfo> retInfo = new ArrayList<>();
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/getList");
            JSONObject mJson = new JSONObject(Content);
            JSONArray mArray = mJson.getJSONArray("data");
            for(int i=0;i<mArray.length();i++){
                JSONObject item = mArray.getJSONObject(i);
                VoiceProvider.FileInfo info = new VoiceProvider.FileInfo();
                info.type = 5;
                info.Name = item.getString("name");
                info.Path = item.getString("id");
                retInfo.add(info);
            }
            return retInfo;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public static ArrayList<VoiceProvider.FileInfo> getBundleContent(String BundleID){
        try{
            ArrayList<VoiceProvider.FileInfo> retInfo = new ArrayList<>();
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundleInfo");
            JSONObject mJson = new JSONObject(Content);
            JSONArray mArray = mJson.getJSONArray("data");
            for(int i=0;i<mArray.length();i++){
                JSONObject item = mArray.getJSONObject(i);
                VoiceProvider.FileInfo info = new VoiceProvider.FileInfo();
                info.type = 6;
                info.Name = item.getString("Name");
                info.Path = "https://cdn.haonb.cc/"+item.getString("URI");
                retInfo.add(info);
            }
            return retInfo;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    private static String requestForRndKey(){
        String rndKey = FileUtils.ReadFileString(HookEnv.ExtraDataPath + "配置文件目录/VoiceToken");
        if (TextUtils.isEmpty(rndKey)){
            rndKey = NameUtils.getRandomString(64);
            FileUtils.WriteToFile(HookEnv.ExtraDataPath + "配置文件目录/VoiceToken",rndKey);
        }
        return rndKey;
    }
}

