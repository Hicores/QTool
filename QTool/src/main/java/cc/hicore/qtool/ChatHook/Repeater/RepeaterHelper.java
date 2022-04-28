package cc.hicore.qtool.ChatHook.Repeater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.HashMap;

import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import de.robv.android.xposed.XposedBridge;

@SuppressLint("ResourceType")
public class RepeaterHelper {
    private static final HashMap<String,String> supportMessageTypes = new HashMap<>();
    static {
        supportMessageTypes.put("MessageForPic","RelativeLayout");
        supportMessageTypes.put("MessageForText","ETTextView");
        supportMessageTypes.put("MessageForLongTextMsg","ETTextView");
        supportMessageTypes.put("MessageForFoldMsg","ETTextView");
        supportMessageTypes.put("MessageForPtt","BreathAnimationLayout");
        supportMessageTypes.put("MessageForMixedMsg","MixedMsgLinearLayout");
        supportMessageTypes.put("MessageForReplyText","SelectableLinearLayout");
        supportMessageTypes.put("MessageForScribble","RelativeLayout");
        supportMessageTypes.put("MessageForMarketFace","RelativeLayout");
        supportMessageTypes.put("MessageForArkApp","ArkAppRootLayout");
        supportMessageTypes.put("MessageForStructing","RelativeLayout");
        supportMessageTypes.put("MessageForTroopPobing","LinearLayout");
        supportMessageTypes.put("MessageForTroopEffectPic","RelativeLayout");
        supportMessageTypes.put("MessageForAniSticker","FrameLayout");
        supportMessageTypes.put("MessageForArkFlashChat","ArkAppRootLayout");
        supportMessageTypes.put("MessageForShortVideo","RelativeLayout");
        supportMessageTypes.put("MessageForPokeEmo","RelativeLayout");
    }
    public static void createRepeatIcon(RelativeLayout baseChatItem, Object ChatMsg) throws Exception {
        boolean isSendFromLocal = MMethod.CallMethodNoParam(ChatMsg,"isSendFromLocal",boolean.class);
        Context context = baseChatItem.getContext();
        ResUtils.StartInject(context);
        String clzName = ChatMsg.getClass().getSimpleName();
        if (supportMessageTypes.containsKey(clzName)){
            ImageButton imageButton =baseChatItem.findViewById(23333);
            if (imageButton == null){
                imageButton = new ImageButton(context);
                imageButton.setImageResource(R.drawable.repeat);
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(Utils.dip2px(context,35), Utils.dip2px(context,35));
                imageButton.setAdjustViewBounds(true);
                imageButton.getBackground().setAlpha(0);
                imageButton.setMaxHeight(Utils.dip2px(context,35));
                imageButton.setMaxWidth(Utils.dip2px(context,35));
                imageButton.setId(23333);
                imageButton.setTag(ChatMsg);
                imageButton.setOnClickListener(v-> {
                    try {
                        Repeater.Repeat(HookEnv.SessionInfo,v.getTag());
                    } catch (Exception e) {
                        Utils.ShowToast("复读发生错误:\n" +e);
                    }
                });
                baseChatItem.addView(imageButton,param);
            }else {
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setTag(ChatMsg);
            }

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
            String attachName = supportMessageTypes.get(clzName);
            View attachView = findView(attachName,baseChatItem);
            if (attachView != null){
                if (isSendFromLocal){
                    param.removeRule(RelativeLayout.ALIGN_RIGHT);
                    param.removeRule(RelativeLayout.ALIGN_TOP);
                    param.removeRule(RelativeLayout.ALIGN_LEFT);
                    param.addRule(RelativeLayout.ALIGN_TOP,attachView.getId());
                    param.addRule(RelativeLayout.ALIGN_LEFT,attachView.getId());
                    param.leftMargin=Utils.dip2px(context,-5);
                    param.topMargin = Utils.dip2px(context,-5);
                }else {
                    param.removeRule(RelativeLayout.ALIGN_RIGHT);
                    param.removeRule(RelativeLayout.ALIGN_TOP);
                    param.removeRule(RelativeLayout.ALIGN_LEFT);
                    param.addRule(RelativeLayout.ALIGN_TOP,attachView.getId());
                    param.addRule(RelativeLayout.ALIGN_RIGHT,attachView.getId());
                    param.rightMargin=Utils.dip2px(context,-5);
                    param.topMargin = Utils.dip2px(context,-5);
                }
                imageButton.setLayoutParams(param);
            }
        }else {
            ImageButton imageButton =baseChatItem.findViewById(23333);
            if (imageButton != null)baseChatItem.removeView(imageButton);
        }
    }
    public static View findView(String Name, ViewGroup vg) {
        for(int i=0;i<vg.getChildCount();i++) {
            if(vg.getChildAt(i).getClass().getSimpleName().contains(Name)) {
                return vg.getChildAt(i);
            }
        }
        return null;
    }
}
