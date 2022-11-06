package cc.hicore.qtool.ChatHook.LongClickSendCard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import de.robv.android.xposed.XC_MethodHook;

@XPItem(name = "长按发送卡片消息", itemType = XPItem.ITEM_Hook)
public class SendBtnHooker {
    private CoreLoader.XPItemInfo info;

    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.targetID = 1;
        ui.type = 1;
        ui.name = "长按发送卡片消息";
        ui.groupName = "聊天辅助";
        return ui;
    }

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getMethodInfo(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("before_method", "em_gb_ice_sticker", m -> true));
        container.addMethod(MethodFinderBuilder.newFinderByMethodInvokingLinked("getAIORoot", "before_method", m -> ((Method) m).getReturnType().equals(ViewGroup.class)));
    }

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieInit(MethodContainer container) {
        Finders.BaseChatPieInit_8893(container);
    }

    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieOld(MethodContainer container) {
        Finders.BaseChatPieInit(container);
    }

    @XPExecutor(methodID = "basechatpie_init", period = XPExecutor.After, hook_period = XC_MethodHook.PRIORITY_LOWEST)
    @VerController(max_targetVer = QQVersion.QQ_8_8_90)
    public BaseXPExecutor xpWorker_old() {
        return param -> {
            ViewGroup vg = MField.GetFirstField(param.thisObject, ViewGroup.class);
            if (vg == null) return;
            Context ctx = vg.getContext();
            int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
            View sendBtn = vg.findViewById(fun_btn);
            int ed_id = ctx.getResources().getIdentifier("input", "id", ctx.getPackageName());
            EditText ed = vg.findViewById(ed_id);
            View.OnLongClickListener upListener = null;
            try {
                Object listener = MField.GetField(sendBtn, "mListenerInfo");
                upListener = MField.GetField(listener, "mOnLongClickListener");
            } catch (Exception e) {

            }
            View.OnLongClickListener finalUpListener = upListener;
            sendBtn.setOnLongClickListener(v -> {
                String input = ed.getText().toString();
                if (input.startsWith("{")) {
                    QQMsgSender.sendArkApp(HookEnv.SessionInfo, QQMsgBuilder.build_arkapp(input));
                    ed.setText("");
                    return true;
                } else if (input.startsWith("<")) {
                    QQMsgSender.sendStruct(HookEnv.SessionInfo, QQMsgBuilder.build_struct(input));
                    ed.setText("");
                    return true;
                }
                if (finalUpListener != null) {
                    return finalUpListener.onLongClick(v);
                }
                return false;
            });
        };
    }

    @XPExecutor(methodID = "basechatpie_init", period = XPExecutor.After)
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public BaseXPExecutor xpWorker() {
        return param -> {
            ViewGroup vg = (ViewGroup) (((Method) info.scanResult.get("getAIORoot")).invoke(param.thisObject));
            if (vg == null) return;
            Context ctx = vg.getContext();
            int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
            View sendBtn = vg.findViewById(fun_btn);
            int ed_id = ctx.getResources().getIdentifier("input", "id", ctx.getPackageName());
            EditText ed = vg.findViewById(ed_id);
            sendBtn.setOnLongClickListener(v -> {
                String input = ed.getText().toString();
                if (input.startsWith("{")) {
                    QQMsgSender.sendArkApp(HookEnv.SessionInfo, QQMsgBuilder.build_arkapp(input));
                    ed.setText("");
                    return true;
                } else if (input.startsWith("<")) {
                    QQMsgSender.sendStruct(HookEnv.SessionInfo, QQMsgBuilder.build_struct(input));
                    ed.setText("");
                    return true;
                }
                return false;
            });
        };
    }
}
