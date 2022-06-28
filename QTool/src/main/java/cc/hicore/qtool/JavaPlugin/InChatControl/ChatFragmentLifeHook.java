package cc.hicore.qtool.JavaPlugin.InChatControl;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.MethodFinder;

/*
挂钩判断聊天窗口显示/隐藏事件
 */
@XPItem(name = "ChatFragmentLifeHook",itemType = XPItem.ITEM_Hook)
public class ChatFragmentLifeHook{
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("ChatOnShow",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"), "U", new Class[0]));
        container.addMethod("ChatOnHide",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"), "R", new Class[0]));
    }
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getHookMethod_8_8_93(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("ChatOnShow","loadBackgroundAsync: skip for mosaic is on",m->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie")));
        container.addMethod(MethodFinderBuilder.newFinderByString("ChatOnHide","doOnStop",m->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie")));
    }
    @VerController
    @XPExecutor(methodID = "ChatOnShow",period = XPExecutor.After)
    public BaseXPExecutor onShow(){
        return param -> onShow(param.thisObject);
    }
    @VerController
    @XPExecutor(methodID = "ChatOnHide",period = XPExecutor.After)
    public BaseXPExecutor onHide(){
        return param -> onHide(param.thisObject);
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
}
