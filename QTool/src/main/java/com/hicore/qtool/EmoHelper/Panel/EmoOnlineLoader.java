package com.hicore.qtool.EmoHelper.Panel;

import android.os.Handler;
import android.os.Looper;

import com.hicore.Utils.DataUtils;
import com.hicore.Utils.HttpUtils;
import com.hicore.qtool.EmoHelper.CloudSync.SyncCore;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XposedInit.EnvHook;

import java.io.File;

public class EmoOnlineLoader {
    public static void submit(EmoPanel.EmoInfo info, Runnable run){
        SyncCore.syncThread.submit(()->{
            EnvHook.requireCachePath();
            String CacheDir = HookEnv.ExtraDataPath + "/Cache/img_"+info.MD5;
            if (info.MD5.equals(DataUtils.getFileMD5(new File(CacheDir)))){
                info.Path = CacheDir;
                new Handler(Looper.getMainLooper()).post(run);
                return;
            }
            new File(CacheDir).delete();

            HttpUtils.DownloadToFile(info.URL,CacheDir);
            info.Path = CacheDir;
            new Handler(Looper.getMainLooper()).post(run);
        });
    }
}
