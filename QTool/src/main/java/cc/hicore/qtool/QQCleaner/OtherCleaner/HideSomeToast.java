package cc.hicore.qtool.QQCleaner.OtherCleaner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "屏蔽部分烦人提示", itemType = XPItem.ITEM_Hook)
public class HideSomeToast {
    public static View traversalView(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                View v6 = traversalView((ViewGroup) view);
                if (v6 != null) return v6;
            } else {
                if (doView(view)) return view;
            }
        }
        return null;
    }

    private static boolean doView(View view) {
        return view.getClass().getSimpleName().equals("TextView");
    }

    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽部分烦人提示";
        ui.groupName = "其他净化";
        ui.targetID = 2;
        ui.type = 1;
        ui.desc = "语音转换文字失败,资源域名已拦截,设置禁言,解除禁言";
        return ui;
    }

    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook", MMethod.FindMethod("com.tencent.mobileqq.widget.QQToast$ProtectedToast", "show", void.class, new Class[0]));
    }

    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker() {
        return param -> {
            Toast toast = (Toast) param.thisObject;
            ViewGroup vg = (ViewGroup) toast.getView();
            if (vg == null) return;
            TextView textView = (TextView) traversalView(vg);
            if (textView != null) {
                String ToastText = (String) textView.getText();
                if (ToastText.equals("转发成功") || ToastText.equals("语音转换文字失败") || ToastText.equals("资源域名已拦截")
                        || ToastText.contains("设置禁言") || ToastText.contains("解除禁言")) {
                    param.setResult(null);
                }
            }
        };
    }
}
