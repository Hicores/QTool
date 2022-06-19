package cc.hicore.HookItemLoader.core;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIClick;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseMethodInfo;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;
import de.robv.android.xposed.XposedBridge;

public class CoreLoader {
    protected static final HashMap<Class<?>,XPItemInfo> allInstance = new HashMap<>();
    protected static final HashMap<Class<?>,XPItemInfo> clzInstance = new HashMap<>();

    protected static class XPItemInfo{
        Object Instance;

        HashMap<String,String> ExecutorException = new HashMap<>();
        HashMap<String,String> CheckerException = new HashMap<>();
        ArrayList<String> cacheException = new ArrayList<>();

        boolean isVersionAvailable;
        boolean isLoadEarly;
        boolean ScannerSuccess;
        boolean isEnabled = true;

        HashSet<BaseMethodInfo> NeedMethodInfo = new HashSet<>();
        ArrayList<Method> scanResult = new ArrayList<>();

        HashSet<Method> XPExecutors = new HashSet<>();

        UIInfo ui;
        Method uiClick;

        String ItemName;
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
                    allInstance.put(ItemClz,newInfo);
                }catch (Exception e){

                }
            }
        } catch (Exception e) {

        }

    }
    public static void onBeforeLoad(){
        //扫描所有的类要求版本号
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            XPItem item = clz.getAnnotation(XPItem.class);
            if (item != null && info != null){
                if (item.proc() == XPItem.PROC_MAIN){
                    info.isVersionAvailable = HookEnv.IsMainProcess && checkVersionAvailable(item.target(),item.isStrict());
                }else if (item.proc() == XPItem.PROC_ALL){
                    info.isVersionAvailable = checkVersionAvailable(item.target(),item.isStrict());
                }
                info.ItemName = item.name();
                if (info.isVersionAvailable){
                    clzInstance.put(clz,info);
                }
                info.isLoadEarly = item.period() == XPItem.Period_Early;
            }
        }
        //扫描所有需要的方法查找内容
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            //排序类
            AnnoScanSort<MethodScanner> sort = new AnnoScanSort<MethodScanner>() {
                int maxVer = 0;
                MethodScanner scannerAnno;
                @Override
                public void onResult(Method m, MethodScanner Anno) {
                    if (maxVer < Anno.target() && HostInfo.getVerCode() >= Anno.target()){
                        maxVer = Anno.target();
                        scannerAnno=Anno;
                    }
                }
                @Override
                public List<MethodScanner> onGetResult() {
                    ArrayList<MethodScanner> scanner = new ArrayList<>();
                    if (scannerAnno != null){
                        scanner.add(scannerAnno);
                    }
                    return scanner;
                }
            };
            AnnoScanResult<MethodScanner> methodCollector = (m, Anno) -> {
                if (checkVersionAvailable(Anno.target(), Anno.isStrict())) {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0] == MethodContainer.class) {
                        MethodContainer container = new MethodContainer();
                        try {
                            m.invoke(info.Instance, container);
                            info.NeedMethodInfo.addAll(container.getInfo());
                        } catch (Throwable th) {
                            info.cacheException.add(Log.getStackTraceString(th));
                        }
                    }
                }
            };
            ScanAnnotation(clz,MethodScanner.class,methodCollector,true,sort);
        }
        //扫描所有的UI信息内容
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            //排序类
            AnnoScanSort<UIItem> sort = new AnnoScanSort<UIItem>() {
                int maxVer = 0;
                UIItem scannerAnno;
                @Override
                public void onResult(Method m, UIItem Anno) {
                    if (maxVer < Anno.target() && HostInfo.getVerCode() >= Anno.target()){
                        maxVer = Anno.target();
                        scannerAnno=Anno;
                    }
                }
                @Override
                public List<UIItem> onGetResult() {
                    ArrayList<UIItem> scanner = new ArrayList<>();
                    if (scannerAnno != null){
                        scanner.add(scannerAnno);
                    }
                    return scanner;
                }
            };
            AnnoScanResult<UIItem> methodCollector = (m, Anno) -> {
                if (checkVersionAvailable(Anno.target(), Anno.isStrict())) {
                    if (m.getReturnType().equals(UIInfo.class)) {
                        try {
                            info.ui = (UIInfo) m.invoke(info.Instance);
                        } catch (Throwable th) {
                            info.cacheException.add(Log.getStackTraceString(th));
                        }
                    }
                }
            };
            ScanAnnotation(clz,UIItem.class,methodCollector,true,sort);
        }
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            //排序类
            AnnoScanSort<UIClick> sort = new AnnoScanSort<UIClick>() {
                int maxVer = 0;
                UIClick scannerAnno;
                @Override
                public void onResult(Method m, UIClick Anno) {
                    if (maxVer < Anno.target() && HostInfo.getVerCode() >= Anno.target()){
                        maxVer = Anno.target();
                        scannerAnno=Anno;
                    }
                }
                @Override
                public List<UIClick> onGetResult() {
                    ArrayList<UIClick> scanner = new ArrayList<>();
                    if (scannerAnno != null){
                        scanner.add(scannerAnno);
                    }
                    return scanner;
                }
            };
            AnnoScanResult<UIClick> methodCollector = (m, Anno) -> {
                XposedBridge.log(m.getDeclaringClass().getName()+"."+m.getName());
                if (checkVersionAvailable(Anno.target(), Anno.isStrict())) {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(Context.class)) {
                        info.uiClick = m;
                    }
                }
            };
            ScanAnnotation(clz,UIClick.class,methodCollector,true,sort);
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
    private static void XPHookInstance(boolean isBefore){
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            if (isBefore != info.isLoadEarly) continue;
            AnnoScanResult<XPExecutor> xpExecutor = (m, Anno) -> {
                int index = Anno.index();
                if (index < info.scanResult.size() && info.scanResult.get(index) != null){
                    if (m.getReturnType().equals(BaseXPExecutor.class)){
                        try {
                            BaseXPExecutor executor = (BaseXPExecutor) m.invoke(info.Instance);
                            if (Anno.period() == XPExecutor.Before){
                                XPBridge.HookBefore(info.scanResult.get(index),param -> {
                                    try{
                                        if (info.isEnabled) {
                                            executor.onInvoke(param);
                                        }
                                    }catch (Throwable th){
                                        info.ExecutorException.put(m.getName(),Log.getStackTraceString(th));
                                    }
                                });
                            }else {
                                XPBridge.HookAfter(info.scanResult.get(index),param -> {
                                    try{
                                        if (info.isEnabled) {
                                            executor.onInvoke(param);
                                        }
                                    }catch (Throwable th){
                                        info.ExecutorException.put(m.getName(),Log.getStackTraceString(th));
                                    }
                                });
                            }
                        } catch (Exception e) {
                            info.ExecutorException.put(m.getName(),Log.getStackTraceString(e));
                        }
                    }
                }
            };
            AnnoScanSort<XPExecutor> sort = new AnnoScanSort<XPExecutor>() {
                final ArrayList<XPExecutor> executors = new ArrayList<>();
                int targetVer = 0;
                @Override
                public void onResult(Method m, XPExecutor Anno) {
                    if (targetVer < Anno.target() && HostInfo.getVerCode() >= Anno.target()){
                        targetVer = Anno.target();
                        executors.clear();
                    }
                    executors.add(Anno);
                }

                @Override
                public List<XPExecutor> onGetResult() {
                    return executors;
                }
            };
            ScanAnnotation(clz,XPExecutor.class,xpExecutor,true,sort);
        }

    }
    private static void CommonExecutorWorker(boolean isBefore){
        for (Class<?> clz : clzInstance.keySet()){
            XPItemInfo info = clzInstance.get(clz);
            if (isBefore != info.isLoadEarly) continue;
            AnnoScanResult<CommonExecutor> xpExecutor = (m, Anno) -> {
                try {
                    m.invoke(info.Instance);
                }catch (Exception e){

                }
            };
            AnnoScanSort<CommonExecutor> sort = new AnnoScanSort<CommonExecutor>() {
                final ArrayList<CommonExecutor> executors = new ArrayList<>();
                int targetVer = 0;
                @Override
                public void onResult(Method m, CommonExecutor Anno) {
                    if (targetVer < Anno.target() && HostInfo.getVerCode() >= Anno.target()){
                        targetVer = Anno.target();
                        executors.clear();
                    }
                    executors.add(Anno);
                }

                @Override
                public List<CommonExecutor> onGetResult() {
                    return executors;
                }
            };
            ScanAnnotation(clz,CommonExecutor.class,xpExecutor,true,sort);
        }
    }
    private static boolean checkVersionAvailable(int version,boolean isStrict){
        if (version > 1){
            if (isStrict){
                return HostInfo.getVerCode() == version;
            }else {
                return HostInfo.getVerCode() >= version;
            }
        }else {
            return true;
        }
    }
    private static <An> void ScanAnnotation(@NotNull  Class<?> ScanClz, Class<An> annoInfo, AnnoScanResult<An> onResult,boolean isSort,AnnoScanSort<An> sortCallback){
        if (!isSort){
            for (Method m : ScanClz.getDeclaredMethods()){
                Annotation[] annos = m.getDeclaredAnnotations();
                for (Annotation anno : annos){
                    if (anno.getClass().isAnnotation() && annoInfo.equals(anno.getClass())){
                        onResult.onResult(m, (An) anno);
                    }
                }
            }
        }else {
            HashMap<An,Method> result = new HashMap<>();
            for (Method m : ScanClz.getDeclaredMethods()){
                Annotation[] annos = m.getDeclaredAnnotations();
                for (Annotation anno : annos){
                    if (annoInfo.isAssignableFrom(anno.getClass())){
                        sortCallback.onResult(m, (An) anno);
                        result.put((An) anno,m);
                    }
                }
            }
            List<An> mPreResult = sortCallback.onGetResult();
            for (An anno : mPreResult){
                Method m = result.get(anno);
                onResult.onResult(m,anno);
            }
        }

    }
    interface AnnoScanResult<T>{
        void onResult(Method m,T Anno);
    }
    interface AnnoScanSort<T>{
        void onResult(Method m,T Anno);
        List<T> onGetResult();
    }
}
