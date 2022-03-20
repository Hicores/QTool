package com.hicore.ConfigUtils;

import com.hicore.qtool.HookEnv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ConfigUtils_MapFile {
    private static final HashMap<String, MappedByteBuffer> bufferCache = new HashMap<>();
    public static synchronized String ReadFile(String ItemName){
        try{
            MappedByteBuffer buffer = bufferCache.get(ItemName);
            if (buffer == null){
                File par = new File(HookEnv.ExtraDataPath+"配置文件目录/");
                if (!par.exists())par.mkdirs();
                RandomAccessFile access = new RandomAccessFile(HookEnv.ExtraDataPath+"配置文件目录/"+ItemName,"rw");
                FileChannel channel = access.getChannel();
                buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,64 * 1024);

                bufferCache.put(ItemName,buffer);
            }
            buffer.clear();
            ByteArrayOutputStream write = new ByteArrayOutputStream();
            for (int i=0;i<64 * 1024;i++){
                byte read = buffer.get();
                if (read != 0) write.write(read);
                else break;
            }
            return write.toString();
        }catch (Exception e){
            return null;
        }

    }
    public static synchronized void WriteFile(String ItemName,String Content){
        try{
            MappedByteBuffer buffer = bufferCache.get(ItemName);
            if (buffer == null){
                File par = new File(HookEnv.ExtraDataPath+"配置文件目录/");
                if (!par.exists())par.mkdirs();
                RandomAccessFile access = new RandomAccessFile(HookEnv.ExtraDataPath+"配置文件目录/"+ItemName,"rw");
                FileChannel channel = access.getChannel();
                buffer = channel.map(FileChannel.MapMode.READ_WRITE,0,64 * 1024);

                bufferCache.put(ItemName,buffer);
            }
            buffer.clear();
            buffer.put(Content.getBytes(StandardCharsets.UTF_8));
            buffer.put((byte) 0);
        }catch (Exception ignored){
        }
    }
}

