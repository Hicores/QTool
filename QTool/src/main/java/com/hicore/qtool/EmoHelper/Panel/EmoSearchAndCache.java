package com.hicore.qtool.EmoHelper.Panel;

import com.hicore.qtool.HookEnv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class EmoSearchAndCache {
    public static ArrayList<EmoPanel.EmoInfo> searchForEmo(String PathName){
        String rawPath = HookEnv.ExtraDataPath + "/Pic/"+PathName;
        String CachePath = HookEnv.ExtraDataPath + "/Pic/"+PathName+".info";

        File[] fs = new File(rawPath).listFiles();

        ArrayList<EmoPanel.EmoInfo> findEmoInfos = new ArrayList<>();
        if (fs != null){
            for (File f : fs){
                if (f.isFile() && !f.getName().contains(".")){
                    EmoPanel.EmoInfo newInfo = new EmoPanel.EmoInfo();
                    newInfo.IsGif = false;
                    newInfo.Name = f.getName();
                    newInfo.Path = f.getAbsolutePath();
                    findEmoInfos.add(newInfo);
                }
            }
        }
        return findEmoInfos;
    }
    public static boolean checkIsGif(String Path){
        try {
            FileInputStream sIns = new FileInputStream(Path);
            if (sIns.read() == 'G' && sIns.read() == 'I' && sIns.read() == 'F'){
                sIns.close();
                return true;
            }
            sIns.close();
            return false;
        } catch (IOException e) {
            return false;
        }

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
