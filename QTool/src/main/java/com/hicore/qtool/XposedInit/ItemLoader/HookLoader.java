package com.hicore.qtool.XposedInit.ItemLoader;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.qtool.XPWork.BaseHookItem;
import com.hicore.qtool.XPWork.PLInit;
import com.hicore.qtool.XposedInit.HookEnv;

import java.util.Enumeration;
import java.util.LinkedHashMap;

import dalvik.system.DexFile;

public class HookLoader {
    private static final String TAG = "HookLoader";
    private static LinkedHashMap<String,BaseHookItem> loadHookInstances;
    private static LinkedHashMap<String,PLInit> PLInitInstances;
    public static void SearchAndLoadAllHook(){
        try {//从模块的Dex文件中搜索到所有Hook实例类并进行加载
            DexFile dexFile = new DexFile(HookEnv.ToolApkPath);
            ClassLoader mLoader = BaseHookItem.class.getClassLoader();
            Enumeration classNames = dexFile.entries();
            while (classNames.hasMoreElements()){
                try{
                    String clzName = (String) classNames.nextElement();
                    if (clzName.startsWith(BaseHookItem.class.getPackage().getName())){
                        Class clz = mLoader.loadClass(clzName);
                        if (BaseHookItem.class.isAssignableFrom(clz)){
                            LogUtils.debug(TAG,"Found hook instance class "+clzName+",try load it.");
                            try{
                                BaseHookItem item = (BaseHookItem) clz.newInstance();
                                item.startHook();
                                loadHookInstances.put(clzName,item);
                                LogUtils.debug(TAG,"Load hook instance "+clzName+" success.");
                            }catch (Throwable e){
                                LogUtils.error(TAG,"Load hook instance "+clzName+" failed.");
                            }
                        }if (PLInit.class.isAssignableFrom(clz)){
                            LogUtils.debug(TAG,"Found ConfInit instance class "+clzName+",try load it.");
                            try{
                                PLInit instance;
                                if (loadHookInstances.containsValue(clzName)){
                                    instance = (PLInit) loadHookInstances.get(clzName);
                                }else {
                                    instance = (PLInit) clz.newInstance();
                                }
                                instance.StartInit();
                                LogUtils.debug(TAG,"Load ConfInit instance "+clzName+" success.");
                            }catch (Throwable e){
                                LogUtils.error(TAG,"Load ConfInit instance "+clzName+" failed.");
                            }

                        }

                    }
                }catch (Exception e){ }

            }

        } catch (Throwable e) {
            LogUtils.fetal_error(TAG,"Can't search and load hook:\n"+ Log.getStackTraceString(e));
        }
    }
    public static void CallAllDelayHook(){
        for (BaseHookItem item : loadHookInstances.values()){
            try{
                item.startDelayHook();
            }catch (Throwable th){
                LogUtils.error(TAG,"Can't load DelayHook for clz:" + item.getClass().getName());
            }
        }

        for (PLInit item : PLInitInstances.values()){
            try{
                item.StartDelayInit();
            }catch (Throwable th){
                LogUtils.error(TAG,"Can't invoke PLInit for clz:" + item.getClass().getName());
            }
        }
    }
}
