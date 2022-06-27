package cc.hicore.qtool.QQGroupHelper.FileHelper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.XposedInit.EnvHook;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
@XPItem(name = "apk上传失败替换",itemType = XPItem.ITEM_Hook)
public class FileFailureRename{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "apk上传失败替换";
        ui.desc = "apk在群聊上传失败自动压缩为ZIP重试";
        ui.groupName = "功能辅助";
        ui.targetID = 1;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.filemanager.uftwrapper.QFileTroopTransferWrapper$TroopUploadWrapper"), null, void.class,
                new Class[]{MClass.loadClass("com.tencent.mobileqq.uftransfer.api.IUFTTransferKey"), int.class,
                        MClass.loadClass("com.tencent.mobileqq.uftransfer.api.IUFTUploadCompleteInfo")}));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> {
            int i = (int) param.args[1];
            if (i == 202) {
                Object item = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item"));
                String Path = MField.GetField(item, "LocalFile", String.class);
                if (Path.endsWith(".apk")) {
                    File f = new File(Path);
                    EnvHook.requireCachePath();

                    String dest;
                    if (f.getName().equals("base.apk")) {
                        dest = HookEnv.ExtraDataPath + "Cache/" + GetPackageInfo(f.getAbsolutePath()) + ".zip";
                    } else {
                        dest = HookEnv.ExtraDataPath + "Cache/" + f.getName() + ".zip";
                    }

                    new File(dest).delete();
                    ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(dest));
                    ZipEntry entry = new ZipEntry(f.getName());
                    zOut.putNextEntry(entry);
                    FileInputStream fInp = new FileInputStream(f);

                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fInp.read(buffer)) != -1) zOut.write(buffer, 0, read);
                    zOut.close();
                    fInp.close();

                    long TroopUin = MField.GetField(item, "troopuin", long.class);
                    QQMsgSender.sendFileByPath(dest, String.valueOf(TroopUin));

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
            return appName + "-" + version;
        }
        return "base.apk";
    }
}
