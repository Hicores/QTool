package cc.hicore.qtool.JavaPlugin.InChatControl;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.MethodFinder;

/*
挂钩判断聊天窗口显示/隐藏事件
 */
@HookItem(isDelayInit = false, isRunInAllProc = false)
public class ChatFragmentLifeHook extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {

        Method[] hookMethod = getHookMethod();
        XPBridge.HookAfter(hookMethod[0], param -> {
            onShow(param.thisObject);
        });
        XPBridge.HookAfter(hookMethod[1], param -> {
            onHide(param.thisObject);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        Method[] m = getHookMethod();
        return m[0] != null && m[1] != null;
    }

    private void onShow(Object pie) {
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    try {
                        Object Session = MField.GetFirstField(pie, MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
                        FloatWindowControl.onShowEvent(true, Session);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }, 500);
    }

    private void onHide(Object pie) {
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    try {
                        Object Session = MField.GetFirstField(pie, MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
                        FloatWindowControl.onShowEvent(false, Session);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 500);
    }

    public Method[] getHookMethod() {
        Method[] m = new Method[2];
        m[0] = MethodFinder.findMethodFromCache("ChatOnShow");
        if (m[0] == null){
            MethodFinder.NeedReportToFindMethod("ChatOnShow","SH","loadBackgroundAsync: skip for mosaic is on",a->a.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie"));
        }

        m[1] = MethodFinder.findMethodFromCache("ChatOnHide");
        if (m[1] == null){
            MethodFinder.NeedReportToFindMethod("ChatOnHide","SH","doOnStop",a->a.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie"));
        }
        return m;
    }
}
