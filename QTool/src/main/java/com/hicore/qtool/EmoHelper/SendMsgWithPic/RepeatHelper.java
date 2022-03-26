package com.hicore.qtool.EmoHelper.SendMsgWithPic;

import com.hicore.HookItem;
import com.hicore.UIItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

//@UIItem(itemType = 1,itemName = "带图回复",itemDesc = "带图回复消息",mainItemID = 1,ID = "RepeatWithPic")
public class RepeatHelper extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
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
            HookLoader.CallHookStart(RepeatHelper.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
}
