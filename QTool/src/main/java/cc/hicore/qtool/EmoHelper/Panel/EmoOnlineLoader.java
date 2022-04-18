package cc.hicore.qtool.EmoHelper.Panel;

import android.os.Handler;
import android.os.Looper;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.qtool.EmoHelper.CloudSync.SyncCore;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.EnvHook;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmoOnlineLoader {
    static ExecutorService savePool = Executors.newFixedThreadPool(16);
    public static void submit(EmoPanel.EmoInfo info, Runnable run){
        SyncCore.syncThread.submit(()->{
            try{
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
            }catch (Throwable th){
                new Handler(Looper.getMainLooper()).post(run);
            }

        });
    }
    public static void submit2(EmoPanel.EmoInfo info, Runnable run){
        savePool.submit(()->{
            try{
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
            }catch (Throwable th){
                new Handler(Looper.getMainLooper()).post(run);
            }

        });
    }
}
