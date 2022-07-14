package cc.hicore.qtool.XPWork.QQProxy;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;

@XPItem(name = "Proxy_Base_msg",itemType = XPItem.ITEM_Hook)
public class BaseMsgProxy{
    private static final String TAG = "BaseMsgProxy";
    private static final long StartTime = System.currentTimeMillis();
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.BaseMessageManager"), "a", void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.persistence.EntityManager"),
                boolean.class, boolean.class, boolean.class, boolean.class,
                MClass.loadClass("com.tencent.imcore.message.BaseMessageManager$AddMessageContext")
        }));
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","addMessage set sendmsg extra ",m -> m.getDeclaringClass().getName().startsWith("com.tencent.imcore.message.")));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> {
            Object MessageRecord = param.args[0];
            if (System.currentTimeMillis() - StartTime < 10 * 1000) return;
            PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMessage(MessageRecord));
        };
    }
}
