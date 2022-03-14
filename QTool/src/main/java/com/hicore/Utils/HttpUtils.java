package com.hicore.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
    public static String PostForResult(String URL,String key,byte[] buffer,int size){
        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestProperty("key",key);
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(buffer,0,size);
            out.flush();
            out.close();

            InputStream ins = connection.getInputStream();
            byte[] result = DataUtils.readAllBytes(ins);
            ins.close();
            return new String(result);
        }catch (Exception e){
            return "";
        }
    }
}
