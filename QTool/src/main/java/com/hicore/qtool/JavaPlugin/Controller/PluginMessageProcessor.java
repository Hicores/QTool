package com.hicore.qtool.JavaPlugin.Controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginMessageProcessor {
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static void onMessage(Object msg){
        executor.submit(()->onMessage0(msg));
    }
    public static void submit(Runnable run){
        executor.submit(run);
    }
    private static void onMessage0(Object msg){

    }
    public static void onMuteEvent(String GroupUin,String UserUin,String OPUin,long time){

    }
    public static void onExitEvent(String GroupUin,String UserUin,String OPUin){

    }
    public static void onJoinEvent(String GroupUin,String UserUin,String Invitor,String AcceptOPUin){

    }
    public static void onRevoke(Object msg){

    }
    public static void onRequestJoin(String GroupUin,String UserUin,String Invitor,String source,String ans,String raw_ans,Object callback){

    }
    private static PluginMessage decodeGuildMsg(Object msg){
        return null;
    }
    private static PluginMessage decodeCommonMsg(Object msg){
        return null;
    }
}
