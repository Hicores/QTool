package com.hicore.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static void WriteToFile(String File,String FileContent){
        try{
            FileOutputStream fOut = new FileOutputStream(File);
            fOut.write(FileContent.getBytes(StandardCharsets.UTF_8));
            fOut.close();
        }catch (Exception e){
        }
    }
    public static String ReadFileString(String File){
        try{
            FileInputStream fInp = new FileInputStream(File);
            String Content = new String(DataUtils.readAllBytes(fInp),StandardCharsets.UTF_8);
            fInp.close();
            return Content;
        }catch (Exception e){
            return null;
        }
    }
}
