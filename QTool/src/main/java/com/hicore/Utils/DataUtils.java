package com.hicore.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

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
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16).toUpperCase();
    }
}
