package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;

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
import cc.hicore.qtool.XposedInit.HostInfo;
import de.robv.android.xposed.XposedHelpers;

@XPItem(name = "屏蔽回执消息提示", itemType = XPItem.ITEM_Hook)
public class RemoveReceiptMsg {
    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽回执消息提示";
        ui.groupName = "消息屏蔽";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook", XposedHelpers.findConstructorBestMatch(MClass.loadClass("com.tencent.mobileqq.activity.recent.msg.TroopReceiptMsg"), Context.class));
        container.addMethod("hook_2", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"), "notifyMessageReceived", void.class, new Class[]{MClass.loadClass("com.tencent.imcore.message.Message"), boolean.class, boolean.class}));
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        container.addMethod("hook", XposedHelpers.findConstructorBestMatch(MClass.loadClass("com.tencent.mobileqq.activity.recent.b.z"), Context.class));
        container.addMethod("hook_2", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"), "notifyMessageReceived", void.class, new Class[]{MClass.loadClass("com.tencent.imcore.message.Message"), boolean.class, boolean.class}));
    }

    @VerController(max_targetVer = QQVersion.QQ_8_8_90)
    @XPExecutor(methodID = "hook", period = XPExecutor.After)
    public BaseXPExecutor worker_1() {
        return param -> MField.SetField(param.thisObject, "c", String.class, "");
    }

    @VerController(targetVer = QQVersion.QQ_8_8_90)
    @XPExecutor(methodID = "hook", period = XPExecutor.After)
    public BaseXPExecutor worker_1_8890() {
        return param -> MField.SetField(param.thisObject, "g", String.class, "");

    }

    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2() {
        return param -> {
            Object Message = param.args[0];
            int NeedRemove = HostInfo.getVerCode() > 7540 ? 13 : 12;
            int sr = MField.GetField(Message, "bizType", int.class);
            if (sr == NeedRemove) {
                param.setResult(null);
            }
        };
    }
}
