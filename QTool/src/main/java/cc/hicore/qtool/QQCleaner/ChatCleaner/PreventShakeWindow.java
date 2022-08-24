package cc.hicore.qtool.QQCleaner.ChatCleaner;

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
@XPItem(name = "屏蔽窗口抖动",itemType = XPItem.ITEM_Hook)
public class PreventShakeWindow{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽窗口抖动";
        ui.groupName = "聊天净化";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        Finders.onTroopMessage(container);
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container){
        Finders.onTroopMessageNew(container);
    }
    @VerController
    @XPExecutor(methodID = "troopMsgProxy")
    public BaseXPExecutor worker() {
        return param -> {
            Object ChatMsg = param.args[2];
            if (ChatMsg.getClass().getSimpleName().equals("MessageForShakeWindow")){
                MField.SetField(ChatMsg,"isread",true);
            }
        };
    }
}
