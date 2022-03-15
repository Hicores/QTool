package com.hicore.qtool.XPWork.QQUtilsHook;

import com.hicore.HookItem;
import com.hicore.UIItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(itemName = "透明头像上传",mainItemID = 1,itemType = 1,ID = "Upload_Avatar")
public class HookForUploadAvatar extends BaseHookItem implements BaseUiItem {
    @Override
    public boolean startHook() throws Throwable {
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
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
