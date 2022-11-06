package cc.hicore.qtool.XPWork.QQProxy;

import java.util.HashSet;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;

@XPItem(name = "Proxy_Troop_Join_Exit", itemType = XPItem.ITEM_Hook)
public class TroopJoinExitProxy {
    private static final String TAG = "TroopEventProxy";
    private static final HashSet<String> cacheExitMap = new HashSet<>();
    private static final HashSet<String> cacheJoinMap = new HashSet<>();

    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook_1", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.api.impl.TroopCreateInfoServiceImpl"),
                "deleteInInviteList", new Class[]{String.class, String.class}));
        container.addMethod("hook_2", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.api.impl.TroopCreateInfoServiceImpl"),
                "isInInviteList", new Class[]{String.class, String.class}));
    }

    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1() {
        return param -> {
            String GroupUin = (String) param.args[0];
            String UserUin = (String) param.args[1];
            if (cacheExitMap.contains(GroupUin + "-" + UserUin)) return;
            cacheJoinMap.remove(GroupUin + "-" + UserUin);
            cacheExitMap.add(GroupUin + "-" + UserUin);

            PluginMessageProcessor.onExitEvent(GroupUin, UserUin, "-1");
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2() {
        return param -> {
            String GroupUin = String.valueOf(param.args[0]);
            String UserUin = (String) param.args[1];
            if (cacheJoinMap.contains(GroupUin + "-" + UserUin)) return;
            cacheExitMap.remove(GroupUin + "-" + UserUin);
            cacheJoinMap.add(GroupUin + "-" + UserUin);

            PluginMessageProcessor.onJoinEvent(GroupUin, UserUin, "-1", "-1");
        };
    }
}
