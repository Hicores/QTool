package cc.hicore.DexFinder;

import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;
import me.iacn.biliroaming.utils.DexHelper;

public class DexFinder {
    private static DexFinder instance;
    public static DexFinder getInstance(){
        if (instance != null)return instance;
        instance = new DexFinder();
        return instance;
    }
    private DexHelper helper;
    private DexFinder(){
        ClassLoader loader = HookEnv.mLoader;
        initLibrary();
        helper = new DexHelper(loader);
    }
    private void initLibrary(){
        String cachePath = HookEnv.AppContext.getCacheDir() + "/" +DataUtils.getStrMD5(Settings.Secure.ANDROID_ID+"_").substring(0,8) + "/";

        if (android.os.Process.is64Bit()){
            outputLibToCache(cachePath,true);
            System.load(cachePath+"libdex_builder.so");
            System.load(cachePath+"libdexfinder.so");
        }else {
            outputLibToCache(cachePath,false);
            System.load(cachePath+"libdex_builder.so");
            System.load(cachePath+"libdexfinder.so");
        }
    }
    private void outputLibToCache(String cachePath,boolean is64){
        new File(cachePath).mkdirs();
        String apkPath = HookEnv.ToolApkPath;
        try{
            ZipInputStream zInp = new ZipInputStream(new FileInputStream(apkPath));
            ZipEntry entry;
            while ((entry = zInp.getNextEntry())!= null){

                if (is64 && entry.getName().startsWith("lib/arm64-v8a/")){

                    String libName = entry.getName().substring(14);
                    FileUtils.WriteToFile(cachePath + libName,DataUtils.readAllBytes(zInp));
                }else if (!is64 && entry.getName().startsWith("lib/armeabi-v7a/")){
                    String libName = entry.getName().substring(16);
                    FileUtils.WriteToFile(cachePath + libName,DataUtils.readAllBytes(zInp));
                }
            }
            zInp.close();
        }catch (Exception e){

        }

    }
    public Method[] findMethodByString(String str){
        if (str == null)return new Method[0];
        long[] dexIndexes = helper.findMethodUsingString(str,false,-1, (short) -1, null, -1,
                null, null, null, false);
        ArrayList<Method> retArr = new ArrayList<>();
        for (long index : dexIndexes){
            Member m = helper.decodeMethodIndex(index);
            if (m instanceof Method){
                retArr.add((Method) m);
            }
        }
        return retArr.toArray(new Method[0]);
    }
    public Method[] findMethodBeInvoked(Method beInvoked){
        if (beInvoked == null)return new Method[0];
        long[] dexIndexes = helper.findMethodInvoked(helper.encodeMethodIndex(beInvoked),-1,(short) -1,null,-1,null,
                null,null,false);
        ArrayList<Method> retArr = new ArrayList<>();
        for (long index : dexIndexes){
            Member m = helper.decodeMethodIndex(index);
            if (m instanceof Method){
                retArr.add((Method) m);
            }
        }
        return retArr.toArray(new Method[0]);
    }
    public Method[] findMethodInvoking(Method beInvoked){
        if (beInvoked == null)return new Method[0];
        long[] dexIndexes = helper.findMethodInvoking(helper.encodeMethodIndex(beInvoked),-1,(short) -1,null,-1,null,
                null,null,false);
        ArrayList<Method> retArr = new ArrayList<>();
        for (long index : dexIndexes){
            Member m = helper.decodeMethodIndex(index);
            if (m instanceof Method){
                retArr.add((Method) m);
            }
        }
        return retArr.toArray(new Method[0]);
    }
}
