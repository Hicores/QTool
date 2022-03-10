package com.hicore.qtool.JavaPlugin.Controller;

import android.util.Log;

import com.hicore.ReflectUtils.MClass;

import java.util.HashMap;

import bsh.BshMethod;
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
    public static void LoadOnce(PluginInfo info){

    }
    public static void LoadInner(String FileContent,String LocalPath,String BandPluginID){

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
