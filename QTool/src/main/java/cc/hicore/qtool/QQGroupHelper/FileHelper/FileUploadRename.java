package cc.hicore.qtool.QQGroupHelper.FileHelper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Locale;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@UIItem(name = "上传重命名base.apk", type = 1, id = "UploadFileRename", targetID = 1,groupName = "功能辅助")
@HookItem(isDelayInit = false, isRunInAllProc = false)
public class FileUploadRename extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {
        Method hookMethod = getMethod();
        XPBridge.HookBefore(hookMethod, param -> {
            if (!IsEnable) return;
            String stack = Log.getStackTraceString(new Throwable());
            if (stack.contains("com.tencent.mobileqq.uftransfer.depend.UFTDependFeatureApi")){
                String path = (String) param.args[0];
                File f = new File(path);
                if (f.getName().toLowerCase(Locale.ROOT).startsWith("base") && f.getName().toLowerCase(Locale.ROOT).endsWith(".apk")) {
                    String Name = GetPackageInfo(path);
                    param.setResult(Name);
                }
            }

        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(FileUploadRename.class.getName());
        }
    }

    @Override
    public void ListItemClick(Context context) {

    }

    public Method getMethod() {
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.utils.FileUtils"), "getFileName", String.class,
                new Class[]{String.class});
        return m;
    }

    public static String GetPackageInfo(String Path) {
        PackageManager manager = HookEnv.AppContext.getPackageManager();
        PackageInfo info = manager.getPackageArchiveInfo(Path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = Path;
            appInfo.publicSourceDir = Path;
            String appName = manager.getApplicationLabel(appInfo).toString();
            String version = info.versionName;
            return appName + "-" + version + ".apk";
        }
        return "base.apk";
    }
}
