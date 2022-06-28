package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
@XPItem(name = "Proxy_Troop_Mute",itemType = XPItem.ITEM_Hook)
public class TroopMuteProxy{
    private static final String TAG = "TroopMuteProxy";
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"), null, void.class, new Class[]{
                String.class,
                long.class,
                long.class,
                int.class,
                String.class,
                String.class,
                boolean.class
        }));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"), null, void.class, new Class[]{
                String.class,
                String.class,
                long.class,
                long.class,
                int.class,
                boolean.class,
                boolean.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1(){
        return param -> {
            String GroupUin = (String) param.args[0];
            long TimeRest = (long) param.args[2];
            String AdminUin = (String) param.args[4];
            String Target = (String) param.args[5];

            PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMuteEvent(GroupUin, Target, AdminUin, TimeRest));
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> {
            String GroupUin = (String) param.args[0];
            String AdminUin = (String) param.args[1];
            long TimeRest = (long) param.args[3];
            boolean b = (boolean) param.args[5];

            if (b) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMuteEvent(GroupUin, "", AdminUin, TimeRest));
            } else {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMuteEvent(GroupUin, QQEnvUtils.getCurrentUin(), AdminUin, TimeRest));
            }
        };
    }
}
