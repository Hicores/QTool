package cc.hicore.qtool.XPWork.LittleHook;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "解锁更多类型消息左滑回复", itemType = XPItem.ITEM_Hook)
public class SupportMoreReplyMsg {
    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "允许更多类型消息左滑回复";
        ui.groupName = "聊天界面增强";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook", getMethod());
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("hook", "isInterestedMotionEvent() is called. ev", m ->
                MMethod.FindMethod(m.getDeclaringClass(), "H", boolean.class, new Class[0])
        ));
    }

    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker() {
        return param -> param.setResult(true);
    }

    public Method getMethod() {
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.bubble.LeftSwipeReplyHelper"), "h", boolean.class, new Class[0]);
        if (m == null)
            m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.bubble.LeftSwipeReplyHelper"), "H", boolean.class, new Class[0]);
        return m;
    }
}
