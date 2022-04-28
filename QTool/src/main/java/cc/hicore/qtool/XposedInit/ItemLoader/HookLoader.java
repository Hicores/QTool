package cc.hicore.qtool.XposedInit.ItemLoader;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XposedBridge;

public class HookLoader {
    public static class UiInfo {
        public int type;
        public int Position;
        public String title;
        public String desc;
        public String ClzName;
        public String ID;
        public boolean IsCheckDef;

        public BaseUiItem UIInstance;
    }

    private static final String TAG = "HookLoader";

    private static ArrayList<String> BasicInit;
    private static ArrayList<String> DelayInit;
    private static ArrayList<String> runOnAllProc;
    private static ArrayList<String> runOnMainProc;

    private static HashMap<String, BaseHookItem> cacheHookInst = new HashMap<>();
    private static LinkedHashMap<String, BaseUiItem> cacheUiItem = new LinkedHashMap<>();

    //获取被注解的类,并通过newInstance进行加载,所以需要动态加载的Hook对象一定要是public的,不能是其他属性
    public static void SearchAndLoadAllHook() {
        try {
            ClassLoader mLoader = HookLoader.class.getClassLoader();
            Class findClz = mLoader.loadClass("cc.hicore.qtool.XposedInit.ItemLoader.MItemInfo");
            BasicInit = MField.GetField(null, findClz, "BasicInit", ArrayList.class);
            DelayInit = MField.GetField(null, findClz, "DelayInit", ArrayList.class);
            runOnAllProc = MField.GetField(null, findClz, "runOnAllProc", ArrayList.class);
            runOnMainProc = MField.GetField(null, findClz, "runOnMainProc", ArrayList.class);
            for (String clzName : BasicInit) {
                if (!cacheHookInst.containsKey(clzName)) {
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)) {
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName, mItem);
                    }
                }
            }
            for (String clzName : DelayInit) {
                if (!cacheHookInst.containsKey(clzName)) {
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)) {
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName, mItem);
                    }
                }
            }
            for (String clzName : runOnAllProc) {
                if (!cacheHookInst.containsKey(clzName)) {
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)) {
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName, mItem);
                    }
                }
            }
            for (String clzName : runOnMainProc) {
                if (!cacheHookInst.containsKey(clzName)) {
                    Class<?> clz = mLoader.loadClass(clzName);
                    if (BaseHookItem.class.isAssignableFrom(clz)) {
                        BaseHookItem mItem = (BaseHookItem) clz.newInstance();
                        cacheHookInst.put(clzName, mItem);
                    }
                }
            }

            if (HookEnv.IsMainProcess) {
                for (String clzName : runOnMainProc) {
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()) {
                        try {
                            item.setTryLoad();
                            item.setLoad(item.startHook());
                        } catch (Throwable th) {
                            LogUtils.error(TAG, "An error happen when invoke " + clzName + ".startHook:\n" + Log.getStackTraceString(th));
                        }

                    }
                }

                for (String clzName : runOnAllProc) {
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()) {
                        try {
                            item.setTryLoad();
                            item.setLoad(item.startHook());
                        } catch (Throwable th) {
                            LogUtils.error(TAG, "An error happen when invoke " + clzName + ".startHook:\n" + Log.getStackTraceString(th));
                        }
                    }
                }
            } else {
                for (String clzName : runOnAllProc) {
                    BaseHookItem item = cacheHookInst.get(clzName);
                    if (BasicInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()) {

                        try {
                            item.setTryLoad();
                            item.setLoad(item.startHook());
                        } catch (Throwable th) {
                            LogUtils.error(TAG, "An error happen when invoke " + clzName + ".startHook:\n" + Log.getStackTraceString(th));
                        }
                    }
                }
            }
            InitUIHookInstance();

        } catch (Throwable e) {
            LogUtils.fetal_error(TAG, "Can't search and load hook:\n" + Log.getStackTraceString(e));
        }
    }

    //初始化一次UI数据,这样在模块刚启动时就能加载需要开关的Hook了
    public static void InitUIHookInstance() {
        LinkedHashSet<UiInfo> uiInfos = HookLoader.getUiInfos();
        for (UiInfo NewInfo : uiInfos) {
            NewInfo.UIInstance = searchForUiInstance(NewInfo.ClzName);
            if (NewInfo.UIInstance != null) {
                NewInfo.UIInstance.SwitchChange(HookEnv.Config.getBoolean("Main_Switch", NewInfo.ID, false));
            }
        }
    }

    //延迟Hook
    public static void CallAllDelayHook() {
        if (HookEnv.IsMainProcess) {
            for (String clzName : runOnMainProc) {
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()) {
                    try {
                        item.setTryLoad();
                        item.setLoad(item.startHook());
                    } catch (Throwable th) {
                        LogUtils.error(TAG, "An error happen when invoke " + clzName + ".startHook:\n" + Log.getStackTraceString(th));
                    }
                }
            }

            for (String clzName : runOnAllProc) {
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()) {
                    try {
                        item.setTryLoad();
                        item.setLoad(item.startHook());
                    } catch (Throwable th) {
                        LogUtils.error(TAG, "An error happen when invoke " + clzName + ".startHook:\n" + Log.getStackTraceString(th));
                    }
                }
            }
        } else {
            for (String clzName : runOnAllProc) {
                BaseHookItem item = cacheHookInst.get(clzName);
                if (DelayInit.contains(clzName) && item != null && item.isEnable() && !item.isLoaded()) {
                    try {
                        item.setTryLoad();
                        item.setLoad(item.startHook());
                    } catch (Throwable th) {
                        LogUtils.error(TAG, "An error happen when invoke " + clzName + ".startHook:\n" + Log.getStackTraceString(th));
                    }
                }
            }
        }
    }

    //获取所有的Ui信息,以供显示在主菜单界面
    public static LinkedHashSet<UiInfo> getUiInfos() {
        try {
            ClassLoader mLoader = HookLoader.class.getClassLoader();
            Class findClz = mLoader.loadClass("cc.hicore.qtool.XposedInit.ItemLoader.UiItemInfo");
            LinkedHashSet<UiInfo> NewUiList = MMethod.CallMethod(null, findClz, "getUiInfos", LinkedHashSet.class, new Class[0]);
            return NewUiList;
        } catch (Exception e) {
            XposedBridge.log(e);
            return new LinkedHashSet<>();
        }

    }

    //通过类名扫描得到其对应的UI信息
    public static BaseUiItem searchForUiInstance(String clzName) {
        if (cacheUiItem.containsKey(clzName)) return cacheUiItem.get(clzName);
        if (cacheHookInst.containsKey(clzName)) {
            Object obj = cacheHookInst.get(clzName);
            if (obj instanceof BaseUiItem) {
                BaseUiItem item = (BaseUiItem) obj;
                cacheUiItem.put(clzName, item);
                return item;
            }
            return null;
        } else {
            try {
                Class<?> clz = HookLoader.class.getClassLoader().loadClass(clzName);
                Object NewObj = clz.newInstance();
                if (NewObj instanceof BaseUiItem) {
                    BaseUiItem item = (BaseUiItem) NewObj;
                    cacheUiItem.put(clzName, item);
                    return item;
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }

    //扫码所有被注解的HookItem类并调用其获取其状态
    public static ArrayList<CheckResult> CheckForItemsStatus() {
        ArrayList<CheckResult> result = new ArrayList<>();
        for (String clzName : cacheHookInst.keySet()) {
            CheckResult subResult = new CheckResult();
            subResult.ClassName = clzName;
            BaseHookItem item = cacheHookInst.get(clzName);
            if (item != null) {
                subResult.Name = item.getTag();
                try {
                    subResult.IsAvailable = item.check();
                    subResult.IsEnable = item.isEnable();
                    subResult.ErrorInfo = item.getErrorInfo();
                    subResult.IsLoaded = item.isLoaded();
                } catch (Throwable th) {
                    subResult.IsAvailable = false;
                    subResult.ErrorInfo = th.toString();
                }
                result.add(subResult);
            }
        }
        return result;
    }

    //插到指定的HookItem类并尝试进行加载,一般用于主菜单界面从未打开到打开进行动态挂钩
    public static void CallHookStart(String ClzName) {
        BaseHookItem item = cacheHookInst.get(ClzName);
        if (item != null) {
            if (!HookEnv.IsMainProcess) {
                if (runOnAllProc.contains(ClzName)) {
                    if (!item.isLoaded()) {
                        item.setTryLoad();
                        try {
                            item.setLoad(item.startHook());
                        } catch (Throwable th) {
                            LogUtils.error(TAG, "An error happen when invoke " + ClzName + ".startHook:\n" + Log.getStackTraceString(th));
                        }
                    }
                }
            } else {
                if (!item.isLoaded()) {
                    item.setTryLoad();
                    try {
                        item.setLoad(item.startHook());
                    } catch (Throwable th) {
                        LogUtils.error(TAG, "An error happen when invoke " + ClzName + ".startHook:\n" + Log.getStackTraceString(th));
                    }
                }
            }
        }


    }

    public static class CheckResult {
        public String Name;
        public String ClassName;
        public boolean IsAvailable;
        public boolean IsEnable;
        public boolean IsLoaded;
        public String ErrorInfo;
    }
}
