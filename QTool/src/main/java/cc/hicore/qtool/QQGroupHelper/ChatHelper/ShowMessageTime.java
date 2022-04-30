package cc.hicore.qtool.QQGroupHelper.ChatHelper;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@UIItem(name = "在消息右下角显示时间", type = 1, id = "ShowMessageTime", targetID = 1,groupName = "聊天界面增强")
@HookItem(isDelayInit = true, isRunInAllProc = false)
public class ShowMessageTime extends BaseHookItem implements BaseUiItem {
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

    boolean IsEnable;

    @Override
    @SuppressLint("ResourceType")
    public boolean startHook() throws Throwable {
        Method m = getMethod();
        XPBridge.HookAfter(m, param -> {
            Object mGetView = param.getResult();
            if (!IsEnable || !(mGetView instanceof RelativeLayout)) return;
            List msgList = MField.GetField(param.thisObject, param.thisObject.getClass(), "a", List.class);
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
        if (IsCheck) {
            HookLoader.CallHookStart(ShowMessageTime.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }

    public Method getMethod() {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        return m;
    }

    private static View searchForView(String Name, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            if (vg.getChildAt(i).getClass().getSimpleName().contains(Name)) {
                return vg.getChildAt(i);
            }
        }
        return null;
    }
}
