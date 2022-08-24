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
import io.github.qauxv.util.DexMethodDescriptor;
import me.iacn.biliroaming.utils.DexHelper;

public class DexFinder {
    private static DexFinder instance;
    private String apkPath;
    private long dexkitInstance;
    private ClassLoader loader;
    public static DexFinder getInstance(String apkPath){
        if (instance != null)return instance;
        instance = new DexFinder(apkPath);
        return instance;
    }
    private DexHelper helper;
    private DexFinder(String apkPath){
        loader = HookEnv.mLoader;

        this.apkPath = apkPath;

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
        dexkitInstance = DexFinderNative.InitPath(apkPath);
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
        String[] strResult = DexFinderNative.findMethodUsingString(dexkitInstance,str);
        ArrayList<Method> newMethod = new ArrayList<>();
        for (String s : strResult){
            try {
                newMethod.add(MethodSignToMethodInstance(loader,s));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return newMethod.toArray(new Method[0]);
    }
    public static Method MethodSignToMethodInstance(ClassLoader loader,String methodSign) throws NoSuchMethodException {
        return new DexMethodDescriptor(methodSign).getMethodInstance(loader);
    }
    public Method[] findMethodBeInvoked_DexKit(Method method){
        String methodDesc = new DexMethodDescriptor(method).toString();
        String[] strResult = DexFinderNative.findMethodInvoked(dexkitInstance,methodDesc);
        ArrayList<Method> newMethod = new ArrayList<>();
        for (String s : strResult){
            try {
                newMethod.add(MethodSignToMethodInstance(loader,s));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return newMethod.toArray(new Method[0]);
    }

}
