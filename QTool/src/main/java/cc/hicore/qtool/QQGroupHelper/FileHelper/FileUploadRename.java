package cc.hicore.qtool.QQGroupHelper.FileHelper;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.util.Locale;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
@XPItem(name = "上传重命名base.apk",itemType = XPItem.ITEM_Hook)
public class FileUploadRename{
    CoreLoader.XPItemInfo info;
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "上传重命名base.apk";
        ui.type = 1;
        ui.targetID = 1;
        ui.groupName = "功能辅助";
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.utils.FileUtils"), "getFileName", String.class,
                new Class[]{String.class}));
        container.addMethod(MethodFinderBuilder.newFinderByString("bclz","[UFTTransfer] UFTDependFeatureApi",m->true));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> {
            String stack = Log.getStackTraceString(new Throwable());
            if (stack.contains("com.tencent.mobileqq.uftransfer.depend.UFTDependFeatureApi") || (info.scanResult.get("bclz") != null && stack.contains(info.scanResult.get("bclz").getDeclaringClass().getName()))){
                String path = (String) param.args[0];
                File f = new File(path);
                if (f.getName().toLowerCase(Locale.ROOT).startsWith("base") && f.getName().toLowerCase(Locale.ROOT).endsWith(".apk")) {
                    String Name = GetPackageInfo(path);
                    param.setResult(Name);
                }
            }
        };
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
