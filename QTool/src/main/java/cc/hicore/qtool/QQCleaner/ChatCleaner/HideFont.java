package cc.hicore.qtool.QQCleaner.ChatCleaner;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "屏蔽聊天特效字体",groupName = "聊天净化",type = 1,id = "ChatFontHider",targetID = 2)
public class HideFont extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable)param.setResult(null);
        });
        XPBridge.HookBefore(m[1],param -> {
            if (IsEnable)param.setResult(null);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(HideFont.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.TextItemBuilder"), "b",void.class,new Class[]{
                View.class, Classes.ChatMessage()
        });
        m[1] = MMethod.FindMethod(MClass.loadClass("com.etrump.mixlayout.ETTextView"),"setFont",void.class,new Class[]{
                MClass.loadClass("com.etrump.mixlayout.ETFont"), long.class,int.class,
        });
        return m;
    }
}
