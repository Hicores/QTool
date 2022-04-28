package cc.hicore.ConfigUtils;

import android.content.SharedPreferences;

import cc.hicore.qtool.HookEnv;

public class GlobalConfig {
    private static final String TAG = "QTool";

    public static void Put_String(String Name, String Value) {
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(Name, Value);
        editor.commit();
    }

    public static String Get_String(String Name) {
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences(TAG, 0);
        return share.getString(Name, "");
    }

    public static void Put_Boolean(String Name, boolean Value) {
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(Name, Value);
        editor.commit();
    }

    public static boolean Get_Boolean(String Name, boolean Defvalue) {
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences(TAG, 0);
        return share.getBoolean(Name, Defvalue);
    }

    public static void Put_Long(String Name, long Value) {
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putLong(Name, Value);
        editor.commit();
    }

    public static long Get_Long(String Name, long Defvalue) {
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences(TAG, 0);
        return share.getLong(Name, Defvalue);
    }
}
