package com.hicore.Utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hicore.qtool.XposedInit.HookEnv;

public class Utils {
    public static <T> void ShowTaost(T Value){
        String ToastText = String.valueOf(Value);
        new Handler(Looper.getMainLooper()).post(()-> Toast.makeText(HookEnv.AppContext, ToastText, Toast.LENGTH_SHORT).show());
    }
}
