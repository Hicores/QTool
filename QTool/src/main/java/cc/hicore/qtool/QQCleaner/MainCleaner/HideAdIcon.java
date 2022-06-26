package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import cc.hicore.qtool.XposedInit.MethodFinder;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "隐藏主界面右上角入口",itemType = XPItem.ITEM_Hook)
public class HideAdIcon{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "隐藏主界面右上角入口";
        ui.desc = "可能包含小世界入口等";
        ui.groupName ="主界面净化";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> param.setResult(null);
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook_before","#666666",m->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.home.Conversation")));
        container.addMethod(MethodFinderBuilder.newFinderWhichMethodInvokingLinked("hook","hook_before",m -> ((Method)m).getParameterCount() == 0 && m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.ConversationTitleBtnCtrl")));
    }
}
