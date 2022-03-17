package com.hicore.qtool.EmoHelper.Panel;

import android.content.Context;

import com.hicore.qtool.QQTools.ContUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

public class EmoPanel {
    public static void createShow(Context context){
        Context fixContext = ContUtil.getFixContext(context);
        EmoPanelView NewView = new EmoPanelView(fixContext);

        XPopup.Builder NewPop = new XPopup.Builder(fixContext);
        BasePopupView base = NewPop.asCustom(NewView);
        base.show();
    }
    public static class EmoInfo{
        public String Path;
        public String Name;
        public boolean IsGif;
    }
}
