package cc.hicore.qtool.ChatHook.ChatCracker;

import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "去除回复自动艾特",groupName = "聊天界面增强",targetID = 1,type = 1,id = "DisableReplayAt")
public class DisableReplayAt extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
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
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(DisableReplayAt.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.rebuild.input.InputUIUtils"),"a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.AIOContext"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                boolean.class
        });
    }
}
