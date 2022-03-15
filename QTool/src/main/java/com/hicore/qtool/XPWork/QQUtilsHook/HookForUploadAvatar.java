package com.hicore.qtool.XPWork.QQUtilsHook;

import com.hicore.HookItem;
import com.hicore.UIItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(itemDesc = "开启后直接上传即可",itemName = "透明头像上传",mainItemID = 1,itemType = 1)
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
