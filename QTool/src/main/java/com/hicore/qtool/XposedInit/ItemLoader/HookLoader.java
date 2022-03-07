package com.hicore.qtool.XposedInit.ItemLoader;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MField;
import com.hicore.qtool.XPWork.BaseHookItem;
import com.hicore.qtool.XposedInit.HookEnv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HookLoader {
    private static final String TAG = "HookLoader";
    private static LinkedHashMap<String,BaseHookItem> loadHookInstances;

    private static ArrayList<String> BasicInit;
    private static ArrayList<String> DelayInit;
    private static ArrayList<String> runOnAllProc;
    private static ArrayList<String> runOnMainProc;

    private static HashMap<String,BaseHookItem> cacheHookInst = new HashMap<>();

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
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : DelayInit){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : runOnAllProc){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)){
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName,mItem);
                    }
                }
            }
            for (String clzName : runOnMainProc){
                if (!cacheHookInst.containsKey(clzName)){
                    Class clz = mLoader.loadClass(clzName);
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
                        item.startHook();
                    }
                }

                for (String clzName : runOnAllProc){
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                        item.startHook();
                    }
                }
            }
            else {
                for (String clzName : runOnAllProc){
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                        item.startHook();
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
                    item.startHook();
                }
            }

            for (String clzName : runOnAllProc){
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                    item.startHook();
                }
            }
        }
        else {
            for (String clzName : runOnAllProc){
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()){
                    item.startHook();
                }
            }
        }
    }
}
