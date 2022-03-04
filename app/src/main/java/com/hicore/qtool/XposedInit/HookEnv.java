package com.hicore.qtool.XposedInit;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.hicore.ConfigUtils.ConfigCore;

public class HookEnv {
    public static ClassLoader mLoader;
    public static String ProcessName;

    public static String ExtraDataPath;
    public static String AppApkPath;
    public static String ToolApkPath;

    public static Context AppContext;

    public static boolean IsMainProcess;
    public static ConfigCore Config;
    public static SharedPreferences globalConfig;
}
