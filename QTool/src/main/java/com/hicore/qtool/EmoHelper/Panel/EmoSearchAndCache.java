package com.hicore.qtool.EmoHelper.Panel;

import com.hicore.qtool.HookEnv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class EmoSearchAndCache {
    public static ArrayList<EmoPanel.EmoInfo> searchForEmo(String PathName){
        String rawPath = HookEnv.ExtraDataPath + "/Pic/"+PathName;
        String CachePath = HookEnv.ExtraDataPath + "/Pic/"+PathName+".info";

        File[] fs = new File(rawPath).listFiles();


        ArrayList<EmoPanel.EmoInfo> findEmoInfos = new ArrayList<>();
        if (fs != null){
            Arrays.sort(fs, (o1, o2) -> {
                long result = o2.lastModified() - o1.lastModified();
                if (result == 0)return 0;
                return (result < 0 ) ? -1 : 1;
            });

            for (File f : fs){
                if (f.isFile() && !f.getName().contains(".")){
                    EmoPanel.EmoInfo newInfo = new EmoPanel.EmoInfo();
                    newInfo.type = 1;
                    newInfo.Name = f.getName();
                    newInfo.Path = f.getAbsolutePath();
                    findEmoInfos.add(newInfo);
                }
            }
        }


        return findEmoInfos;
    }
    public static ArrayList<String> searchForPathList(){
        String rawPath = HookEnv.ExtraDataPath + "/Pic/";
        File[] fs = new File(rawPath).listFiles();
        if (fs == null)return new ArrayList<>();

        ArrayList<String> arrName = new ArrayList<>();
        for(File f : fs){
            if (f.isDirectory()){
                arrName.add(f.getName());
            }
        }
        return arrName;
    }


}
