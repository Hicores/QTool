package cc.hicore.DexFinder;

import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;

public class SoLoader {
    public static void loadByName(String name){
        String cachePath = HookEnv.AppContext.getCacheDir() + "/" + DataUtils.getStrMD5(Settings.Secure.ANDROID_ID+"_").substring(0,8) + "/";
        String tempName = ""+name.hashCode();
        FileUtils.deleteFile(new File(cachePath+tempName));
        outputLibToCache(cachePath+tempName,name);
        System.load(cachePath+tempName);
    }
    private static void outputLibToCache(String cachePath,String name){
        String apkPath = HookEnv.ToolApkPath;
        try{
            ZipInputStream zInp = new ZipInputStream(new FileInputStream(apkPath));
            ZipEntry entry;
            while ((entry = zInp.getNextEntry())!= null){
                if (android.os.Process.is64Bit() && entry.getName().startsWith("lib/arm64-v8a/"+name) ){
                    FileUtils.WriteToFile(cachePath,DataUtils.readAllBytes(zInp));
                    break;
                }else if (!android.os.Process.is64Bit() && entry.getName().startsWith("lib/armeabi-v7a/"+name)){
                    FileUtils.WriteToFile(cachePath,DataUtils.readAllBytes(zInp));
                    break;
                }
            }
        }catch (Exception ignored){

        }

    }
}
