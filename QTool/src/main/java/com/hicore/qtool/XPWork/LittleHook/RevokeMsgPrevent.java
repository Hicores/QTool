package com.hicore.qtool.XPWork.LittleHook;

import com.hicore.HookItem;
import com.hicore.UIItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;

//@HookItem(isRunInAllProc = false,isDelayInit = false)
//@UIItem(itemType = 1,itemName = "消息防撤回",mainItemID = 1,ID = "PreventRevokeMsg")
public class RevokeMsgPrevent extends BaseHookItem implements BaseUiItem {
    @Override
    public boolean startHook() throws Throwable {
        return false;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick() {

    }
}
