package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

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
@UIItem(name = "屏蔽下拉小程序",groupName = "主界面净化",id = "HideMainAppEntry",targetID = 2,type = 1)
public class HideAppEntry extends BaseHookItem implements BaseUiItem {
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
        if (IsCheck) HookLoader.CallHookStart(HideAppEntry.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.mini.api.impl.MiniAppServiceImpl"),"createMiniAppEntryManager",MClass.loadClass("com.tencent.mobileqq.mini.entry.MiniAppPullInterface"),new Class[]{
                boolean.class, Activity.class, Object.class, Object.class, Object.class, Object.class, ViewGroup.class
        });
    }
}
