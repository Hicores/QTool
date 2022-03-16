package com.hicore.qtool.XPWork.QQCleanerHook;


import android.view.View;
import android.widget.LinearLayout;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Method;

@UIItem(itemName = "隐藏聊天界面相机图标",mainItemID = 2,itemType = 1,ID = "HideChatCameraButton")
@HookItem(isRunInAllProc = false,isDelayInit = true)
public class HideChatCamera extends BaseHookItem implements BaseUiItem {
    static boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {

        XPBridge.HookAfter(getMethod(),param -> {
            if (!IsEnable)return;
            LinearLayout l = (LinearLayout) param.thisObject;
            View v = l.getChildAt(2);
            if (v != null){
                v.setVisibility(View.GONE);
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
    public Method getMethod(){
        Method m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout",
                "a",void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")});
        return m;
    }
    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck){
            HookLoader.CallHookStart(HideChatCamera.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
}
