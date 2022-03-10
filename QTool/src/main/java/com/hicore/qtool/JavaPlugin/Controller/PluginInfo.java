package com.hicore.qtool.JavaPlugin.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import bsh.Interpreter;

public class PluginInfo {
    public String PluginID;
    public String PluginVerifyID;
    public String LocalPath;
    public boolean IsBlackMode;
    public ArrayList<String> ListStr;

    public boolean IsLoadSuccess;
    public boolean IsLoading;

    public String PluginName;
    public String PluginAuthor;
    public String PluginVersion;
    public Interpreter Instance;
    public HashMap<String,FunctionInfo> ItemFunctions = new HashMap<>();

    public boolean IsAvailable(String GroupUin,int isTroop){
        if (isTroop == 1 || isTroop == 10014){
            return IsBlackMode != ListStr.contains(GroupUin);
        }
        return true;
    }

    public static class FunctionInfo{
        public String FunctionName;
        public String FunctionCallback;

        public int FunctionType;
    }

}
