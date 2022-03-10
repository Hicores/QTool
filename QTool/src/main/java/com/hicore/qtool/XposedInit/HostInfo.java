package com.hicore.qtool.XposedInit;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hicore.LogUtils.LogUtils;
import com.hicore.qtool.HookEnv;

public class HostInfo {
    private static final String TAG = "HostInfo";
    private static String Version;
    private static int Version_Code;
    protected static void Init(){
        try {
            //获取普通apk版本信息
            PackageManager pm = HookEnv.AppContext.getPackageManager();
            PackageInfo mSelfInfo = pm.getPackageInfo("com.tencent.mobileqq",0);
            String Version_1 = mSelfInfo.versionName;

            //获取QQ小版本信息
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq",PackageManager.GET_META_DATA);
            String UUID = sAppInfo.metaData.getString("com.tencent.rdm.uuid");

            Version = Version_1+"."+UUID.substring(0,UUID.indexOf("_"));
            Version_Code = Integer.parseInt(UUID.substring(0,UUID.indexOf("_")));
        } catch (Throwable e) {
            LogUtils.fetal_error(TAG,e);
        }
    }
    public static int getVerCode(){
        return Version_Code;
    }
}
