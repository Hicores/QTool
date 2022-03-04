package com.hicore.Utils;

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
}
