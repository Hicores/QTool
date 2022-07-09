package cc.hicore.HookItemLoader.core;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIClick;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseMethodInfo;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;

public class CoreLoader {
    public static final HashMap<Class<?>,XPItemInfo> allInstance = new HashMap<>();
    public static final HashMap<Class<?>,XPItemInfo> clzInstance = new HashMap<>();

    public static class XPItemInfo{
        public Object Instance;

        public HashMap<String,String> ExecutorException = new HashMap<>();
        public ArrayList<String> cacheException = new ArrayList<>();

        public XPItem item;

        public boolean isVersionAvailable;
        public boolean isLoadEarly;
        public boolean isEnabled = true;

        public boolean isApi;
        public Method apiExecutor;

        public HashMap<String,BaseMethodInfo> NeedMethodInfo = new HashMap<>();
        public HashMap<String, Member> scanResult = new HashMap<>();

        public ArrayList<Method> fitMethods = new ArrayList<>();

        public UIInfo ui;
        public Method uiClick;

        public String ItemName;
        public String id;
    }
    static {
        ClassLoader loader = CoreLoader.class.getClassLoader();
        try {
            Class<?> clzItemInfo = loader.loadClass("cc.hicore.HookItemLoader.bridge.XPItems");
            Field f = clzItemInfo.getField("XPItems");
            ArrayList<String> ItemInfo = (ArrayList<String>) f.get(null);
            for (String clzName : ItemInfo){
                try {
                    Class<?> ItemClz = loader.loadClass(clzName);
                    Object newInstance = ItemClz.newInstance();
                    XPItemInfo newInfo = new XPItemInfo();
                    newInfo.Instance = newInstance;
                    newInfo.id = ItemClz.getName();
                    allInstance.put(ItemClz,newInfo);
                }catch (Exception e){

                }
            }
        } catch (Exception e) {

        }

    }
    public static void onBeforeLoad(){
        //扫描所有的类要求版本号
        for (Class<?> clz : allInstance.keySet()){
            XPItemInfo info = allInstance.get(clz);
            XPItem item = clz.getAnnotation(XPItem.class);

            if (item != null && info != null){
                if (item.proc() == XPItem.PROC_MAIN){
                    info.isVersionAvailable = HookEnv.IsMainProcess && checkVersionAvailable(item.targetVer(),item.targetApp(),item.max_targetVer());
                }else if (item.proc() == XPItem.PROC_ALL){
                    info.isVersionAvailable = checkVersionAvailable(item.targetVer(),item.targetApp(),item.max_targetVer());
                }
                info.ItemName = item.name();
                info.isApi = item.itemType() == XPItem.ITEM_Api;
                if (info.isVersionAvailable){
                    clzInstance.put(clz,info);
                }
                info.item = item;
                info.isLoadEarly = item.period() == XPItem.Period_Early;
            }
            try{
                for (Field f :clz.getDeclaredFields()){
                    if (f.getType().equals(XPItemInfo.class)){
                        f.setAccessible(true);
                        f.set(info.Instance,info);
                    }
                }
            }catch (Exception e){
                info.cacheException.add(Log.getStackTraceString(e));
            }

        }
        //扫描有效的方法并将其加入到方法列表中
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            for (Method m : clz.getDeclaredMethods()){
                VerController controller = m.getAnnotation(VerController.class);
                if (controller != null && info != null){
                    if (checkVersionAvailable(controller.targetVer(),controller.targetApp(),controller.max_targetVer())){
                        info.fitMethods.add(m);
                    }
                }
            }
        }
        //扫描所有的方法查找信息
        for (XPItemInfo info : clzInstance.values()){
            for (Method m :  info.fitMethods){
                //查找方法扫描信息
                MethodScanner controller = m.getAnnotation(MethodScanner.class);
                if (controller != null){
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == MethodContainer.class) {
                        MethodContainer container = new MethodContainer();
                        try {
                            m.invoke(info.Instance, container);
                            for (BaseMethodInfo methodInfo : container.getInfo()){
                                methodInfo.bandToInfo = info;
                                info.NeedMethodInfo.put(methodInfo.id,methodInfo);
                            }
                        } catch (Throwable th) {
                            info.cacheException.add(Log.getStackTraceString(th));
                        }
                    }
                }
                //查找UI显示信息
                UIItem ui = m.getAnnotation(UIItem.class);
                if (ui != null){
                    if (m.getReturnType().equals(UIInfo.class)) {
                        try {
                            info.ui = (UIInfo) m.invoke(info.Instance);
                            info.ui.connectTo = info;
                            info.isEnabled = HookEnv.Config.getBoolean("Main_Switch",info.id,false);
                        } catch (Throwable th) {
                            info.cacheException.add(Log.getStackTraceString(th));
                        }
                    }
                }

                //查找UIClick信息
                UIClick click = m.getAnnotation(UIClick.class);
                if (click != null){
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(Context.class)) {
                        info.uiClick = m;
                    }
                }

                //查找ApiExecutor
                ApiExecutor executor = m.getAnnotation(ApiExecutor.class);
                if (executor != null){
                    info.apiExecutor = m;
                }
            }
        }
        if (MethodScannerWorker.checkIsAvailable()){
            XPHookInstance(true);
            CommonExecutorWorker(true);
        }
    }
    public static void onAfterLoad(){
        if (MethodScannerWorker.checkIsAvailable()){
            XPHookInstance(false);
            CommonExecutorWorker(false);
        }else {
            MethodScannerWorker.doFindMethod();
        }
    }
    private static void XPHookInstance(boolean isEarly){
        for (XPItemInfo info : clzInstance.values()){
            for (Method m : info.fitMethods){
                XPExecutor executor = m.getAnnotation(XPExecutor.class);
                if (executor != null && (info.isLoadEarly == isEarly)){
                    try{

                        Member hookMethod = info.scanResult.get(executor.methodID());
                        BaseXPExecutor baseXPExecutor = (BaseXPExecutor) m.invoke(info.Instance);
                        Assert.notNull(hookMethod,"hookMethod is NULL,for "+m.getName());
                        Assert.notNull(baseXPExecutor,"baseXPExecutor is NULL,for "+m.getName());
                        if (executor.period() == XPExecutor.After){
                            XPBridge.HookAfter(hookMethod,param -> {
                                if (info.isEnabled){
                                    try{
                                        baseXPExecutor.onInvoke(param);
                                    }catch (Throwable th){
                                        info.ExecutorException.put(m.getName(),Log.getStackTraceString(th));
                                    }
                                }
                            }, executor.hook_period());
                        }else {
                            XPBridge.HookBefore(hookMethod,param -> {
                                if (info.isEnabled){
                                    try{
                                        baseXPExecutor.onInvoke(param);
                                    }catch (Throwable th){
                                        info.ExecutorException.put(m.getName(),Log.getStackTraceString(th));
                                    }
                                }
                            }, executor.hook_period());
                        }
                    }catch (Throwable e){
                        info.cacheException.add(Log.getStackTraceString(e));
                    }
                }
            }
        }
    }
    private static void CommonExecutorWorker(boolean isEarly){
        for (XPItemInfo info : clzInstance.values()){
            for (Method m : info.fitMethods){
                CommonExecutor executor = m.getAnnotation(CommonExecutor.class);
                if (executor != null && (info.isLoadEarly == isEarly)){
                    try{
                        m.invoke(info.Instance);
                    }catch (Throwable e){
                        info.cacheException.add(Log.getStackTraceString(e));
                    }
                }
            }
        }
    }
    private static boolean checkVersionAvailable(int version,int targetApp,int max_version){
        //检测App是否符合
        if ((targetApp & HookEnv.CurrentApp) == 0)return false;
        //检测最大版本号是否符合
        if (max_version > 1){
            if (HostInfo.getVerCode() >= max_version)return false;
        }
        //检测目标版本号是否符合
        if (version > 1){
            return HostInfo.getVerCode() >= version;
        }else {
            return true;
        }
    }
}
