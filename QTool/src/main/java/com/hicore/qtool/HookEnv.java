package com.hicore.qtool;

import android.content.Context;
import android.content.SharedPreferences;


import com.hicore.ConfigUtils.ConfigCore;
import com.hicore.qtool.XposedInit.HookEntry;

public class HookEnv {
    public static ClassLoader mLoader;
    public static HookEntry.FixSubClassLoader fixLoader;
    public static ClassLoader moduleLoader;

    public static String ProcessName;

    public static String ExtraDataPath;
    public static String AppApkPath;
    public static String ToolApkPath;

    public static Context AppContext;

    public static boolean IsMainProcess;
    public static ConfigCore Config;
    public static SharedPreferences globalConfig;
}
