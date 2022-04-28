package cc.hicore.qtool.XPWork.QQUtilsHook;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.DebugUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false, isRunInAllProc = false)
@UIItem(itemName = "半透明头像上传", mainItemID = 1, itemType = 1, ID = "Upload_Avatar")
public class HookForUploadAvatar extends BaseHookItem implements BaseUiItem {
    boolean IsEnable = false;

    @Override
    public boolean startHook() throws Throwable {
        Method qqMethod = getMethod();
        XPBridge.HookBefore(qqMethod, param -> {
            try {
                if (!IsEnable) return;
                //自己进行图像转换,不给QQ把透明背景扣掉的机会
                FileOutputStream fos = new FileOutputStream((String) param.args[0]);
                Bitmap bitmap = (Bitmap) param.args[1];
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                param.setResult(true);
            } catch (Throwable e) {
                LogUtils.error("HookForUploadAvatar", e);
            }
        });

        Method systemMethod = Bitmap.class.getMethod("compress", Bitmap.CompressFormat.class, int.class, OutputStream.class);
        XPBridge.HookBefore(systemMethod, param -> {
            if (!IsEnable) return;
            String CurrentCallStacks = DebugUtils.getCurrentCallStacks();
            if (CurrentCallStacks.contains("NearbyPeoplePhotoUploadProcessor") || CurrentCallStacks.contains("doInBackground") ||
                    CurrentCallStacks.contains("TroopUploadingThread")) {
                param.args[0] = Bitmap.CompressFormat.PNG;
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
            HookLoader.CallHookStart(HookForUploadAvatar.class.getName());
        }
    }

    private Method getMethod() {
        Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.pic.compress.Utils", "a", boolean.class, new Class[]{
                String.class,
                Bitmap.class,
                int.class,
                String.class,
                MClass.loadClass("com.tencent.mobileqq.pic.CompressInfo")
        });
        return hookMethod;
    }

    @Override
    public void ListItemClick() {

    }
}
