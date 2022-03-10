package com.hicore.qtool.JavaPlugin.Controller;

import java.util.ArrayList;

public class PluginSetController {
    public static boolean IsBlackMode(String PluginID){
        return false;
    }
    public static boolean IsAutoLoad(String PluginID){
        return false;
    }
    public static ArrayList<String> getAutoLoadList(){
        return new ArrayList<>();
    }
    public static ArrayList<String> getModeList(String PluginID){
        return new ArrayList<>();
    }
}
