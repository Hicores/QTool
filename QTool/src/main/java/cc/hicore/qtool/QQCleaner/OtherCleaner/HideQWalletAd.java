package cc.hicore.qtool.QQCleaner.OtherCleaner;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@XPItem(name = "隐藏QQ钱包广告",itemType = XPItem.ITEM_Hook,proc = XPItem.PROC_ALL)
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
    @VerController(max_targetVer = QQVersion.QQ_8_8_55)
    @MethodScanner
    public void getPluginLoad(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pluginsdk.PluginStatic"),"getOrCreateClassLoaderByPath",ClassLoader.class,new Class[]{
                Context.class, String.class, String.class, boolean.class
        }));
    }
    static AtomicBoolean IsLoad = new AtomicBoolean();
    @VerController(max_targetVer = QQVersion.QQ_8_8_55)
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor hookForPlugin(){
        return param -> {
            String sName = (String) param.args[1];
            if(sName.equals("qwallet_plugin.apk") && !IsLoad.getAndSet(true)){
                ClassLoader pluginLoader = (ClassLoader) param.getResult();
                XposedHelpers.findAndHookMethod("com.qwallet.activity.QWalletHomeActivity", pluginLoader,
                        "onCreate", Bundle.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                ViewGroup group = MField.GetFirstField(param.thisObject,pluginLoader.loadClass("com.qwallet.view.QWalletHeaderView"));
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
                            }
                        }
                );
            }
        };
    }
    @VerController(targetVer = QQVersion.QQ_8_8_55)
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

    @VerController(targetVer = QQVersion.QQ_8_8_55)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.qwallet.activity.QWalletHomeActivity"),"onViewCreated",void.class,new Class[]{
                View.class, Bundle.class
        }));
    }
    ViewTreeObserver.OnGlobalLayoutListener listener = null;
}
