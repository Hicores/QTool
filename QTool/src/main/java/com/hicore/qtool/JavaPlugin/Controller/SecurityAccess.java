package com.hicore.qtool.JavaPlugin.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SecurityAccess {
    public static boolean checkAccess(String Token,String RequestText){
        long AccessExpiredTime = HookEnv.Config.getLong("Security_Access",Token,0);
        if (AccessExpiredTime > System.currentTimeMillis()){
            return true;
        }
        return requestAccess(Token,RequestText);
    }
    private static boolean requestAccess(String Token,String RequestText){
        if (Thread.currentThread().getName().equals("main")){
            Utils.ShowToast("在主线程中无法调用授权操作");
            return false;
        }
        AtomicBoolean clickEnd = new AtomicBoolean();
        AtomicInteger clickResult = new AtomicInteger();
        new Handler(Looper.getMainLooper())
                .post(()->{
                    AlertDialog dialog = new AlertDialog.Builder(Utils.getTopActivity(),3)
                            .setTitle("授权提示")
                            .setMessage(RequestText)
                            .setNegativeButton("拒绝", (dialog1, which) -> {
                                clickEnd.getAndSet(true);
                                clickResult.getAndSet(1);
                            }).setNeutralButton("同意", (dialog12, which) -> {
                                clickEnd.getAndSet(true);
                                clickResult.getAndSet(2);
                            }).setOnDismissListener(dialog13 -> {
                                clickEnd.getAndSet(true);
                            })
                            .create();
                    dialog.getWindow().getDecorView().setFilterTouchesWhenObscured(true);
                    dialog.show();
                });


        for (int i=0;i<100;i++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (clickEnd.get())break;
        }
        if (clickResult.get() == 2){
            HookEnv.Config.setLong("Security_Access",Token,System.currentTimeMillis() + 7 * 24 * 3600 * 1000);//授权7天有效
            cleanAccessExpire();
        }

        return clickResult.get() == 2;
    }
    private static void cleanAccessExpire(){
        String[] keys = HookEnv.Config.getKeys("Security_Access");
        for (String key : keys){
            long ExpireTime = HookEnv.Config.getLong("Security_Access",key,0);
            if (ExpireTime < System.currentTimeMillis()){
                HookEnv.Config.removeKey("Security_Access",key);
            }
        }
    }
}
