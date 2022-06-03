package cc.hicore.qtool.ServerKiller;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "禁用LoggerWriter",groupName = "服务调节",targetID = 4,type = 1,id = "LogWriterKiller")
public class LogWriterKiller extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> param.setResult(null));
        XPBridge.HookBefore(m[1],param -> param.setResult(true));
        XPBridge.HookBefore(m[2],param -> param.setResult(null));
        XPBridge.HookBefore(m[3],param -> param.setResult(null));
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
        if (IsCheck) HookLoader.CallHookStart(LogWriterKiller.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[4];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.QLogItemManager"),"addLogItem",void.class,new Class[]{
                MClass.loadClass("com.tencent.qphone.base.util.QLogItem")
        });
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.LogWriterManager"),"writeLogItems",boolean.class,new Class[]{
                List.class
        });
        m[2] = MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.QLogItemManager"),"init",void.class,new Class[]{
                long.class
        });
        m[3] = MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.QLogItemManager$WriteHandler"),"tryInit",void.class,new Class[0]);

        return m;
    }
}
