package com.hicore.Utils;

import java.io.File;
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
    public static String ReadFileString(File f){
        try{
            FileInputStream fInp = new FileInputStream(f);
            String Content = new String(DataUtils.readAllBytes(fInp),StandardCharsets.UTF_8);
            fInp.close();
            return Content;
        }catch (Exception e){
            return null;
        }
    }
    public static String ReadFileString(String f){
        return ReadFileString(new File(f));
    }
}
