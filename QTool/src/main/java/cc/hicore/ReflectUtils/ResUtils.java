package cc.hicore.ReflectUtils;

import android.content.Context;
import android.util.Log;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.qtool.R;

public class ResUtils {
    public static void StartInject(Context ctx) {
        try {
            if (ctx == null) return;
            try {
                ctx.getResources().getString(R.string.TestResInject);
            } catch (Exception e) {
                EzXHelperInit.INSTANCE.addModuleAssetPath(ctx);
            }
        } catch (Exception e) {
            LogUtils.fetal_error("Inject_Res", e);
        }
    }

    public static boolean CheckResInject(Context context) {
        try {
            return context.getResources().getString(R.string.TestResInject).equals("Test Success");
        } catch (Exception e) {
            return false;
        }
    }


}
