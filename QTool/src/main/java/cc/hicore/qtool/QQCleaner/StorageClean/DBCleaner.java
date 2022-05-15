package cc.hicore.qtool.QQCleaner.StorageClean;

import android.content.Context;

import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;

@UIItem(name = "聊天数据库清理",groupName = "其他净化",targetID = 2,type = 2,id = "StorageCleanView")
public class DBCleaner implements BaseUiItem {

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        Utils.ShowToast("未实现");
    }
}
