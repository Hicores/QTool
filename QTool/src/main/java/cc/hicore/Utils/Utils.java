package cc.hicore.Utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Map;

import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.qtool.HookEnv;

public class Utils {
    public static int dip2px(Context context, float dpValue) {
        if (dpValue > 0) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } else {
            float f = -dpValue;
            final float scale = context.getResources().getDisplayMetrics().density;
            return -(int) (f * scale + 0.5f);
        }

    }
    public static String secondToTime(long second) {
        if(second == 0) return "0秒";
        long days = second / 86400;
        second = second % 86400;
        long hours = second / 3600;
        second = second % 3600;
        long minutes = second / 60;
        second = second % 60;
        return(days == 0 ? "" : days + "天") + (hours == 0 ? "" : hours + "小时") + (minutes == 0 ? "" : minutes + "分钟") + (second == 0 ? "" : second + "秒");
    }
    public static String secondToTime2(long second) {
        if(second == 0) return "0秒";
        long days = second / 86400;
        second = second % 86400;
        long hours = second / 3600;
        return(days == 0 ? "" : days + "天") + (hours == 0 ? "" : hours + "小时");
    }
    public static boolean getDarkModeStatus(Context context) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static int dip2sp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density /
                context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }

    public static <T> void ShowToast(T Value) {
        String ToastText = String.valueOf(Value);
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(HookEnv.AppContext, ToastText, Toast.LENGTH_SHORT).show());
    }

    public static <T> void ShowToastL(T Value) {
        String ToastText = String.valueOf(Value);
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(HookEnv.AppContext, ToastText, Toast.LENGTH_LONG).show());
    }

    public static Activity getTopActivity() {
        try {
            Object ActivityThread = MField.GetStaticField(Class.forName("android.app.ActivityThread"), "sCurrentActivityThread");
            Map<?, ?> activities = MField.GetField(ActivityThread, "mActivities");
            for (Object activityRecord : activities.values()) {
                boolean isPause = MField.GetField(activityRecord, "paused", boolean.class);
                if (!isPause) {
                    Activity act = MField.GetField(activityRecord, "activity");
                    ResUtils.StartInject(act);
                    return act;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static final int GB = 1024 * 1024 * 1024;
    //定义MB的计算常量
    private static final int MB = 1024 * 1024;
    //定义KB的计算常量
    private static final int KB = 1024;

    public static String bytes2kb(long bytes) {
        DecimalFormat format = new DecimalFormat("###.00");
        if (bytes / GB >= 1) {
            return format.format((double) bytes / GB) + "GB";
        } else if (bytes / MB >= 1) {
            return format.format((double) bytes / MB) + "MB";
        } else if (bytes / KB >= 1) {
            return format.format((double) bytes / KB) + "KB";
        } else {
            return bytes + "字节";
        }
    }

    public static void SetTextClipboard(String str) {
        ClipboardManager manager = (ClipboardManager) HookEnv.AppContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text", str);
        manager.setPrimaryClip(data);
        Thread.currentThread();
    }
    public static void PostToMain(Runnable run){
        new Handler(Looper.getMainLooper()).post(run);
    }

}
