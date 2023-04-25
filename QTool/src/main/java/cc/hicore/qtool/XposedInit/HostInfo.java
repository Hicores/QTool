package cc.hicore.qtool.XposedInit;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.qtool.HookEnv;

//获取QQ的版本号信息
public class HostInfo {
    private static final String TAG = "HostInfo";
    private static String Version;
    private static int Version_Code;

    protected static void Init() {
        try {
            //获取普通apk版本信息
            PackageManager pm = HookEnv.AppContext.getPackageManager();
            PackageInfo mSelfInfo = pm.getPackageInfo("com.tencent.mobileqq", 0);
            String Version_1 = mSelfInfo.versionName;

            //获取QQ小版本信息
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq", PackageManager.GET_META_DATA);
            String UUID = sAppInfo.metaData.getString("com.tencent.rdm.uuid");

            Version = Version_1;
            Version_Code = Integer.parseInt(UUID.substring(0, UUID.indexOf("_")));
        } catch (Throwable e) {
            LogUtils.fetal_error(TAG, e);
        }
    }

    public static int getVerCode() {
        return Version_Code;
    }

    public static String getVersion() {
        return Version;
    }

    public static boolean checkIsGrayQQ(){
        try {
            PackageManager pm = HookEnv.AppContext.getPackageManager();
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq", PackageManager.GET_META_DATA);
            String Settings = sAppInfo.metaData.getString("AppSetting_params");
            if (Settings.contains("#Gray"))return true;
        }catch (Exception ignored) {}
        return false;
    }
}
