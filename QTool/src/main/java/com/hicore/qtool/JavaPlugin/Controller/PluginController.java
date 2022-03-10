package com.hicore.qtool.JavaPlugin.Controller;

import android.util.Log;

import com.hicore.ReflectUtils.MClass;
import com.hicore.Utils.FileUtils;
import com.hicore.Utils.NameUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

public class PluginController {
    private static HashMap<String,PluginInfo> runningInfo = new HashMap<>();

    public static boolean IsRunning(String PluginID){
        for(String VerifyID : runningInfo.keySet()){
            PluginInfo info = runningInfo.get(VerifyID);
            if (PluginID.equals(info.PluginID))return info.IsRunning;
        }
        return false;
    }
    public static boolean LoadOnce(PluginInfo info){
        try{
            if (IsRunning(info.PluginID)){
                Utils.ShowToast("已经有相同ID的脚本在加载了,请修改ID再重试");
                return false;
            }
            info.PluginVerifyID = NameUtils.getRandomString(32);
            info.Instance = new Interpreter();
            info.IsLoading = true;
            info.IsBlackMode = PluginSetController.IsBlackMode(info.PluginID);
            info.ListStr = PluginSetController.getModeList(info.PluginID);
            File mainJava = new File(info.LocalPath,"main.java");
            if (!mainJava.exists()){
                Utils.ShowToast("当前脚本目录不存在 main.java 文件,无法加载");
                return false;
            }
            String fileContent = FileUtils.ReadFileString(mainJava);
            runningInfo.put(info.PluginVerifyID,info);
            LoadFirst(info);
            LoadInner(fileContent,mainJava.getAbsolutePath(),info.PluginVerifyID);
            return true;
        }catch (Throwable th){
            forceEnd(info);
            Utils.ShowToast("脚本加载错误,已停止执行:\n"+Log.getStackTraceString(th));
            return false;
        }
    }
    public static void endPlugin(PluginInfo Info){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(()-> InvokeToPlugin(Info.Instance,"onUnload"));
        int count = 0;
        while (count < 100){
            count++;
            if (future.isDone()){
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        forceEnd(Info);

    }
    private static void forceEnd(PluginInfo info){
        info.IsRunning = false;
        info.Instance.getNameSpace().clear();
        runningInfo.remove(info.PluginVerifyID);
    }
    private static String checkAndRemoveNode(String Content){
        return Content;
    }
    private static void LoadFirst(PluginInfo info){

    }
    public static void LoadInner(String FileContent,String LocalPath,String BandVerifyID) throws Exception {
        String LoadContent = checkAndRemoveNode(FileContent);
        PluginInfo info = runningInfo.get(BandVerifyID);
        Interpreter instance = info.Instance;
        instance.set("context", HookEnv.AppContext);
        instance.set("PluginID",BandVerifyID);
        instance.set("SDKVer",10);
        instance.set("loader",HookEnv.mLoader);
        instance.set("AppPath",info.LocalPath);

        instance.eval(LoadContent);
    }
    public static void checkAndInvoke(String GroupUin,int type,String MethodName,Object... param){
        for(String VerifyID : runningInfo.keySet()){
            PluginInfo info = runningInfo.get(VerifyID);
            if (info.IsRunning){
                if (info.IsAvailable(GroupUin,type)){
                    try{
                        InvokeToPlugin(info.Instance,MethodName,param);
                    }catch (RuntimeException runtime){
                        Throwable cause = runtime.getCause();
                        PluginErrorOutput.Print(info.LocalPath, Log.getStackTraceString(cause));
                    }

                }
            }
        }
    }
    private static void InvokeToPlugin(Interpreter Instance,String MethodName, Object... param){
        try {
            NameSpace space = Instance.getNameSpace();
            Class[] clz = new Class[param.length];
            for (int i=0;i< param.length;i++)clz[i] = param[i].getClass();

            Loop:
            for (BshMethod method : space.getMethods()){
                if (method.getName().equals(MethodName)){
                    Class[] params = method.getParameterTypes();
                    if (params.length == clz.length){
                        for (int i=0;i<param.length;i++){
                            if (!MClass.CheckClass(params[i],clz[i])) continue Loop;
                        }
                        method.invoke(param,Instance);
                        return;
                    }
                }

            }
        }catch (Throwable th){
            throw new RuntimeException(th);
        }
    }
}
