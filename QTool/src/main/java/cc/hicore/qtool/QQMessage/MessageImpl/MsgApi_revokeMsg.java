package cc.hicore.qtool.QQMessage.MessageImpl;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;

@XPItem(name = "MsgApi_revokeMsg",itemType = XPItem.ITEM_Api)
public class MsgApi_revokeMsg {
    CoreLoader.XPItemInfo info;
    @ApiExecutor
    @VerController
    public void revoke(Object msg) throws Exception {
        Object MessageFacade = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMessageFacade",
                MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
        Object MsgCache = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMsgCache",
                MClass.loadClass("com.tencent.mobileqq.service.message.MessageCache"));
        ((Method)info.scanResult.get("updateCache")).invoke(MsgCache,true);
        ((Method)info.scanResult.get("revoke")).invoke(MessageFacade,msg);
    }
    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    public void getCacheMethod(MethodContainer container) throws Exception {
        Object MsgCache = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMsgCache",
                MClass.loadClass("com.tencent.mobileqq.service.message.MessageCache"));
        container.addMethod("updateCache",MMethod.FindMethod(MsgCache.getClass(), "b", void.class, new Class[]{boolean.class}));
    }
    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_11)
    public void getMethod(MethodContainer container){
        container.addMethod("revoke",MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade", "d", void.class, new Class[]{Classes.MessageRecord()}));
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_11,max_targetVer = QQVersion.QQ_8_8_93)
    public void get2Revoke(MethodContainer container){
        container.addMethod("revoke",MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade", "f", void.class, new Class[]{Classes.MessageRecord()}));
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getRevoke_8893(MethodContainer container){
        Method target = MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),null,void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageForFile")
        });
        container.addMethod(MethodFinderBuilder.newFinderWhichMethodInvoking("revoke",target,m -> m.getDeclaringClass().getName().equals("com.tencent.imcore.message.QQMessageFacade")));
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getUpdateCache(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("getMethod_Before","qq queryEmojiInfo: result:",m -> true));
        container.addMethod(MethodFinderBuilder.newFinderByMethodInvokingLinked("revoke","getMethod_Before",m -> ((Method)m).getDeclaringClass().equals(MClass.loadClass("com.tencent.mobileqq.service.message.MessageCache")) && ((Method)m).getReturnType().equals(void.class) && ((Method)m).getParameterCount() == 1 && ((Method)m).getParameterTypes()[0].equals(boolean.class)));
    }
}
