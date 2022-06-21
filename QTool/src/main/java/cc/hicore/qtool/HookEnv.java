package cc.hicore.qtool;

import android.app.Application;
import android.content.Context;
import android.os.HandlerThread;

import cc.hicore.ConfigUtils.ConfigCore;
import cc.hicore.ConfigUtils.ConfigCore_Json;

public class HookEnv {
    public static ClassLoader mLoader;
    public static ClassLoader moduleLoader;

    public static String ProcessName;

    public static String ExtraDataPath;
    public static String AppApkPath;
    public static String ToolApkPath;
    public static String AppPath;

    public static Context AppContext;
    public static Application Application;

    public static boolean IsMainProcess;
    public static ConfigCore Config = new ConfigCore_Json();

    public static Object AppInterface;
    public static Object SessionInfo;

    public static String New_Version;
    public static HandlerThread tasker = new HandlerThread("QTool_Handler");
    public static boolean isInSubMode = true;
    public static ClassLoader SubClassLoader;

    public static StringBuilder loadFailed = new StringBuilder();
    public static int CurrentApp = 1;
}
