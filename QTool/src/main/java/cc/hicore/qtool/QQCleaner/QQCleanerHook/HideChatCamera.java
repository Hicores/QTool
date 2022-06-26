package cc.hicore.qtool.QQCleaner.QQCleanerHook;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.EmoHelper.Hooker.HookInjectEmoTabView;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

@UIItem(name = "隐藏聊天界面相机图标", type = 1, id = "HideChatCameraButton", targetID = 2,groupName = "聊天界面净化")
@HookItem(isRunInAllProc = false, isDelayInit = true)
public class HideChatCamera extends BaseHookItem implements BaseUiItem {
    static boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {

        XPBridge.HookAfter(getMethod(), param -> {
            LinearLayout l = (LinearLayout) param.thisObject;
            View v = l.getChildAt(2);
            if (IsEnable) {
                if (v != null) {
                    v.setVisibility(View.GONE);
                }
            }
        });

        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.AIODtReportHelper"),
                null, void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout")});
        XPBridge.HookBefore(m,param -> {
            param.setResult(null);
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

    public Method getMethod() {
        return MMethod.FindMethod("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout",
                null, void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")});
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(HideChatCamera.class.getName());
        }
    }

    @Override
    public void ListItemClick(Context context) {

    }
}
