package com.hicore.qtool.EmoHelper.Hooker;


import com.hicore.HookItem;

import com.hicore.UIItem;
import com.hicore.qtool.XPWork.QQCleanerHook.HideChatCamera;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

/*
注入主界面选项菜单,同时在菜单勾选时请求三个钩子的挂钩确认
 */
@UIItem(itemName = "分类表情栏",itemType = 1,mainItemID = 1,ID = "EmoHelper")
@HookItem(isDelayInit = true,isRunInAllProc = false)
public class HookInjectEmoTabView extends BaseHookItem implements BaseUiItem {
    public static boolean IsEnable = true;
    @Override
    public boolean startHook() throws Throwable {
        return false;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck){
            HookLoader.CallHookStart(HideChatCamera.class.getName());
            HookLoader.CallHookStart(HookHandlerPicLongClick.class.getName());
            HookLoader.CallHookStart(HookForMixedMsgLongClick.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
}
