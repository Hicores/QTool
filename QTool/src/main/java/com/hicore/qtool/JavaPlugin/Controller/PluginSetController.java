package com.hicore.qtool.JavaPlugin.Controller;

import com.hicore.qtool.HookEnv;

import java.util.ArrayList;

public class PluginSetController {
    public static boolean IsBlackMode(String PluginID){
        return HookEnv.Config.getBoolean("Plugin_Black_Mode_List",PluginID,false);
    }
    public static void SetBlackMode(String PluginID,boolean IsBlack){
        if (IsBlack){
            HookEnv.Config.setBoolean("Plugin_Black_Mode_List",PluginID,true);
        }else {
            HookEnv.Config.removeKey("Plugin_Black_Mode_List",PluginID);
        }
    }
    public static boolean IsAutoLoad(String PluginID){
        return HookEnv.Config.getBoolean("Plugin_Auto_Load_List",PluginID,false);
    }
    public static void SetAutoLoad(String PluginID,boolean IsAutoLoad){
        if (IsAutoLoad) HookEnv.Config.setBoolean("Plugin_Auto_Load_List",PluginID,true);
        else HookEnv.Config.removeKey("Plugin_Auto_Load_List",PluginID);
    }
    public static ArrayList<String> getAutoLoadList(){
        return new ArrayList<>();
    }
    public static ArrayList<String> getModeList(String PluginID){
        return new ArrayList<>();
    }
}
