package cc.hicore.qtool.QQCleaner.QQCleanerHook;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "隐藏打卡提示", itemType = XPItem.ITEM_Hook)
public class HideSignTip {
    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "隐藏打卡提示";
        ui.groupName = "聊天界面净化";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook", MMethod.FindMethod("com.tencent.mobileqq.graytip.UniteGrayTipUtil", null, MClass.loadClass("com.tencent.mobileqq.graytip.UniteEntity"),
                new Class[]{String.class}));
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("hook", "parseXML, illegel note: ", m -> m.getDeclaringClass().getName().startsWith("com.tencent.mobileqq.graytip")));
    }

    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker() {
        return param -> {
            String paramText = String.valueOf(param.args[0]);
            if (paramText.contains("我也要打卡")) {
                param.setResult(null);
            }
        };
    }
}
