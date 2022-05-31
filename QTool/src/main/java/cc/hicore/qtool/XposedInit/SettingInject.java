package cc.hicore.qtool.XposedInit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.MainMenu;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import de.robv.android.xposed.XposedBridge;

public class SettingInject {
    public static void startInject(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", boolean.class, new Class[]{Bundle.class});
        XPBridge.HookAfter(m,param -> {
            try {
                Activity act = (Activity) param.thisObject;
                ResUtils.StartInject(act);

                View item = MField.GetFirstField(act, MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"));
                ViewGroup mRoot = (ViewGroup) item.getParent();
                View newItem = FormItemUtils.createMultiItem(act, "QTool", BuildConfig.VERSION_NAME, v -> { MainMenu.onCreate(act); });
                newItem.setOnLongClickListener(v -> {
                    DebugDialog.startShow(v.getContext());
                    return true;
                });
                mRoot.addView(newItem, 1);
            } catch (Exception e) {
                XposedBridge.log(e);
            }
        });
    }
}
