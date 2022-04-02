package com.hicore.qtool.VoiceHelper.Panel;

import android.content.Context;

import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.EmoHelper.Panel.EmoPanelView;
import com.hicore.qtool.QQTools.ContUtil;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

public class VoicePanel {
    public static void createVoicePanel(){
        Context context = Utils.getTopActivity();
        ResUtils.StartInject(context);
        Context fixContext = ContUtil.getFixContext(context);
        VoicePanelController controller = new VoicePanelController(fixContext);

        XPopup.Builder NewPop = new XPopup.Builder(fixContext)
                .autoOpenSoftInput(false)
                .autoFocusEditText(false)
                .isDestroyOnDismiss(true)
                .moveUpToKeyboard(false);

        BasePopupView base = NewPop.asCustom(controller);
        base.show();
    }
}