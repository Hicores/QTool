package com.hicore.qtool.VoiceHelper.Hooker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.R;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;


@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(itemType = 1,itemName = "语音面板",itemDesc = "可以在QQ的发送语音界面注入打开语音面板按钮",mainItemID = 1,ID = "VoicePanelInject")
public class QQVoicePanelInject extends BaseHookItem implements BaseUiItem {
    boolean IsEnable = false;
    @Override
    public boolean startHook() throws Throwable {
        Member cons = getMethod();

        XPBridge.HookAfter(cons,param -> {
            int mSpeakID = HookEnv.AppContext.getResources().getIdentifier("press_to_speak_iv","id", HookEnv.AppContext.getPackageName());
            RelativeLayout RLayout = (RelativeLayout) param.thisObject;
            ResUtils.StartInject(RLayout.getContext());
            ImageView image = new ImageView(RLayout.getContext());
            image.setImageResource(R.drawable.voice_panel);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.dip2px(RLayout.getContext(),25), Utils.dip2px(RLayout.getContext(),25));
            params.addRule(RelativeLayout.BELOW,mSpeakID);
            params.addRule(RelativeLayout.CENTER_IN_PARENT,1);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,1);

            RLayout.addView(image,params);
            image.setOnClickListener(v-> Utils.ShowToast("Click"));
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
        if (IsCheck){
            HookLoader.CallHookStart(QQVoicePanelInject.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }
    public Member getMethod(){
        Class clz = MClass.loadClass("com.tencent.mobileqq.activity.aio.audiopanel.PressToSpeakPanel");
        for(Constructor cons : clz.getDeclaredConstructors()){
            if (cons.getParameterCount() == 2){
                if (cons.getParameterTypes()[0] == Context.class && cons.getParameterTypes()[1] == AttributeSet.class){
                    cons.setAccessible(true);
                    return cons;
                }
            }
        }
        return null;
    }
}
