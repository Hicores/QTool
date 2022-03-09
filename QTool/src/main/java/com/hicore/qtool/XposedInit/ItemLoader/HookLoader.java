package com.hicore.qtool.XposedInit.ItemLoader;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MField;
import com.hicore.qtool.HookEnv;

import java.util.ArrayList;
import java.util.HashMap;

public class HookLoader {
    private static final String TAG = "HookLoader";

    private static ArrayList<String> BasicInit;
    private static ArrayList<String> DelayInit;
    private static ArrayList<String> runOnAllProc;
    private static ArrayList<String> runOnMainProc;

    private static HashMap<String,BaseHookItem> cacheHookInst = new HashMap<>();

    public static void SearchAndLoadAllHook(){
        try {
            LogUtils.debug(TAG,"Search for hook to load start.");
            ClassLoader mLoader = HookLoader.class.getClassLoader();
            Class findClz = mLoader.loadClass("com.hicore.qtool.XposedInit.ItemLoader.MItemInfo");
            BasicInit = MField.GetField(null,findClz,"BasicInit",ArrayList.class);
            DelayInit = MField.GetField(null,findClz,"DelayInit",ArrayList.class);
            runOnAllProc = MField.GetField(null,findClz,"runOnAllProc",ArrayList.class);
            runOnMainProc = MField.GetField(null,findClz,"runOnMainProc",ArrayList.class);
            for (String clzName : BasicInit){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        LogUtils.debug(TAG,"Found BasicInit class:"+clzName+" and load to cache.");
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : DelayInit){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        LogUtils.debug(TAG,"Found DelayInit class:"+clzName+" and load to cache.");
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : runOnAllProc){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        LogUtils.debug(TAG,"Found runOnAllProc class:"+clzName+" and load to cache.");
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : runOnMainProc){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        LogUtils.debug(TAG,"Found runOnMainProc class:"+clzName+" and load to cache.");
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }

            if (HookEnv.IsMainProcess){
                for (String clzName : runOnMainProc){
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                        try{
                            item.setTryLoad();
                            item.setLoad(item.startHook());
                        }catch (Throwable th){
                            LogUtils.error(TAG,"An error happen when invoke "+clzName+".startHook:\n"+Log.getStackTraceString(th));
                        }

                    }
                }

                for (String clzName : runOnAllProc){
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                        try{
                            item.setTryLoad();
                            item.setLoad(item.startHook());
                        }catch (Throwable th){
                            LogUtils.error(TAG,"An error happen when invoke "+clzName+".startHook:\n"+Log.getStackTraceString(th));
                        }
                    }
                }
            }
            else {
                for (String clzName : runOnAllProc){
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){

                        try{
                            item.setTryLoad();
                            item.setLoad(item.startHook());
                        }catch (Throwable th){
                            LogUtils.error(TAG,"An error happen when invoke "+clzName+".startHook:\n"+Log.getStackTraceString(th));
                        }
                    }
                }
            }

        } catch (Throwable e) {
            LogUtils.fetal_error(TAG,"Can't search and load hook:\n"+ Log.getStackTraceString(e));
        }
    }
    public static void CallAllDelayHook(){
        if (HookEnv.IsMainProcess){
            for (String clzName : runOnMainProc){
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                    try{
                        item.setTryLoad();
                        item.setLoad(item.startHook());
                    }catch (Throwable th){
                        LogUtils.error(TAG,"An error happen when invoke "+clzName+".startHook:\n"+Log.getStackTraceString(th));
                    }
                }
            }

            for (String clzName : runOnAllProc){
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                    try{
                        item.setTryLoad();
                        item.setLoad(item.startHook());
                    }catch (Throwable th){
                        LogUtils.error(TAG,"An error happen when invoke "+clzName+".startHook:\n"+Log.getStackTraceString(th));
                    }
                }
            }
        }
        else {
            for (String clzName : runOnAllProc){
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                    try{
                        item.setTryLoad();
                        item.setLoad(item.startHook());
                    }catch (Throwable th){
                        LogUtils.error(TAG,"An error happen when invoke "+clzName+".startHook:\n"+Log.getStackTraceString(th));
                    }
                }
            }
        }
    }
    public static ArrayList<CheckResult> CheckForItemsStatus(){
        ArrayList<CheckResult> result = new ArrayList<>();
        for (String clzName : cacheHookInst.keySet()){
            CheckResult subResult = new CheckResult();
            subResult.ClassName = clzName;
            BaseHookItem item = cacheHookInst.get(clzName);
            if (item != null){
                subResult.Name = item.getTag();
                try{
                    subResult.IsAvailable = item.check();
                    subResult.IsEnable = item.isEnable();
                    subResult.ErrorInfo = item.getErrorInfo();
                }catch (Throwable th){
                    subResult.IsAvailable = false;
                    subResult.ErrorInfo = th.toString();
                }
                result.add(subResult);
            }
        }
        return result;
    }
    public static void CallHookStart(String ClzName){
        BaseHookItem item = cacheHookInst.get(ClzName);
        if (!item.isLoaded()){
            item.setTryLoad();
            item.setLoad(item.startHook());
        }
    }

    public static class CheckResult{
        public String Name;
        public String ClassName;
        public boolean IsAvailable;
        public boolean IsEnable;
        public String ErrorInfo;
    }
}
