package com.tencent.rmonitor.looper;

import android.os.Handler;
import android.os.Looper;

import cc.hicore.qtool.DebugHelper.CatchInstance;

public class LooperProxy implements Runnable{
    public static void Proxy(){
        new Handler(Looper.getMainLooper())
                .post(new LooperProxy());
    }

    @Override
    public void run() {
        while (true){
            try {
                Looper.loop();
                break;
            }catch (Throwable e){
                CatchInstance.ICatchEx(Thread.currentThread(),e);
            }
        }
    }
}
