package cc.hicore.Utils;

import java.lang.reflect.Field;
import java.util.Map;

import cc.hicore.LogUtils.LogUtils;
import de.robv.android.xposed.XposedBridge;

public class DebugUtils {
    public static String getCurrentCallStacks() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement ele : elements) {
            builder.append(ele.getClassName()).append(".")
                    .append(ele.getMethodName()).append("() (")
                    .append(ele.getFileName()).append(":")
                    .append(ele.getLineNumber()).append(")")
                    .append("\n");
        }
        return builder.toString();
    }
    public static void PrintAllField(Object o){
        for (Field f : o.getClass().getDeclaredFields()){
            try{

                f.setAccessible(true);
                XposedBridge.log(f.getName()+"->"+f.get(o));
            }catch (Exception ew){

            }
        }
    }
    public static void PrintAllField(Object o,Class clz){
        for (Field f : clz.getDeclaredFields()){
            try{
                f.setAccessible(true);
                XposedBridge.log(f.getName()+"->"+f.get(o));
            }catch (Exception ew){

            }
        }
    }
    public static void PrintAllThreadStack(){
        Map<Thread,StackTraceElement[]> thread = Thread.getAllStackTraces();
        for (Thread th : thread.keySet()){
            StackTraceElement[] element = thread.get(th);
            StringBuilder builder = new StringBuilder();
            builder.append(th.getName()).append("\n");
            for (StackTraceElement ele : element){
                builder.append(ele.getClassName())
                        .append(".").append(ele.getMethodName())
                        .append("(").append(ele.getFileName()).append(":").append(ele.getLineNumber()).append(")\n");
            }
            builder.append("\n\n");
            LogUtils.debug("ThreadPrinter",builder.toString());
        }
    }
    public static String getLinkStackMsg(Throwable th){
        StringBuilder builder = new StringBuilder();
        builder.append(th.toString());
        Throwable up = th.getCause();
        while (up != null){
            builder.append("\n--------------\n").append(up);
            up = up.getCause();
        }
        return builder.toString();
    }

}
