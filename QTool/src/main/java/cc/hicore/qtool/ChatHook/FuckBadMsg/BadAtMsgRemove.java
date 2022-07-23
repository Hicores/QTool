package cc.hicore.qtool.ChatHook.FuckBadMsg;

import org.json.JSONObject;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(itemType = XPItem.ITEM_Hook,name = "屏蔽异常艾特提示")
public class BadAtMsgRemove {
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽异常艾特提示";
        ui.groupName = "消息屏蔽";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("msg_notify", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"), "notifyMessageReceived", void.class, new Class[]{MClass.loadClass("com.tencent.imcore.message.Message"), boolean.class, boolean.class}));
    }
    @VerController
    @XPExecutor(methodID = "msg_notify")
    public BaseXPExecutor hookWorker(){
        return param -> {
            Object Message = param.args[0];
            int sr = MField.GetField(Message, "bizType", int.class);
            if (sr == 25){
                String msg = MField.GetField(Message, "msg", String.class);
                String extStr = MField.GetField(Message,"extStr",String.class);
                JSONObject json = new JSONObject(extStr);
                if (json.optString("long_text_recv_state").equals("1")){
                    if (!msg.contains("@")){
                        param.setResult(null);
                    }
                }
            }

        };
    }
}
