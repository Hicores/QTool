package cc.hicore.qtool.QQCleaner.OtherCleaner;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

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
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "隐藏QQ钱包广告",itemType = XPItem.ITEM_Hook)
public class HideQWalletAd{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "隐藏QQ钱包广告";
        ui.groupName = "其他净化";
        ui.targetID = 2;
        ui.type = 1;
        return ui;
    }
    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor worker(){
        return param -> {
            ViewGroup group = MField.GetFirstField(param.thisObject,MClass.loadClass("com.qwallet.view.QWalletHeaderView"));
            listener = () -> {
                try {
                    View v = MField.GetFirstField(group,MClass.loadClass("com.tencent.biz.ui.TouchWebView"));
                    if (v == null)return;
                    group.removeView(v);
                    group.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
            };
            group.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        };
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.qwallet.activity.QWalletHomeActivity"),"onViewCreated",void.class,new Class[]{
                View.class, Bundle.class
        }));
    }
    ViewTreeObserver.OnGlobalLayoutListener listener = null;
}
