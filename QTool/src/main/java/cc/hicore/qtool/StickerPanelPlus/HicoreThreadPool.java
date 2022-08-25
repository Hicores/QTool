package cc.hicore.qtool.StickerPanelPlus;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class HicoreThreadPool {
    private static class ThreadInfo implements Runnable{
        Thread thread;
        boolean isIdle;
        String workID;
        @Override
        public void run() {
            try {
                AtomicInteger idleCount = new AtomicInteger();
                while (true){
                    if (idleCount.getAndIncrement() > 100)break;
                    ThreadTaskInfo peekInfo = taskList.poll();
                    if (peekInfo != null){
                        try {
                            isIdle = false;
                            workID = peekInfo.BandToID;
                            peekInfo.runnable.run();
                            workID = "";
                        }catch (Throwable th){
                            Log.w("HicoreThreadPool",Log.getStackTraceString(th));
                        }finally {
                            isIdle = true;
                            Thread.interrupted();
                        }
                    }else {
                        Thread.sleep(100);
                    }
                }
            }catch (Exception e){

            }finally {
                threadCache.remove(this);
            }
        }
    }
    private static final List<ThreadInfo> threadCache = Collections.synchronizedList(new ArrayList<>());
    private static final Queue<ThreadTaskInfo> taskList = new ConcurrentLinkedDeque<>();
    public static class ThreadTaskInfo{
        public String BandToID;
        public Runnable runnable;
    }
    public static void PostWork(ThreadTaskInfo info){
        if (info == null)return;
        taskList.add(info);
        ThreadFactory();
    }
    private static void ThreadFactory(){
        if ((threadCache.size() < 32 && taskList.size() > 5) || threadCache.size() < 5){
            ThreadInfo newInfo = new ThreadInfo();
            newInfo.thread = new Thread(newInfo);
            newInfo.thread.start();
            threadCache.add(newInfo);
        }
    }
    public static void stopWork(String ID){
        for (ThreadInfo info : threadCache){
            if (info.workID.equals(ID)){
                info.thread.interrupt();
            }
        }
        taskList.removeIf(info -> info.BandToID.equals(ID));
    }
}
