package cc.hicore.HookItemLoader.core;

import android.content.SharedPreferences;

import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;

public class SecurityChecker {
    public static int checkLoaderType(){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("SecurityLoad",0);
        return share.getInt("type",0);
    }
    public static void saveLoaderType(int type){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("SecurityLoad",0);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt("type",type);
        editor.commit();
    }
    public static boolean isLoading(){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("SecurityLoad",0);
        return share.getBoolean("loading",false);
    }
    public static void savePreload(){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("SecurityLoad",0);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean("loading",true);
        editor.commit();
    }
    public static void finishPreload(){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("SecurityLoad",0);
        SharedPreferences.Editor editor = share.edit();
        editor.remove("loading");
        editor.commit();
    }
    public static void crash(){
        QQEnvUtils.ExitQQAnyWays();
    }
}
