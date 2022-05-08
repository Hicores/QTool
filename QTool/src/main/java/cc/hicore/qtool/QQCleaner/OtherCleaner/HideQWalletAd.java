package cc.hicore.qtool.QQCleaner.OtherCleaner;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "隐藏QQ钱包广告",groupName = "其他净化",targetID = 2,type = 1,id = "HideQWalletAd")
public class HideQWalletAd extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    ViewTreeObserver.OnGlobalLayoutListener listener = null;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            if (IsEnable){
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
            }

        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(HideQWalletAd.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.qwallet.activity.QWalletHomeActivity"),"onViewCreated",void.class,new Class[]{
                View.class, Bundle.class
        });
    }
}
