package cc.hicore.Utils;

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
}
