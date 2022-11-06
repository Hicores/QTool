package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.app.Activity;
import android.view.ViewGroup;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "屏蔽下拉小程序", itemType = XPItem.ITEM_Hook)
public class HideAppEntry {
    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽下拉小程序";
        ui.targetID = 2;
        ui.type = 1;
        ui.groupName = "主界面净化";
        return ui;
    }

    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker() {
        return param -> param.setResult(null);
    }

    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.mini.api.impl.MiniAppServiceImpl"), "createMiniAppEntryManager", MClass.loadClass("com.tencent.mobileqq.mini.entry.MiniAppPullInterface"), new Class[]{
                boolean.class, Activity.class, Object.class, Object.class, Object.class, Object.class, ViewGroup.class
        }));
    }
}
