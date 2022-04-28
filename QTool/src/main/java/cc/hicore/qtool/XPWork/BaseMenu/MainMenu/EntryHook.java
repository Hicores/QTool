package cc.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import de.robv.android.xposed.XposedBridge;

/*
Hook注入QQ的设置界面添加一个选项
 */
@HookItem(isRunInAllProc = false, isDelayInit = false)
public class EntryHook extends BaseHookItem {
    private static final String TAG = "EntryHook";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method m = getHookMethod();
        XPBridge.HookAfter(m, param -> {
            try {
                Activity act = (Activity) param.thisObject;
                ResUtils.StartInject(act);

                View item = MField.GetFirstField(act, MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"));
                ViewGroup mRoot = (ViewGroup) item.getParent();
                mRoot.addView(FormItemUtils.createMultiItem(act, "QTool", BuildConfig.VERSION_NAME, v -> {
                    MainMenu.createActivity(act);
                }), 1);
            } catch (Exception e) {
                XposedBridge.log(e);
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }


    @Override
    public boolean check() {
        return getHookMethod() != null;
    }

    public Method getHookMethod() {
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", boolean.class, new Class[]{Bundle.class});
    }
}
