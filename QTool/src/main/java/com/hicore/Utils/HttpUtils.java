package com.hicore.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpUtils {
    public static String getContent(String Path){
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(Path).openConnection();
            InputStream ins = connection.getInputStream();
            String Content = new String(DataUtils.readAllBytes(ins), StandardCharsets.UTF_8);
            ins.close();
            return Content;
        }catch (Exception e){
            return null;
        }
    }
    public static boolean DownloadToFile(String url,String local){
        try {
            File parent = new File(local).getParentFile();
            if (!parent.exists())parent.mkdirs();
            FileOutputStream fOut = new FileOutputStream(local);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream ins = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = ins.read(buffer))!=-1){
                fOut.write(buffer,0,read);
            }
            fOut.close();
            ins.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
