package cc.hicore.Utils;

public class ThreadUtils {
    public static void PostCommonTask(Runnable runnable){
        new Thread(runnable).start();
    }
}
