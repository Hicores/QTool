package cc.hicore.qtool.XPWork.QQProxy;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;

@XPItem(name = "Proxy_Guild_Msg",itemType = XPItem.ITEM_Hook,targetVer = QQVersion.QQ_8_8_35)
public class BaseGuildMsgProxy{
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.GuildOnlineMessageProcessor"), null,
                void.class, new Class[]{Classes.MessageRecord()}));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.api.impl.GuildMessageUtilsApiImpl"), "handleSelfSendMsg",
                void.class, new Class[]{Classes.AppInterface(), Classes.MessageRecord(), Classes.MessageRecord(), int.class}));
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0,max_targetVer = QQVersion.QQ_8_9_8)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook_1","updateDirectMessageNodeIfNeeded(), message source is invalid! channelId: ",m -> MMethod.FindMethod(m.getDeclaringClass(), null,
                void.class, new Class[]{Classes.MessageRecord()})));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.api.impl.GuildMessageUtilsApiImpl"), "handleSelfSendMsg",
                void.class, new Class[]{Classes.AppInterface(), Classes.MessageRecord(), Classes.MessageRecord(), int.class}));
    }
    @VerController(targetVer = QQVersion.QQ_8_9_8)
    @MethodScanner
    public void getHookMethod_898(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook_1","processPush : after checkAndHandleSelfSendMessage, msgListSize: ",m -> MMethod.FindMethod(m.getDeclaringClass(), null,
                void.class, new Class[]{Classes.MessageRecord()})));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.api.impl.GuildMessageUtilsApiImpl"), "handleSelfSendMsg",
                void.class, new Class[]{Classes.AppInterface(), Classes.MessageRecord(), Classes.MessageRecord(), int.class}));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1(){
        return param -> PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMessage(param.args[0]));
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> {
            if (!param.args[1].getClass().getSimpleName().equals("MessageRecord")) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onMessage(param.args[1]));
            }
        };
    }
}
