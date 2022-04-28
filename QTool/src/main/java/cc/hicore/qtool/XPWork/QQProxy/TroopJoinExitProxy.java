package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;
import java.util.HashSet;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isDelayInit = false, isRunInAllProc = false)
public class TroopJoinExitProxy extends BaseHookItem {
    private static final String TAG = "TroopEventProxy";
    private static final HashSet<String> cacheExitMap = new HashSet<>();
    private static final HashSet<String> cacheJoinMap = new HashSet<>();

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0], param -> {
            String GroupUin = (String) param.args[0];
            String UserUin = (String) param.args[1];
            if (cacheExitMap.contains(GroupUin + "-" + UserUin)) return;
            cacheJoinMap.remove(GroupUin + "-" + UserUin);
            cacheExitMap.add(GroupUin + "-" + UserUin);

            PluginMessageProcessor.onExitEvent(GroupUin, UserUin, "-1");
        });
        XPBridge.HookBefore(m[1], param -> {
            String GroupUin = String.valueOf(param.args[0]);
            String UserUin = (String) param.args[1];
            if (cacheJoinMap.contains(GroupUin + "-" + UserUin)) return;
            cacheExitMap.remove(GroupUin + "-" + UserUin);
            cacheJoinMap.add(GroupUin + "-" + UserUin);

            PluginMessageProcessor.onJoinEvent(GroupUin, UserUin, "-1", "-1");
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    public Method[] getMethod() {
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.api.impl.TroopCreateInfoServiceImpl"),
                "deleteInInviteList", new Class[]{String.class, String.class});
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.api.impl.TroopCreateInfoServiceImpl"),
                "isInInviteList", new Class[]{String.class, String.class});
        return m;
    }
}
