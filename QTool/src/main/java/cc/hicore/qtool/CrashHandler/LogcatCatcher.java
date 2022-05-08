package cc.hicore.qtool.CrashHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class LogcatCatcher {
    public static void startCatcherOnce(){
        CatchInstance.StartCatch();
    }
    public static void StartCatch() {
        new Thread(()->{
            try {
                Process proc = Runtime.getRuntime().exec("logcat");
                InputStreamReader reader = new InputStreamReader(proc.getInputStream());
                BufferedReader mReader = new BufferedReader(reader);
                String Line;
                while ((Line = mReader.readLine())!=null){
                    StringPool_Logcat.Add(Line);
                }

            } catch (IOException e) {
                StringPool_Logcat.Add("Can't execute logcat.");
            }
        }).start();
    }
}
