package cc.hicore.qtool.QQCleaner.ChatCleaner;

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
@XPItem(name = "屏蔽一起听歌顶栏",itemType = XPItem.ITEM_Hook)
public class HideListenerTogether{
    @UIItem
    @VerController
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽一起听歌顶栏";
        ui.groupName = "聊天界面净化";
        ui.targetID = 2;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","onUIModuleNeedRefresh, uidata=",m->true));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> param.setResult(null);
    }
}
