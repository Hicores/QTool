package com.hicore.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataUtils {
    public static byte[] readAllBytes(InputStream inp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inp.read(buffer))!=-1)out.write(buffer,0,read);
        return out.toByteArray();
    }
    public static byte[] HexToByteArray(String hex){
        if(hex.length()%2!=0){
            hex="0"+hex;
        }
        byte[] result=new byte[hex.length()/2];
        for(int i=0;i<hex.length();i+=2){
            result[i/2]=(byte)Integer.parseInt(hex.substring(i,i+2),16);
        }
        return result;
    }
    public static String ByteArrayToHex(byte[] bytes){
        String result="";
        for(byte b:bytes){
            result=result+Integer.toHexString(b&0xff);
        }
        return result;
    }
}
