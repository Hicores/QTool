package cc.hicore.qtool.QQCleaner.QQCleanerHook;

import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false, isRunInAllProc = false)
@UIItem(name = "隐藏打卡提示", type = 1, id = "HideSignTip", targetID = 2,groupName = "聊天界面净化")
public class HideSignTip extends BaseHookItem implements BaseUiItem {
    private boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {

        Method m = getMethod();
        XPBridge.HookBefore(m, param -> {
            String paramText = String.valueOf(param.args[0]);
            if (IsEnable && paramText.contains("我也要打卡")) {
                param.setResult(null);
                return;
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    private Method getMethod() {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.graytip.UniteGrayTipUtil", "a", MClass.loadClass("com.tencent.mobileqq.graytip.UniteEntity"),
                new Class[]{String.class});
        return m;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(HideSignTip.class.getName());
        }
    }

    @Override
    public void ListItemClick(Context context) {

    }
}
