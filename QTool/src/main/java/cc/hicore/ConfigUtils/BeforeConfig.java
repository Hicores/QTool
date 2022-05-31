package cc.hicore.ConfigUtils;

import org.json.JSONObject;

import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;

public class BeforeConfig {
    public static boolean getBoolean(String Name){
        String path = HookEnv.AppPath + "/Global.json";
        JSONObject jsonObject;
        try {
            jsonObject= new JSONObject(FileUtils.ReadFileString(path));
        }catch (Exception e){
            jsonObject = new JSONObject();
        }
        return jsonObject.optBoolean(Name);
    }
    public static void putBoolean(String Name,boolean value){
        String path = HookEnv.AppPath + "/Global.json";
        JSONObject jsonObject;
        try {
            jsonObject= new JSONObject(FileUtils.ReadFileString(path));
        }catch (Exception e){
            jsonObject = new JSONObject();
        }
        try{
            jsonObject.put(Name,value);
            FileUtils.WriteToFile(path,jsonObject.toString());
        }catch (Exception ignored){

        }
    }
    public static int getInt(String Name){
        String path = HookEnv.AppPath + "/Global.json";
        JSONObject jsonObject;
        try {
            jsonObject= new JSONObject(FileUtils.ReadFileString(path));
        }catch (Exception e){
            jsonObject = new JSONObject();
        }
        return jsonObject.optInt(Name);
    }
    public static void putInt(String Name,int value){
        String path = HookEnv.AppPath + "/Global.json";
        JSONObject jsonObject;
        try {
            jsonObject= new JSONObject(FileUtils.ReadFileString(path));
        }catch (Exception e){
            jsonObject = new JSONObject();
        }
        try{
            jsonObject.put(Name,value);
            FileUtils.WriteToFile(path,jsonObject.toString());
        }catch (Exception ignored){

        }
    }

}
