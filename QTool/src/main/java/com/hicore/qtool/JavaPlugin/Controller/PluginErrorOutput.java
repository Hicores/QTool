package com.hicore.qtool.JavaPlugin.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class PluginErrorOutput {
    public static synchronized void Print(String RootPath,String Msg){
        try{
            File f = new File(RootPath,"error_track.txt");
            if (f.isDirectory())f.delete();
            FileOutputStream fOut = new FileOutputStream(f,true);
            fOut.write((Msg+"\n").getBytes(StandardCharsets.UTF_8));
            fOut.close();
        }catch (Exception e){

        }

    }
}
