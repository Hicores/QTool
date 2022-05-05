package cc.hicore.qtool.DebugHelper;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.qtool.HookEnv;

public class LogcatCatcher {
    public static void startCatcherOnce(){
        if (HookEnv.IsMainProcess){
            if (GlobalConfig.Get_Boolean("Confirm_Output_Logcat",false)){
                if (GlobalConfig.Get_Boolean("Cancel_Output_Logcat",true)){
                    GlobalConfig.Put_Boolean("Confirm_Output_Logcat",false);
                }
                GlobalConfig.Put_Boolean("Cancel_Output_Logcat",true);
                start_logcat();
                start_xposed();
            }
        }
    }
    private static void start_logcat(){

    }
    private static void start_xposed(){

    }
}
