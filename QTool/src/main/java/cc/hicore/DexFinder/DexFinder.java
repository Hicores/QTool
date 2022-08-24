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
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XposedBridge;
import me.iacn.biliroaming.utils.DexHelper;

public class DexFinder {
    private static DexFinder instance;
    private String apkPath;
    public static DexFinder getInstance(String apkPath){
        if (instance != null)return instance;
        instance = new DexFinder(apkPath);
        return instance;
    }
    private DexHelper helper;
    private DexFinder(String apkPath){
        ClassLoader loader = HookEnv.mLoader;

        String cachePath = HookEnv.AppContext.getCacheDir() + "/base.apk";
        FileUtils.copy(apkPath,cachePath);
        this.apkPath = cachePath;
        try{
            initLibrary();
            helper = new DexHelper(loader);
        }catch (Throwable e){
            Utils.ShowToastL("无法加载so库,模块部分功能将无法使用:\n"+e);
        }

    }
    private void initLibrary(){
        String cachePath = HookEnv.AppContext.getCacheDir() + "/" +DataUtils.getStrMD5(Settings.Secure.ANDROID_ID+"_").substring(0,8) + "/";

        if (android.os.Process.is64Bit()){
            outputLibToCache(cachePath,true);
            System.load(cachePath+"libdex_builder.so");
            System.load(cachePath+"libdexfinder.so");
            System.load(cachePath+"libdexkithelper.so");
        }else {
            outputLibToCache(cachePath,false);
            System.load(cachePath+"libdex_builder.so");
            System.load(cachePath+"libdexfinder.so");
            System.load(cachePath+"libdexkithelper.so");
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
    public Method[] findMethodByString_DexKit(String str){
        XposedBridge.log("startFind");
        String[] strResult = DexFinderNative.findMethodUsingString(apkPath,"AIO_doOnCreate_initUI");
        for (String s : strResult){
            XposedBridge.log(s);
        }
        XposedBridge.log("endFind");
        return null;
    }
    public Method[] findMethodBeInvoked_DexKit(Method method){
        return null;
    }

}
