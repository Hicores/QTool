package com.hicore.LogUtils;

import com.hicore.qtool.HookEnv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import de.robv.android.xposed.XposedBridge;

public class LogOutputFile {
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 1 << 1;
    public static final int LEVEL_WARNING = 1 << 2;
    public static final int LEVEL_ERROR = 1 << 3;
    public static final int LEVEL_FETAL_ERROR = 1 << 4;

    public static int Current_LEVEL = LEVEL_DEBUG;
    private BufferedWriter writer;
    private String LogPath;
    private AtomicInteger ErrCount = new AtomicInteger(-1);


    private static HashMap<Integer,LogOutputFile> instance = new HashMap<>();
    static {
        //初始化日志保存的目录
        if (!new File(HookEnv.ExtraDataPath + "Log/").exists()) new File(HookEnv.ExtraDataPath + "Log/").mkdirs();
        if (HookEnv.ExtraDataPath != null){
            instance.put(LEVEL_DEBUG, new LogOutputFile(HookEnv.ExtraDataPath + "Log/Debug.log"));
            instance.put(LEVEL_INFO,new LogOutputFile(HookEnv.ExtraDataPath + "Log/Info.log"));
            instance.put(LEVEL_WARNING,new LogOutputFile(HookEnv.ExtraDataPath + "Log/Warning.log"));
            instance.put(LEVEL_ERROR,new LogOutputFile(HookEnv.ExtraDataPath + "Log/Error.log"));
            instance.put(LEVEL_FETAL_ERROR,new LogOutputFile(HookEnv.ExtraDataPath + "Log/FetalError.log"));
        }

    }
    public LogOutputFile(String Path){
        //这里只保存目录,在首次写入日志时才会创建对应的Writer实例
        LogPath = Path;
    }
    private void print(String Text){
        if (CheckIsAvailable()){
            try {
                writer.write(Text);
                writer.flush();
            } catch (IOException e) {
                XposedBridge.log(e);
            }
        }
    }
    public static void Print(int Level,String Text){
        if (Level < Current_LEVEL)return;
        if (!instance.containsKey(Level))return;
        LogOutputFile out = instance.get(Level);
        out.print(Text);
    }
    private boolean CheckIsAvailable(){
        try {
            if (ErrCount.get() > 10)return false;
            writer.newLine();
            writer.flush();
            return true;
        } catch (Exception e) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LogPath,true)));

                writer.newLine();
                writer.flush();
                return true;
            } catch (IOException ioe) {
                XposedBridge.log(ioe);
                ErrCount.getAndIncrement();
                return false;
            }
        }
    }
}
