package cc.hicore.qtool.ChatHook.ChatCracker;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.Utils.Utils;

public class FixWhileLine {
    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "修补屏幕下方小白条";
        ui.groupName = "其他选项";
        ui.targetID = 4;
        ui.type = 1;
        return ui;
    }

    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void findMethod(MethodContainer container) {
        Finders.BaseChatPieInit(container);
    }

    @VerController(targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void findMethod_8893(MethodContainer container) {
        Finders.BaseChatPieInit_8893(container);
    }

    @VerController
    @XPExecutor(methodID = "basechatpie_init", period = XPExecutor.After)
    public BaseXPExecutor doXPHook() {
        return param -> {
            Activity act = Utils.getTopActivity();
            if (act != null) {
                Window window = act.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                );
            }
        };
    }

}
