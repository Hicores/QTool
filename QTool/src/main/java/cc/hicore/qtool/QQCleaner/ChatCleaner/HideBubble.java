package cc.hicore.qtool.QQCleaner.ChatCleaner;

import java.util.List;

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
@XPItem(name = "屏蔽聊天特殊气泡",itemType = XPItem.ITEM_Hook)
public class HideBubble{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽聊天特殊气泡";
        ui.groupName = "聊天净化";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        Finders.AIOMessageListAdapter_getView(container);
        Finders.onTroopMessage(container);
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container){
        Finders.AIOMessageListAdapter_getView_890(container);
        Finders.onTroopMessageNew(container);
    }
    @VerController
    @XPExecutor(methodID = "onAIOGetView",period = XPExecutor.After)
    public BaseXPExecutor fuck_bubble_1(){
        return param -> {

            List list = MField.GetFirstField(param.thisObject, List.class);
            if(list==null)return;
            Object ChatMsg = list.get((int) param.args[0]);
            MField.SetField(ChatMsg,"vipBubbleID",(long)0);
            MField.SetField(ChatMsg,"vipBubbleDiyTextId",0);
            MField.SetField(ChatMsg,"vipSubBubbleId",0);
        };
    }
    @VerController
    @XPExecutor(methodID = "troopMsgProxy")
    public BaseXPExecutor fuck_bubble_2(){
        return param -> {
            Object ChatMsg = param.args[2];
            MField.SetField(ChatMsg,"vipBubbleID",(long)0);
            MField.SetField(ChatMsg,"vipBubbleDiyTextId",0);
            MField.SetField(ChatMsg,"vipSubBubbleId",0);
        };
    }
}
