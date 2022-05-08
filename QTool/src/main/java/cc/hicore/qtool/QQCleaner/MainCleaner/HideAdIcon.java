package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "隐藏主界面右上角入口",desc = "(不支持旧版QQ)可能包含小世界入口等",groupName = "主界面净化",targetID = 2,type = 1,id = "HideMainAdEntry")
public class HideAdIcon extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable){
                param.setResult(null);
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
        return false;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(HideAdIcon.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[3];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.ConversationTitleBtnCtrl"),"a",void.class,new Class[0]);
        return m;
    }
}
