package cc.hicore.qtool.JavaPlugin.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.ThreadUtils;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "PluginControl",itemType = XPItem.ITEM_Hook)
public class KeyWordUtils {
    private static final HashMap<String, String> keyWords = new HashMap<>();
    @VerController
    @CommonExecutor
    public void work(){
        ThreadUtils.PostCommonTask(KeyWordUtils::updateRemoteWords);
    }
    public static void updateRemoteWords(){
        String sForbiddenKeyWords = HttpUtils.getContent("https://qtool.haonb.cc/PluginControl/getForbiddenKeyWords");
        String sForbiddenPluginMD5 = HttpUtils.getContent("https://qtool.haonb.cc/PluginControl/getForbiddenPluginMD5");
        String sReportKeyWords = HttpUtils.getContent("https://qtool.haonb.cc/PluginControl/getReportKeyWords");

        keyWords.put("forbiddenKeyWord",sForbiddenKeyWords);
        keyWords.put("forbiddenPluginMD5",sForbiddenPluginMD5);
        keyWords.put("reportKeyWord",sReportKeyWords);
    }
    public static String checkAndRemoveUnusedWord(String Plugin){
        return Plugin;
    }
    public static boolean checkIsContainForbiddenKeyword(String Plugin){
        try {
            JSONArray md5Arr = new JSONArray(keyWords.get("forbiddenPluginMD5"));
            for (int i = 0; i < md5Arr.length(); i++) {
                JSONObject item = md5Arr.getJSONObject(i);
                if (DataUtils.getStrMD5(Plugin).equals(item.getString("word"))){
                    return true;
                }
            }

            JSONArray keyWordArr = new JSONArray(keyWords.get("forbiddenKeyWord"));
            for (int i = 0; i < keyWordArr.length(); i++) {
                JSONObject item = keyWordArr.getJSONObject(i);
                if (Plugin.contains(item.getString("word"))){
                    return true;
                }
            }
        }catch (Exception e){

        }
        return false;
    }
    public static void checkAndReportBanKeyWord(String PluginContain) throws JSONException {
        JSONArray keyWordArr = new JSONArray(keyWords.get("reportKeyWord"));
        for (int i = 0; i < keyWordArr.length(); i++) {
            JSONObject item = keyWordArr.getJSONObject(i);
            if (PluginContain.contains(item.getString("word"))){
                ThreadUtils.PostCommonTask(()->reportAbnormalUseForPlugin(QQEnvUtils.getCurrentUin(),item.toString()));
            }
        }
    }
    public static void reportAbnormalUseForPlugin(String Uin,String hitRules){
        try {
            JSONObject reportJson = new JSONObject();
            reportJson.put("uin",Uin);
            reportJson.put("hitRules",hitRules);
            HttpUtils.getContent("https://qtool.haonb.cc/PluginControl/report?data="+DataUtils.ByteArrayToHex(reportJson.toString().getBytes()));
        }catch (Exception e){

        }

    }
}
