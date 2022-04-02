package com.hicore.qtool.VoiceHelper.Panel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class LocalVoiceSearchHelper {
    public static ArrayList<VoiceProvider.FileInfo> searchForPath(String LocalPath,boolean isRoot){
        ArrayList<VoiceProvider.FileInfo> resultArr = new ArrayList<>();
        if (!isRoot){
            VoiceProvider.FileInfo toLast = new VoiceProvider.FileInfo();
            toLast.type = -1;
            toLast.Name = "..";
            resultArr.add(toLast);
        }

        File[] fs = new File(LocalPath).listFiles();
        if (fs == null)return resultArr;


        Arrays.sort(fs, (o1, o2) -> {
            if (o1.isFile() && o2.isDirectory())return 1;
            if (o1.isDirectory() && o2.isFile())return -1;
            return 0;
        });
        for (File f : fs){
            if (f.isDirectory()){
                VoiceProvider.FileInfo newInfo = new VoiceProvider.FileInfo();
                newInfo.Path = f.getAbsolutePath();
                newInfo.Name = f.getName();
                newInfo.type = 2;
                resultArr.add(newInfo);
            }
            if (f.isFile()){
                VoiceProvider.FileInfo newInfo = new VoiceProvider.FileInfo();
                newInfo.Path = f.getAbsolutePath();
                newInfo.Name = f.getName();
                newInfo.type = 1;
                resultArr.add(newInfo);
            }
        }
        return resultArr;
    }
    public static ArrayList<VoiceProvider.FileInfo> searchForName(String LocalPath,String Name){
        ArrayList<VoiceProvider.FileInfo> newInfo = new ArrayList<>();
        File[] fs = new File(LocalPath).listFiles();
        if (fs == null)return newInfo;

        for (File f : fs){
            if (f.isDirectory()){
                newInfo.addAll(searchForName(f.getAbsolutePath(),Name));
            }else if (f.isFile()){
                String name = f.getName();
                if (name.contains(Name)){
                    VoiceProvider.FileInfo newItem = new VoiceProvider.FileInfo();
                    newItem.type = 1;
                    newItem.Name = f.getName();
                    newItem.Path = f.getAbsolutePath();
                    newInfo.add(newItem);
                }
            }
        }
        return newInfo;
    }
}
