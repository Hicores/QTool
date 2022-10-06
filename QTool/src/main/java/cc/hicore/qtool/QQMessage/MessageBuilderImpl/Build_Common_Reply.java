package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

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
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.QQMsgSendUtils;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.QQMessage.QQSessionUtils;

@XPItem(name = "Build_Common_Reply",itemType = XPItem.ITEM_Api)
public class Build_Common_Reply {
    CoreLoader.XPItemInfo info;
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void sendReply_(String GroupUin, Object source, String mixText) throws Exception {
        String Uins = MField.GetField(source, "senderuin", String.class);
        Object Appinterface = QQEnvUtils.getAppRuntime();
        Method SourceInfo = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.reply.ReplyMsgUtils", null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                new Class[]{
                        MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                        MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                        int.class, long.class, String.class
                }
        );
        Object SourceInfoObj = SourceInfo.invoke(null, Appinterface, source, 0, Long.parseLong(Uins), QQGroupUtils.Group_Get_Name(GroupUin));
        Method BuildMsg = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText"),
                new Class[]{
                        MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                        String.class, int.class,
                        MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                        String.class
                }
        );
        if (mixText.contains("[PicUrl=")) {
            String strTo = mixText.substring(0, mixText.indexOf("["));
            Object Builded = BuildMsg.invoke(null, Appinterface, GroupUin, 1, SourceInfoObj, strTo.isEmpty() ? " " : strTo);
            QQMsgSendUtils.decodeAndSendMsg(GroupUin, "", mixText.substring(mixText.indexOf("[")), Builded);
        } else {
            Object Builded = BuildMsg.invoke(null, Appinterface, GroupUin, 1, SourceInfoObj, mixText.isEmpty() ? " " : mixText);
            QQMsgSender.sendReply(QQSessionUtils.Build_SessionInfo(GroupUin, ""), Builded);
        }
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void sendReply_890(String GroupUin, Object source, String mixText) throws Exception {
        String Uins = MField.GetField(source, "senderuin", String.class);
        Object Appinterface = QQEnvUtils.getAppRuntime();
        Method SourceInfo = (Method) info.scanResult.get("invoke");
        Object SourceInfoObj = SourceInfo.invoke(null, Appinterface, source, 0, Long.parseLong(Uins), QQGroupUtils.Group_Get_Name(GroupUin));

        Method BuildMsg = MMethod.FindMethod(info.scanResult.get("builderMethod").getDeclaringClass(), null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText"),
                new Class[]{
                        MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                        String.class, int.class,
                        MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                        String.class
                }
        );
        if (mixText.contains("[PicUrl=")) {
            String strTo = mixText.substring(0, mixText.indexOf("["));
            Object Builded = BuildMsg.invoke(null, Appinterface, GroupUin, 1, SourceInfoObj, strTo.isEmpty() ? " " : strTo);
            QQMsgSendUtils.decodeAndSendMsg(GroupUin, "", mixText.substring(mixText.indexOf("[")), Builded);
        } else {
            Object Builded = BuildMsg.invoke(null, Appinterface, GroupUin, 1, SourceInfoObj, mixText.isEmpty() ? " " : mixText);
            QQMsgSender.sendReply(QQSessionUtils.Build_SessionInfo(GroupUin, ""), Builded);
        }
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void findMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("invoke","generateSourceInfo sender uin exception:",m->true));
        container.addMethod(MethodFinderBuilder.newFinderByString("builderMethod","createMsgRecordFromDB", m->true));
    }
}
