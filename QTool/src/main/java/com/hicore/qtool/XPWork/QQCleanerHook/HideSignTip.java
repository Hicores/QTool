package com.hicore.qtool.XPWork.QQCleanerHook;

import android.util.Log;

import com.hicore.HookItem;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.qtool.QQManager.QQGroupUtils;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(ID = "HideSignTip",mainItemID = 2,itemName = "隐藏打卡提示",itemType = 1)
public class HideSignTip extends BaseHookItem implements BaseUiItem {
    private boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {

        Method m = getMethod();
        XPBridge.HookBefore(m,param -> {
            String paramText = String.valueOf(param.args[0]);
            if (IsEnable && paramText.contains("我也要打卡")){
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

    private Method getMethod(){
        Method m = MMethod.FindMethod("com.tencent.mobileqq.graytip.UniteGrayTipUtil","a",MClass.loadClass("com.tencent.mobileqq.graytip.UniteEntity"),
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
        if (IsCheck){
            HookLoader.CallHookStart(HideSignTip.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
}
