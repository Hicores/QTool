package cc.hicore.Utils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncUtils {
    public interface syncResult{
        void onSuccess();
        void onException(Throwable t);
        void onTimeout();
    }
    private static class threadInfo{
        private Thread runThread;
        private long timeOut;
    }
    private final static HashMap<String,threadInfo> cacheThreadInfo = new HashMap<>();
    static AtomicBoolean isObserveAlive = new AtomicBoolean();
    public static void ObserveThread(){

    }
    public static synchronized void postTask(String TaskName,Runnable task,syncResult callBack){
        Thread newTask = new Thread(() -> {
            try{
                task.run();
                callBack.onSuccess();
            }catch (Throwable th){
                callBack.onException(th);
            }
        });
        newTask.setName(TaskName);
        newTask.start();

    }
}
