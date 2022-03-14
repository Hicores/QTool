package com.hicore.qtool.JavaPlugin.Controller;

import com.hicore.Utils.DataUtils;
import com.hicore.Utils.FileUtils;
import com.hicore.qtool.HookEnv;

import org.json.JSONObject;

import java.io.File;

public class PluginStoreUtils {
    private static void reqPath(){
        String Path = HookEnv.ExtraDataPath + "/PluginConfig/";
        if (!new File(Path).exists()){
            new File(Path).mkdirs();

        }
    }
    public static String getString(String PluginID,String key){
        reqPath();
        String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
        try{
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            return itemJSON.getString(key);
        }catch (Exception e){
        }
        return null;
    }
    public static void putString(String PluginID,String key,String value){
        try{
            reqPath();
            String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            itemJSON.put(key,value);
            FileUtils.WriteToFile(Path,itemJSON.toString());
        }catch (Exception e){

        }


    }
    public static boolean getBoolean(String PluginID,String key,boolean defValue){
        String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
        try{
            reqPath();
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            return itemJSON.getBoolean(key);
        }catch (Exception e){
        }
        return defValue;
    }
    public static void putBoolean(String PluginID,String key,boolean value){
        try{
            reqPath();
            String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            itemJSON.put(key,value);
            FileUtils.WriteToFile(Path,itemJSON.toString());
        }catch (Exception e){

        }
    }
    public static int getInt(String PluginID,String key,int defValue){
        String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
        try{
            reqPath();
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            return itemJSON.getInt(key);
        }catch (Exception e){
        }
        return defValue;
    }
    public static void putInt(String PluginID,String key,int value){
        try{
            reqPath();
            String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            itemJSON.put(key,value);
            FileUtils.WriteToFile(Path,itemJSON.toString());
        }catch (Exception e){

        }
    }
    public static long getLong(String PluginID,String key,long value)
    {
        String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
        try{
            reqPath();
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            return itemJSON.getLong(key);
        }catch (Exception e){
        }
        return value;
    }
    public static void putLong(String PluginID,String key,long value){
        try{
            reqPath();
            String Path = HookEnv.ExtraDataPath + "/PluginConfig/"+ PluginID.hashCode()+".json";
            JSONObject itemJSON = new JSONObject(FileUtils.ReadFileString(Path));
            itemJSON.put(key,value);
            FileUtils.WriteToFile(Path,itemJSON.toString());
        }catch (Exception e){

        }
    }
}
