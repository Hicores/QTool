package cc.hicore.qtool.JavaPlugin.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.ThreadUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "PluginControl",itemType = XPItem.ITEM_Hook)
public class KeyWordUtils {
    private static String keyWords;
    @VerController
    @CommonExecutor
    public void work(){
        ThreadUtils.PostCommonTask(KeyWordUtils::updateRemoteWords);
    }
    public static void updateRemoteWords(){
        keyWords = HttpUtils.getContent("https://qtool.haonb.cc/PluginControl/getKeyWords");
    }
    public static String checkAndRemoveUnusedWord(String Plugin){
        return Plugin;
    }
    public static boolean checkIsContainForbiddenKeyword(String Plugin){
        try {
            if (keyWords == null)return false;
            JSONArray md5Arr = new JSONArray(keyWords);
            for (int i = 0; i < md5Arr.length(); i++) {
                JSONObject item = md5Arr.getJSONObject(i);
                int type = item.getInt("type");
                if (type == 1){
                    if (DataUtils.getStrMD5(Plugin).equals(item.getString("rule"))){
                        return true;
                    }
                }else if (type == 2){
                    int subType = item.getInt("subType");
                    if (Plugin.contains(item.getString("rule"))){
                        if (subType == 1){
                            return true;
                        }else {
                            boolean isStrict = item.getBoolean("isStrict");
                            if (isStrict){
                                ThreadUtils.PostCommonTask(()->reportStrictUseForPlugin(QQEnvUtils.getCurrentUin(),item.toString(),Plugin));
                            }else {
                                ThreadUtils.PostCommonTask(()->reportAbnormalUseForPlugin(QQEnvUtils.getCurrentUin(),item.toString()));
                            }
                        }
                    }

                }else if (type == 3){
                    int subType = item.getInt("subType");
                    Pattern pattern = Pattern.compile(item.getString("rule"));
                    Matcher matcher = pattern.matcher(Plugin);
                    if (matcher.find()){
                        if (subType == 1){
                            return true;
                        }else {
                            boolean isStrict = item.getBoolean("isStrict");
                            if (isStrict){
                                ThreadUtils.PostCommonTask(()->reportStrictUseForPlugin(QQEnvUtils.getCurrentUin(),item.toString(),Plugin));
                            }else {
                                ThreadUtils.PostCommonTask(()->reportAbnormalUseForPlugin(QQEnvUtils.getCurrentUin(),item.toString()));
                            }
                        }
                    }
                }
            }

        }catch (Exception e){

        }
        return false;
    }
    public static void reportAbnormalUseForPlugin(String Uin,String hitRules){
        try {
            if (checkIsNeedReport()){
                JSONObject reportJson = new JSONObject();
                reportJson.put("uin",Uin);
                reportJson.put("hitRules",hitRules);
                HttpUtils.Post("https://qtool.haonb.cc/PluginControl/report",("data="+DataUtils.ByteArrayToHex(reportJson.toString().getBytes())).getBytes(StandardCharsets.UTF_8));
            }
        }catch (Exception e){ }
    }
    public static void reportStrictUseForPlugin(String Uin,String hitRules,String PluginContain){
        try {
            if (checkIsNeedReport()){
                JSONObject reportJson = new JSONObject();
                reportJson.put("uin",Uin);
                reportJson.put("hitRules",hitRules);
                reportJson.put("contain",PluginContain);
                HttpUtils.Post("https://qtool.haonb.cc/PluginControl/report",("data="+DataUtils.ByteArrayToHex(reportJson.toString().getBytes())).getBytes(StandardCharsets.UTF_8));
            }
        }catch (Exception e){ }
    }
    private static boolean checkIsNeedReport(){
        long lastTime = HookEnv.Config.getLong("global","reportTime",0);
        if (lastTime + 20 * 60 * 1000 < System.currentTimeMillis()){
            HookEnv.Config.setLong("global","reportTime",System.currentTimeMillis());
            return true;
        }
        return false;
    }
    public static boolean Pattern_Matches(String raw,String Regex){
        try{
            Pattern pt = Pattern.compile(Regex);
            Matcher matcher = pt.matcher(raw);
            return matcher.find();
        }catch (Exception e){
            return false;
        }

    }
}
