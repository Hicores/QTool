package cc.hicore.qtool.QQCleaner.OtherCleaner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "屏蔽部分烦人提示",groupName = "其他净化",targetID = 2,type = 1,desc = "语音转换文字失败,资源域名已拦截,设置禁言,解除禁言",id = "HideSomeToast")
public class HideSomeToast extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            if (IsEnable){
                Toast toast = (Toast) param.thisObject;
                ViewGroup vg = (ViewGroup) toast.getView();
                if(vg==null) return;
                TextView textView = (TextView) traversalView(vg);
                if (textView != null){
                    String ToastText = (String) textView.getText();
                    if (ToastText.equals("转发成功") || ToastText.equals("语音转换文字失败") || ToastText.equals("资源域名已拦截")
                            || ToastText.contains("设置禁言") || ToastText.contains("解除禁言")){
                        param.setResult(null);
                    }
                }
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
        if (IsCheck) HookLoader.CallHookStart(HideSomeToast.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.widget.QQToast$ProtectedToast","show",void.class,new Class[0]);
    }
    public static View traversalView(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                View v6=traversalView((ViewGroup) view);
                if(v6!=null) return v6;
            } else {
                if(doView(view)) return view;
            }
        }
        return null;
    }
    private static boolean doView(View view) {
        if (view.getClass().getSimpleName().equals("TextView"))
        {
            return true;
        }
        return false;
    }
}
