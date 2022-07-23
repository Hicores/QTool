package cc.hicore.qtool.JavaPlugin.Controller;

import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.DexFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import bsh.classpath.BshLoaderManager;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;
import dalvik.system.PathClassLoader;

public class LoadJarHelper {
    private static final HashMap<String,ClassLoader> jarCache = new HashMap<>();
    public static ClassLoader loadJar(String JarPath) throws Exception{
        File jarFile = new File(JarPath);
        if (!jarFile.exists())throw new FileNotFoundException("Can't find jar file:"+ JarPath);
        String jarMD5 = DataUtils.getFileMD5(jarFile);
        if (jarCache.containsKey(jarMD5))return jarCache.get(jarMD5);

        String jarCachePath = HookEnv.AppContext.getExternalCacheDir()+"/jars/"+jarMD5;
        String dexCache = HookEnv.AppContext.getExternalCacheDir()+"/dex/"+jarMD5;
        FileUtils.copy(JarPath,jarCachePath);
        CompileJarToDex(jarCachePath,dexCache);
        FixJarClassLoader fixLoader = new FixJarClassLoader(HookEnv.mLoader);
        ClassLoader newLoader = new PathClassLoader(dexCache,fixLoader);
        jarCache.put(jarMD5,newLoader);
        BshLoaderManager.addClassLoader(newLoader);
        return newLoader;
    }

    private static class FixJarClassLoader extends ClassLoader{
        ClassLoader parent;
        public FixJarClassLoader(ClassLoader parent){
            this.parent = parent;
        }
    }
    private static void CompileJarToDex(String JarPath,String Dest){
        try{
            DexOptions dexOptions = new DexOptions();
            DexFile newDexFile = new DexFile(dexOptions);
            ZipInputStream zInp = new ZipInputStream(new FileInputStream(JarPath));
            ZipEntry zipEntry;
            while ((zipEntry = zInp.getNextEntry())!= null){
                byte[] classCode = DataUtils.readAllBytes(zInp);
                String clzName = zipEntry.getName();
                DxContext dxContext = new DxContext();
                if (clzName.endsWith(".class")){
                    DirectClassFile classFile = new DirectClassFile(classCode,clzName,true);
                    classFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
                    newDexFile.add(CfTranslator.translate(dxContext,classFile,classCode,new CfOptions(), dexOptions, newDexFile));
                }
            }
            ByteArrayOutputStream destByte = new ByteArrayOutputStream();
            newDexFile.writeTo(destByte,null,false);
            FileUtils.WriteToFile(Dest,destByte.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Can't CompileJarToDex",e);
        }

    }
}
