package com.hicore.qtool.XposedInit;

import android.content.Context;

import androidx.annotation.NonNull;

public class HookEnv {
    public static ClassLoader mLoader;
    public static String ProcessName;

    public static String ExtraDataPath;
    public static String AppApkPath;
    public static String ToolApkPath;

    public static Context AppContext;

    public static boolean IsMainProcess;
}
