package cc.hicore.qtool.XPWork.QQProxy;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class TroopMuteProxy extends BaseHookItem {
    private static final String TAG = "TroopMuteProxy";
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0], param -> {
            String GroupUin = (String) param.args[0];
            long TimeRest = (long) param.args[2];
            String AdminUin = (String) param.args[4];
            String Target = (String) param.args[5];

            PluginMessageProcessor.submit(()->PluginMessageProcessor.onMuteEvent(GroupUin,Target,AdminUin,TimeRest));
        });
        XPBridge.HookBefore(m[1],param -> {
            String GroupUin = (String) param.args[0];
            String AdminUin = (String) param.args[1];
            long TimeRest = (long) param.args[3];
            boolean b = (boolean) param.args[5];

            if (b){
                PluginMessageProcessor.submit(()->PluginMessageProcessor.onMuteEvent(GroupUin, "",AdminUin,TimeRest));
            }else {
                PluginMessageProcessor.submit(()->PluginMessageProcessor.onMuteEvent(GroupUin, QQEnvUtils.getCurrentUin(),AdminUin,TimeRest));
            }

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
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"),"a",void.class,new Class[]{
                String.class,
                long.class,
                long.class,
                int.class,
                String.class,
                String.class,
                boolean.class
        });
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"),"a",void.class,new Class[]{
                String.class,
                String.class,
                long.class,
                long.class,
                int.class,
                boolean.class,
                boolean.class
        });
        return m;
    }
}
