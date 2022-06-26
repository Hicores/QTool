package cc.hicore.qtool.QQCleaner.ChatCleaner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
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
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1", MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1","getView", View.class,new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        }));
        container.addMethod("hook_2",MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy",null, void.class,new Class[]{
                String.class,
                int.class,
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                boolean.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1",period = XPExecutor.After)
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
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor fuck_bubble_2(){
        return param -> {
            Object ChatMsg = param.args[2];
            MField.SetField(ChatMsg,"vipBubbleID",(long)0);
            MField.SetField(ChatMsg,"vipBubbleDiyTextId",0);
            MField.SetField(ChatMsg,"vipSubBubbleId",0);
        };
    }
}
