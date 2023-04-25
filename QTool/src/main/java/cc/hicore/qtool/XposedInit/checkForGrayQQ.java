package cc.hicore.qtool.XposedInit;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cc.hicore.qtool.HookEnv;

public class checkForGrayQQ {
    public static boolean isGrayQQ(){
        try {
            PackageManager pm = HookEnv.AppContext.getPackageManager();
            ApplicationInfo sAppInfo = pm.getApplicationInfo("com.tencent.mobileqq", PackageManager.GET_META_DATA);
            String AppSettings = sAppInfo.metaData.getString("AppSetting_params");
            if (AppSettings.contains("#Gray")){
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }

    }
}
