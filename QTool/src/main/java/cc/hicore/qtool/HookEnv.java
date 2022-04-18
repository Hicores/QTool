package cc.hicore.qtool;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import cc.hicore.ConfigUtils.ConfigCore;
import cc.hicore.ConfigUtils.ConfigCore_Json;

public class HookEnv {
    public static ClassLoader mLoader;
    public static ClassLoader moduleLoader;

    public static String ProcessName;

    public static String ExtraDataPath;
    public static String AppApkPath;
    public static String ToolApkPath;

    public static Context AppContext;
    public static Application Application;

    public static boolean IsMainProcess;
    public static ConfigCore Config = new ConfigCore_Json();
    public static SharedPreferences globalConfig;

    public static Object AppInterface;
    public static Object SessionInfo;
}
