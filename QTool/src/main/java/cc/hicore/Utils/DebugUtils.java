package cc.hicore.Utils;

import java.lang.reflect.Field;

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
}
