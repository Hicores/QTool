package com.hicore.qtool.JavaPlugin.Controller;

import com.hicore.qtool.HookEnv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static List<String> getAutoLoadList(){
        return Arrays.asList(HookEnv.Config.getKeys("Plugin_Auto_Load_List"));
    }
    public static List<String> getModeList(String PluginID){
        return HookEnv.Config.getList("Plugin_Mode_List",PluginID, true);
    }
    public static void setModeList(String PluginID,ArrayList<String> list){
        if (list.size() == 0){
            HookEnv.Config.removeKey("Plugin_Mode_List",PluginID);
        }else {
            HookEnv.Config.setList("Plugin_Mode_List",PluginID,list);
        }
    }
}
