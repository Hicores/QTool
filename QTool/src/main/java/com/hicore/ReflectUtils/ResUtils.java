package com.hicore.ReflectUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.hicore.LogUtils.LogUtils;
import com.hicore.qtool.R;
import com.hicore.qtool.XposedInit.HookEnv;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class ResUtils {
    public static void StartInject(Context ctx){
        try{
            try{
                ctx.getResources().getString(R.string.TestResInject);
            }catch (Exception e){
                EzXHelperInit.INSTANCE.addModuleAssetPath(ctx);
            }
        }catch (Exception e){
            LogUtils.fetal_error("Inject_Res", Log.getStackTraceString(e));
        }
    }
    public static boolean CheckResInject(Context context){
        try{
            return context.getResources().getString(R.string.TestResInject).contains("Success");
        }catch (Exception e){
            return false;
        }
    }


}
