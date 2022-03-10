package com.hicore.LogUtils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
    public static void debug(String Tag,String text){
        LogOutputFile.Print(LogOutputFile.LEVEL_DEBUG,"["+format.format(new Date())+"]"+Tag+":"+text);
    }

    public static void error(String Tag,String text){
        LogOutputFile.Print(LogOutputFile.LEVEL_ERROR,"["+format.format(new Date())+"]"+Tag+":"+text);
    }
    public static void error(String Tag,Throwable t){
        error(Tag,Log.getStackTraceString(t));
    }
    public static void fetal_error(String Tag,String text){
        LogOutputFile.Print(LogOutputFile.LEVEL_FETAL_ERROR,"["+format.format(new Date())+"]"+Tag+":"+text);
    }
    public static void fetal_error(String Tag,Throwable t){
        fetal_error(Tag, Log.getStackTraceString(t));
    }
    public static void info(String Tag,String text){
        LogOutputFile.Print(LogOutputFile.LEVEL_INFO,"["+format.format(new Date())+"]"+Tag+":"+text);
    }
    public static void warning(String Tag,String text){
        LogOutputFile.Print(LogOutputFile.LEVEL_WARNING,"["+format.format(new Date())+"]"+Tag+":"+text);
    }

}
