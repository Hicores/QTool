package cc.hicore.qtool.JavaPlugin.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class PluginErrorOutput {
    //输出错误信息到对应的插件目录中
    public static synchronized void Print(String RootPath, String Msg) {
        try {
            File f = new File(RootPath, "error_track.txt");
            if (f.isDirectory()) f.delete();
            FileOutputStream fOut = new FileOutputStream(f, true);
            fOut.write((Msg + "\n").getBytes(StandardCharsets.UTF_8));
            fOut.close();
        } catch (Exception e) {

        }

    }
}
