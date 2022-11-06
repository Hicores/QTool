package cc.hicore.qtool.QQGroupHelper.ChatHelper;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import cc.hicore.Utils.Utils;

@XPItem(name = "在消息右下角显示时间", itemType = XPItem.ITEM_Hook)
@SuppressLint("ResourceType")
public class ShowMessageTime {
    private static final HashMap<String, String> supportShow = new HashMap<>();

    {
        supportShow.put("MessageForPic", "RelativeLayout");
        supportShow.put("MessageForText", "ETTextView");
        supportShow.put("MessageForLongTextMsg", "ETTextView");
        supportShow.put("MessageForFoldMsg", "ETTextView");
        supportShow.put("MessageForPtt", "BreathAnimationLayout");
        supportShow.put("MessageForMixedMsg", "MixedMsgLinearLayout");
        supportShow.put("MessageForReplyText", "SelectableLinearLayout");
        supportShow.put("MessageForScribble", "RelativeLayout");
        supportShow.put("MessageForMarketFace", "RelativeLayout");
        supportShow.put("MessageForArkApp", "ArkAppRootLayout");
        supportShow.put("MessageForStructing", "RelativeLayout");
        supportShow.put("MessageForTroopPobing", "LinearLayout");
        supportShow.put("MessageForTroopEffectPic", "RelativeLayout");
        supportShow.put("MessageForAniSticker", "FrameLayout");
        supportShow.put("MessageForArkFlashChat", "ArkAppRootLayout");
        supportShow.put("MessageForShortVideo", "RelativeLayout");
        supportShow.put("MessageForPokeEmo", "RelativeLayout");
    }

    private static View searchForView(String Name, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            if (vg.getChildAt(i).getClass().getSimpleName().contains(Name)) {
                return vg.getChildAt(i);
            }
        }
        return null;
    }

    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.groupName = "聊天界面增强";
        ui.name = "在消息右下角显示时间";
        ui.type = 1;
        ui.targetID = 1;
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
    public BaseXPExecutor worker() {
        return param -> {
            Object mGetView = param.getResult();
            if (!(mGetView instanceof RelativeLayout)) return;
            List msgList = MField.GetFirstField(param.thisObject, List.class);
            if (msgList == null) return;
            Object ChatMsg = msgList.get((int) param.args[0]);
            //解析消息
            long time = MField.GetField(ChatMsg, "time", long.class);
            SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
            String timeStr = f.format(new Date(time * 1000));

            RelativeLayout mRoot = (RelativeLayout) mGetView;
            String clzName = ChatMsg.getClass().getSimpleName();
            if (supportShow.containsKey(clzName)) {
                String viewName = supportShow.get(clzName);
                TextView showText = mRoot.findViewById(23390);
                if (showText == null) {
                    showText = new TextView(mRoot.getContext());
                    showText.setText(timeStr);
                    showText.setId(23390);
                    showText.setTextSize(9);
                    mRoot.addView(showText);
                } else {
                    showText.setText(timeStr);
                }

                //更新窗口位置
                View searchForView = searchForView(viewName, mRoot);
                if (searchForView == null) {
                    showText.setVisibility(View.GONE);
                } else {
                    showText.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_RIGHT, searchForView.getId());
                    params.addRule(RelativeLayout.ALIGN_BOTTOM, searchForView.getId());

                    params.leftMargin = Utils.dip2px(mRoot.getContext(), -5);
                    params.topMargin = Utils.dip2px(mRoot.getContext(), -3);

                    showText.setLayoutParams(params);
                }
            }
        };
    }
}
