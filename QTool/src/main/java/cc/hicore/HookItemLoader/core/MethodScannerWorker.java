package cc.hicore.HookItemLoader.core;

import android.content.Context;

import java.util.ArrayList;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.HookItemLoader.bridge.BaseMethodInfo;
import cc.hicore.HookItemLoader.bridge.CommonMethodInfo;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.HostInfo;

public class MethodScannerWorker {
    public static class ScannerLink{
        public String ID;
        public ArrayList<String> LinkingID;
        public String LinkToID;
    }
    public static boolean checkIsAvailable(){
        String QQVer = HostInfo.getVersion()+"."+HostInfo.getVerCode();
        if (GlobalConfig.Get_String("cacheQQVer").equals(QQVer)){
            return true;
        }
        for (CoreLoader.XPItemInfo info : CoreLoader.clzInstance.values()){
            if (info.isVersionAvailable && info.NeedMethodInfo != null){
                for (BaseMethodInfo methodInfo : info.NeedMethodInfo){
                    if (methodInfo instanceof CommonMethodInfo) continue;
                    return false;
                }
            }
        }
        return true;
    }
    public static void doFindMethod(){
        Context context = Utils.getTopActivity();
    }
}
