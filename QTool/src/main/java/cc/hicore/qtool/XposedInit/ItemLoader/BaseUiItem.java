package cc.hicore.qtool.XposedInit.ItemLoader;

import android.content.Context;

public interface BaseUiItem {
    void SwitchChange(boolean IsCheck);

    void ListItemClick(Context context);
}
