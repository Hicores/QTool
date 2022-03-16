package com.hicore.qtool.XPWork.QQCleanerHook;

import android.icu.util.Measure;
import android.util.Log;

import com.hicore.HookItem;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.qtool.EmoHelper.Hooker.HookInjectEmoTabView;
import com.hicore.qtool.XPWork.BaseMenu.MainMenu.MainMenu;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import bsh.Interpreter;


@UIItem(itemType = 1,itemName = "隐藏表情面板热图和自拍表情",mainItemID = 2,ID = "HideGIFAndSelfPicPanel")
@HookItem(isRunInAllProc = false,isDelayInit = true)
public class HideEmoPanelGifAndSelf extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            List l = (List)param.getResult();
            if (IsEnable){
                Iterator it = l.iterator();
                while (it.hasNext()){
                    Object item = it.next();
                    int type = MField.GetField(item,"type");
                    if (type == 11 || type == 12)it.remove();
                }
            }

        });
        return true;
    }
    private Method getMethod(){
        Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.EmoticonPanelController","getPanelDataList", List.class,new Class[0]);
        return m;

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
            HookLoader.CallHookStart(HideEmoPanelGifAndSelf.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
}
