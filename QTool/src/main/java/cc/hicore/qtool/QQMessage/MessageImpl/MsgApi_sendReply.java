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
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(itemType = XPItem.ITEM_Api, name = "MsgApi_sendReply")
public class MsgApi_sendReply {
    CoreLoader.XPItemInfo info;

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void MethodScaner(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("method", "sendReplyMessage chatMessage is null", m -> true));
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void send(Object _Session, Object replyRecord) throws Exception {
        Object Call = MMethod.CallStaticMethodNoParam(MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"), null, MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"));
        Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender", null, void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                Classes.BaseSessionInfo(),
                int.class,
                int.class,
                boolean.class
        });
        mMethod.invoke(Call, QQEnvUtils.getAppRuntime(), replyRecord, _Session, 2, 0, false);
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void send_890(Object _Session, Object replyRecord) throws Exception {
        Method mMethod = MMethod.FindMethod(info.scanResult.get("method").getDeclaringClass(), null, void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                Classes.BaseSessionInfo(),
                int.class,
                int.class,
                boolean.class
        });
        Object Call = MMethod.CallStaticMethodNoParam(info.scanResult.get("method").getDeclaringClass(), null, info.scanResult.get("method").getDeclaringClass());
        mMethod.invoke(Call, QQEnvUtils.getAppRuntime(), replyRecord, _Session, 2, 0, false);
    }
}
