package cc.hicore.qtool.ChatHook.ChatCracker;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "状态消息隐藏", itemType = XPItem.ITEM_Hook)
public class ChangeTipToSystemMsg {
    @UIItem
    @VerController
    public UIInfo getUIInfo() {
        UIInfo ui = new UIInfo();
        ui.groupName = "聊天净化";
        ui.name = "状态消息隐藏";
        ui.desc = "阻止状态消息显示在自己的消息记录中";
        ui.targetID = 2;
        ui.type = 1;
        return ui;
    }

    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    public void getHookMethod(MethodContainer container) {
        Finders.onTroopMessage(container);
    }

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void getHookMethod_890(MethodContainer container) {
        Finders.onTroopMessageNew(container);
    }

    @VerController
    @XPExecutor(methodID = "troopMsgProxy")
    public BaseXPExecutor hookWorker() {
        return param -> {
            Object ChatMsg = param.args[2];
            if (ChatMsg.getClass().getName().contains("MessageForUniteGrayTip")) {
                try {
                    MField.SetField(ChatMsg, "senderuin", "10000");
                    MMethod.CallMethod(ChatMsg, "prewrite", void.class, new Class[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
