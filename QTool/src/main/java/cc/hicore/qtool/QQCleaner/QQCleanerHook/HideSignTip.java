package cc.hicore.qtool.QQCleaner.QQCleanerHook;

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
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
@XPItem(name = "隐藏打卡提示",itemType = XPItem.ITEM_Hook)
public class HideSignTip{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "隐藏打卡提示";
        ui.groupName = "聊天界面净化";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.graytip.UniteGrayTipUtil", null, MClass.loadClass("com.tencent.mobileqq.graytip.UniteEntity"),
                new Class[]{String.class}));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> {
            String paramText = String.valueOf(param.args[0]);
            if (paramText.contains("我也要打卡")) {
                param.setResult(null);
            }
        };
    }
}
