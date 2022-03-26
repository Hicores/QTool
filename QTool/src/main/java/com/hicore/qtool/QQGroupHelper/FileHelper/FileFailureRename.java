package com.hicore.qtool.QQGroupHelper.FileHelper;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.Measure;

import com.hicore.HookItem;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.Utils.FileUtils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQMessage.QQMsgSender;
import com.hicore.qtool.XposedInit.EnvHook;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(itemType = 1,itemName = "apk上传失败自动压缩为ZIP重试",mainItemID = 1,ID = "RenameApkToRename")
public class FileFailureRename extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {
        Method m = getMethod();
        XPBridge.HookBefore(m,param -> {
            if (!IsEnable)return;
            int i = (int) param.args[1];
            if (i == 202){
                Object item = MField.GetFirstField(param.thisObject,MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item"));
                String Path = MField.GetField(item,"LocalFile",String.class);
                if (Path.endsWith(".apk")){
                    File f = new File(Path);
                    EnvHook.requireCachePath();

                    String dest;
                    if (f.getName().equals("base.apk")){
                        dest = HookEnv.ExtraDataPath+"Cache/"+GetPackageInfo(f.getAbsolutePath())+".zip";
                    }else {
                        dest = HookEnv.ExtraDataPath+"Cache/"+f.getName()+".zip";
                    }

                    new File(dest).delete();
                    ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(dest));
                    ZipEntry entry = new ZipEntry(f.getName());
                    zOut.putNextEntry(entry);
                    FileInputStream fInp = new FileInputStream(f);

                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fInp.read(buffer))!= -1)zOut.write(buffer,0,read);
                    zOut.close();
                    fInp.close();

                    long TroopUin = MField.GetField(item,"troopuin",long.class);
                    QQMsgSender.sendFileByPath(dest,String.valueOf(TroopUin));

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
        if (IsCheck){
            HookLoader.CallHookStart(FileFailureRename.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
    public Method getMethod(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.filemanager.uftwrapper.QFileTroopTransferWrapper$TroopUploadWrapper"),"a",void.class,
                 new Class[]{MClass.loadClass("com.tencent.mobileqq.uftransfer.api.IUFTTransferKey"), int.class,
                         MClass.loadClass("com.tencent.mobileqq.uftransfer.api.IUFTUploadCompleteInfo")});
        return m;
    }
    public static String GetPackageInfo(String Path) {
        PackageManager manager = HookEnv.AppContext.getPackageManager();
        PackageInfo info = manager.getPackageArchiveInfo(Path,PackageManager.GET_ACTIVITIES);
        if(info!=null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = Path;
            appInfo.publicSourceDir = Path;
            String appName = manager.getApplicationLabel(appInfo).toString();
            String version = info.versionName;
            return appName + "-"+version;
        }
        return "base.apk";
    }
}
