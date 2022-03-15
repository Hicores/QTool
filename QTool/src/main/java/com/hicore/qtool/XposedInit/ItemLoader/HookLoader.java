package com.hicore.qtool.XposedInit.ItemLoader;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XPWork.BaseMenu.MainMenu.MainMenu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HookLoader {
    public static class UiInfo{
        public int type;
        public int Position;
        public String title;
        public String desc;
        public String ClzName;
        public String ID;
        public boolean IsCheckDef;
    }
    private static final String TAG = "HookLoader";

    private static ArrayList<String> BasicInit;
    private static ArrayList<String> DelayInit;
    private static ArrayList<String> runOnAllProc;
    private static ArrayList<String> runOnMainProc;

    private static HashMap<String,BaseHookItem> cacheHookInst = new HashMap<>();
    private static HashMap<String,BaseUiItem> cacheUiItem = new HashMap<>();

    public static void SearchAndLoadAllHook(){
        try {
            ClassLoader mLoader = HookLoader.class.getClassLoader();
            Class findClz = mLoader.loadClass("com.hicore.qtool.XposedInit.ItemLoader.MItemInfo");
            BasicInit = MField.GetField(null,findClz,"BasicInit",ArrayList.class);
            DelayInit = MField.GetField(null,findClz,"DelayInit",ArrayList.class);
            runOnAllProc = MField.GetField(null,findClz,"runOnAllProc",ArrayList.class);
            runOnMainProc = MField.GetField(null,findClz,"runOnMainProc",ArrayList.class);
            for (String clzName : BasicInit){
                if (!cacheHookInst.containsKey(clzName)){
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : DelayInit){
                if (!cacheHookInst.containsKey(clzName)){
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : runOnAllProc){
                if (!cacheHookInst.containsKey(clzName)){
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : runOnMainProc){
                if (!cacheHookInst.containsKey(clzName)){
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
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
    public static HashSet<UiInfo> getUiInfos(){
        try{
            ClassLoader mLoader = HookLoader.class.getClassLoader();
            Class findClz = mLoader.loadClass("com.hicore.qtool.XposedInit.ItemLoader.UiItemInfo");
            HashSet<UiInfo> NewUiList = MMethod.CallMethod(null,findClz,"getUiInfos",HashSet.class,new Class[0]);
            return NewUiList;
        }catch (Exception e){
            return new HashSet<>();
        }

    }
    public static BaseUiItem searchForUiInstance(String clzName){
        if (cacheUiItem.containsKey(clzName))return cacheUiItem.get(clzName);
        if (cacheHookInst.containsKey(clzName)){
            Object obj = cacheHookInst.get(clzName);
            if (obj instanceof BaseUiItem){
                BaseUiItem item = (BaseUiItem) obj;
                cacheUiItem.put(clzName,item);
                return item;
            }
            return null;
        }else {
            try{
                Class<?> clz = HookLoader.class.getClassLoader().loadClass(clzName);
                Object NewObj = clz.newInstance();
                if (NewObj instanceof BaseUiItem){
                    BaseUiItem item = (BaseUiItem) NewObj;
                    cacheUiItem.put(clzName,item);
                    return item;
                }
                return null;
            }catch (Exception e){
                return null;
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
                    subResult.IsLoaded = item.isLoaded();
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
            try {
                item.setLoad(item.startHook());
            } catch (Throwable th) {
                LogUtils.error(TAG,"An error happen when invoke "+ClzName+".startHook:\n"+Log.getStackTraceString(th));
            }
        }
    }

    public static class CheckResult{
        public String Name;
        public String ClassName;
        public boolean IsAvailable;
        public boolean IsEnable;
        public boolean IsLoaded;
        public String ErrorInfo;
    }
}
