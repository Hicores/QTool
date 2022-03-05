package com.hicore.ConfigUtils;

import android.view.View;

import com.hicore.qtool.XposedInit.HookEnv;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ConfigUtils_MapFile {
    private static HashMap<String, MappedByteBuffer> bufferCache = new HashMap<>();
    public static synchronized String ReadFile(String ItemName){
        try{
            MappedByteBuffer buffer = bufferCache.get(ItemName);
            if (buffer == null){
                RandomAccessFile access = new RandomAccessFile(HookEnv.ExtraDataPath+"配置文件目录/"+ItemName,"rw");
                FileChannel channel = access.getChannel();
                buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,64 * 1024);

                bufferCache.put(ItemName,buffer);
            }
            buffer.clear();
            byte[] bArr = new byte[64 * 1024];
            buffer.get(bArr);
            String str = new String(bArr, StandardCharsets.UTF_8);
            str = str.substring(0,str.indexOf(0));
            return str;
        }catch (Exception e){
            return null;
        }

    }
    public static synchronized void WriteFile(String ItemName,String Content){
        try{
            MappedByteBuffer buffer = bufferCache.get(ItemName);
            if (buffer == null){
                RandomAccessFile access = new RandomAccessFile(HookEnv.ExtraDataPath+"配置文件目录/"+ItemName,"rw");
                FileChannel channel = access.getChannel();
                buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,64 * 1024);

                bufferCache.put(ItemName,buffer);
            }
            buffer.clear();
            buffer.put(Content.getBytes(StandardCharsets.UTF_8));
            buffer.put((byte) 0);
        }catch (Exception e){
        }
    }
}

