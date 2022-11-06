package cc.hicore.qtool.ChatHook.ChatCracker;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.Utils.LayoutUtils;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;

@XPItem(name = "转发消息来源定位", itemType = XPItem.ITEM_Hook)
public class TransShowUin {
    @UIItem
    @VerController
    public UIInfo getUIInfo() {
        UIInfo ui = new UIInfo();
        ui.name = "转发消息来源定位";
        ui.desc = "在合并转发消息中可以点击头像显示资料卡,可以在标题显示来源群号";
        ui.targetID = 1;
        ui.type = 1;
        ui.groupName = "聊天界面增强";
        return ui;
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        Finders.AIOMessageListAdapter_getView(container);
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        Finders.AIOMessageListAdapter_getView_890(container);
    }

    @VerController
    @XPExecutor(methodID = "onAIOGetView", period = XPExecutor.After)
    public BaseXPExecutor xpWorker() {
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if (mGetView instanceof RelativeLayout) mLayout = (RelativeLayout) mGetView;
            else return;
            List MessageRecoreList = MField.GetFirstField(param.thisObject, List.class);
            if (MessageRecoreList == null) return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);

            Activity context = (Activity) mLayout.getContext();
            if (context.getClass().getName().contains("MultiForwardActivity")) {
                int isTroop = MField.GetField(ChatMsg, ChatMsg.getClass(), "istroop", int.class);
                if (isTroop == 1) {
                    String Troop = MField.GetField(ChatMsg, ChatMsg.getClass(), "frienduin", String.class);
                    View mRootView = context.getWindow().getDecorView();
                    int titleid = context.getResources().getIdentifier("title", "id", context.getPackageName());
                    View mtitleView = mRootView.findViewById(titleid);
                    if (mtitleView instanceof TextView) {
                        TextView mView = (TextView) mtitleView;
                        mView.setText("" + QQGroupUtils.GetTroopNameByContact(Troop).replace("\n", "") + "(" + Troop + ")");
                        //mView.setWidth(8000);
                        mView.setOnClickListener(v1 -> QQEnvUtils.OpenTroopCard(Troop));
                    }
                }
                View avatar = LayoutUtils.findView("VasAvatar", mLayout);
                if (avatar != null) {
                    String UserUin = MField.GetField(ChatMsg, ChatMsg.getClass(), "senderuin", String.class);
                    avatar.setOnClickListener(v -> {
                        QQEnvUtils.OpenUserCard(UserUin);
                    });
                }

            }
        };
    }
}
