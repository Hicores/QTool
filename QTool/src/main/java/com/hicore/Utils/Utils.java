package com.hicore.Utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hicore.ReflectUtils.InjectRes;
import com.hicore.ReflectUtils.MField;
import com.hicore.qtool.XposedInit.HookEnv;

import java.lang.reflect.Field;
import java.util.Map;

public class Utils {
    public static <T> void ShowToast(T Value){
        String ToastText = String.valueOf(Value);
        new Handler(Looper.getMainLooper()).post(()-> Toast.makeText(HookEnv.AppContext, ToastText, Toast.LENGTH_SHORT).show());
    }
    public static <T> void ShowToastL(T Value){
        String ToastText = String.valueOf(Value);
        new Handler(Looper.getMainLooper()).post(()-> Toast.makeText(HookEnv.AppContext, ToastText, Toast.LENGTH_LONG).show());
    }
    public static Activity getTopActivity(){
        try{
            Object ActivityThread = MField.GetStaticField(Class.forName("android.app.ActivityThread"),"sCurrentActivityThread");
            Map<?,?> activities = MField.GetField(ActivityThread,"mActivities");
            for(Object activityRecord: activities.values()){
                boolean isPause = MField.GetField(activityRecord,"paused",boolean.class);
                if (!isPause){
                    Activity act = MField.GetField(activityRecord,"activity");
                    InjectRes.StartInject(act);
                    return act;
                }
            }
        }catch (Exception e){}
        return null;
    }

}
