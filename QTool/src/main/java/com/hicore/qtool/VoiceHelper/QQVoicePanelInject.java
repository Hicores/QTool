package com.hicore.qtool.VoiceHelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.qtool.R;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;


@HookItem(isRunInAllProc = false,isDelayInit = false)
//@UIItem(itemType = 1,itemName = "语音面板",itemDesc = "可以在QQ的发送语音界面注入打开语音面板按钮",mainItemID = 1,ID = "VoicePanelInject")
public class QQVoicePanelInject extends BaseHookItem implements BaseUiItem {
    boolean IsEnable = false;
    @Override
    public boolean startHook() throws Throwable {
        Member cons = getMethod();
        XPBridge.HookAfter(cons,param -> {
            RelativeLayout RLayout = (RelativeLayout) param.thisObject;
            ImageView image = new ImageView(RLayout.getContext());
            image.setImageResource(R.drawable.voice_panel);

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
