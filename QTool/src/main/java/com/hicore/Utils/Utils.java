package com.hicore.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hicore.ReflectUtils.ResUtils;
import com.hicore.ReflectUtils.MField;
import com.hicore.qtool.HookEnv;

import java.util.Map;

public class Utils {
    public static int dip2px(Context context, float dpValue) {
        if(dpValue>0)
        {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }else
        {
            float f = -dpValue;
            final float scale = context.getResources().getDisplayMetrics().density;
            return -(int) (f * scale + 0.5f);
        }

    }

    public static int dip2sp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density /
                context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }
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
                    ResUtils.StartInject(act);
                    return act;
                }
            }
        }catch (Exception e){}
        return null;
    }

}
