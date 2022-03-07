package com.hicore.ReflectUtils;

import android.content.res.Resources;
import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.qtool.XposedInit.HookEnv;

import java.lang.reflect.Method;

public class InjectRes {
    public static void StartInject(String ApkPath){
        Resources res = HookEnv.AppContext.getResources();
        try{
            Method m = res.getClass().getDeclaredMethod("addAssetPath",String.class);
            m.setAccessible(true);
            m.invoke(res,ApkPath);
        }catch (Exception e){
            LogUtils.fetal_error("Inject_Res", Log.getStackTraceString(e));
        }
    }

}
