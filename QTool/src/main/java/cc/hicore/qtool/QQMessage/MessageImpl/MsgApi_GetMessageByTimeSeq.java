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
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;

@XPItem(name = "MsgApi_GetMessageByTimeSeq",itemType = XPItem.ITEM_Api)
public class MsgApi_GetMessageByTimeSeq {
    CoreLoader.XPItemInfo info;
    @ApiExecutor
    @VerController
    public Object api_invoker(String uin, int istroop, long msgseq) throws Exception {
        Object MessageFacade = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMessageFacade",
                MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
        return ((Method)info.scanResult.get("invoker")).invoke(MessageFacade,uin,istroop,msgseq);
    }
    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    public void getMethod(MethodContainer container){
        container.addMethod("invoker",MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),
                "c", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                        String.class, int.class, long.class
                }));
    }
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getMethod_8_8_93(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("get_before","counter",m -> m.getDeclaringClass().getName().contains("com.tencent.mobileqq.guild.chatpie.msgviewbuild.builder.GuildReplyTextItemBuilder")));
        container.addMethod(MethodFinderBuilder.newFinderByMethodInvokingLinked("invoker","get_before",m -> m.getDeclaringClass().getName().equals("com.tencent.imcore.message.BaseQQMessageFacade")));

    }
}
