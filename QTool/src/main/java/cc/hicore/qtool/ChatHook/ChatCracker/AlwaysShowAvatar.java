package cc.hicore.qtool.ChatHook.ChatCracker;

import static cc.hicore.qtool.ChatHook.Repeater.RepeaterHelper.findView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "总是显示头像",desc = "针对某些全屏卡片",groupName = "聊天净化",id = "AlwaysShowAvatar",type = 1,targetID = 2)
public class AlwaysShowAvatar extends BaseHookItem implements BaseUiItem {
    @Override
    public String getTag() {
        return "总是显示头像";
    }

    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            if (IsEnable){
                Object mGetView = param.getResult();
                RelativeLayout mLayout;
                if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
                View avatar = findView("VasAvatar",mLayout);
                if (avatar != null){
                    if (avatar.getVisibility() != View.VISIBLE){
                        avatar.setVisibility(View.VISIBLE);
                    }
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
        if (IsEnable) HookLoader.CallHookStart(AlwaysShowAvatar.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
    }
}
