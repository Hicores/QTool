package cc.hicore.qtool.QQMessage;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isDelayInit = false, isRunInAllProc = false)
public class QQMsgApiChecker extends BaseHookItem {
    private static String TAG = "QQMsgApiChecker";
    private static StringBuilder builder = new StringBuilder();

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public String getErrorInfo() {
        return builder.toString();
    }

    @Override
    public boolean check() {
        builder.setLength(0);
        if (!GetMessageByTimeSeq()) builder.append("GetMessageByTimeSeq\n");
        if (!revokeMsg()) builder.append("revokeMsg\n");
        if (!AddMsg()) builder.append("AddMsg\n");
        if (!AddAndSendMsg()) builder.append("AddAndSendMsg\n");
        if (!RevokeTroopFile()) builder.append("RevokeTroopFile\n");
        if (!MessageFacade_RevokeMessage()) builder.append("MessageFacade_RevokeMessage\n");
        if (!build_struct()) builder.append("build_struct\n");
        if (!build_arkapp()) builder.append("build_arkapp\n");
        if (!buildPic0()) builder.append("buildPic0\n");
        if (!buildText()) builder.append("buildText\n");
        if (!buildMix()) builder.append("buildMix\n");
        if (!sendFileByPath()) builder.append("sendFileByPath\n");
        if (!sendText()) builder.append("sendText\n");
        if (!sendPic()) builder.append("sendPic\n");
        if (!sendStruct()) builder.append("sendStruct\n");
        if (!sendArkApp()) builder.append("sendArkApp\n");
        if (!sendVoice()) builder.append("sendVoice\n");
        if (!sendMix()) builder.append("sendMix\n");
        if (!sendReply()) builder.append("sendReply\n");
        if (!sendPaiyiPai()) builder.append("sendPaiyiPai\n");
        if (!RepeatFile()) builder.append("RepeatFile\n");
        if (!sendVideo()) builder.append("sendVideo\n");
        if (!sendShakeWindow()) builder.append("sendShakeWindow\n");
        if (!sendAntEmo()) builder.append("sendAntEmo\n");
        if (!addTip0()) builder.append("addTip0\n");
        if (!sendReply_()) builder.append("sendReply_Builder\n");

        return builder.length() == 0;
    }

    private static boolean sendReply_() {
        try {
            Method SourceInfo = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.reply.ReplyMsgUtils", "a", MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                            int.class, long.class, String.class
                    }
            );
            Method BuildMsg = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", "a", MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            String.class, int.class,
                            MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                            String.class
                    }
            );
            return SourceInfo != null && BuildMsg != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean addTip0() {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", "a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                    int.class
            });
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendAntEmo() {
        try {
            Method m;
            if (HostInfo.getVerCode() < 5865) {
                m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack", "sendAniSticker",
                        boolean.class, new Class[]{int.class, MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo")}
                );
            } else {
                m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack", "sendAniSticker",
                        boolean.class, new Class[]{int.class, MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"), int.class}
                );
            }
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendVideo() {
        try {
            Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.ChatActivityFacade"), "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForShortVideo")
            });
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendShakeWindow() {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", "a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                    int.class
            });
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean RepeatFile() {
        try {
            Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgManager"),
                    "a", void.class, new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            int.class, MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo")
                    });
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendPaiyiPai() {
        try {
            Method m = MMethod.FindMethod("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler", "a", void.class, new Class[]{String.class, String.class, int.class});
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendReply() {
        try {
            Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender", "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                    int.class,
                    int.class,
                    boolean.class
            });
            return mMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendMix() {
        try {
            Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender", "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    int.class
            });
            return mMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendVoice() {
        try {
            Method CallMethod =
                    HostInfo.getVerCode() < 5670 ?
                            MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "a", long.class, new Class[]{Classes.QQAppinterFace(), Classes.SessionInfo(), String.class}) :
                            MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "d", long.class, new Class[]{Classes.QQAppinterFace(), Classes.SessionInfo(), String.class});
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendArkApp() {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "a",
                    boolean.class, new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                            MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage")
                    });
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendStruct() {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "a",
                    void.class, new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                            MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg")
                    });
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendPic() {
        try {
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForPic"),
                    int.class
            });
            return hookMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendText() {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    Context.class,
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    String.class,
                    ArrayList.class
            });
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendFileByPath() {
        try {
            Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.filemanager.app.FileManagerEngine"),
                    "a", boolean.class, new Class[]{String.class, String.class, long.class, int.class});
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean buildMix() {
        try {
            Class<?> clz = MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory");
            for (Method m : clz.getDeclaredMethods()){
                if (m.getReturnType().equals(MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg")))return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean buildText() {
        try {
            Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", "a", MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), new Class[]{
                    MClass.loadClass("com.tencent.common.app.AppInterface"),
                    String.class,
                    String.class,
                    String.class,
                    int.class,
                    byte.class,
                    byte.class,
                    short.class,
                    String.class
            });
            return InvokeMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean build_arkapp() {
        try {
            Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage", "fromAppXml",
                    boolean.class, new Class[]{String.class});
            return med != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean buildPic0() {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", "a", MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"), new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    String.class
            });
            return CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean build_struct() {
        try {
            Method BuildStructMsg = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.structmsg.TestStructMsg"), "a",
                    MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), new Class[]{String.class});
            return BuildStructMsg != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean MessageFacade_RevokeMessage() {
        try {
            Method m =
                    HostInfo.getVerCode() < 5670 ?
                            MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade", "d", void.class, new Class[]{Classes.MessageRecord()}) :
                            MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade", "f", void.class, new Class[]{Classes.MessageRecord()});
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean RevokeTroopFile() {
        try {
            Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.AIORevokeMsgHelper"), "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForTroopFile")
            });
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean GetMessageByTimeSeq() {
        try {
            Object MessageFacade = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            Method m = MMethod.FindMethod(MessageFacade.getClass(), "c", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                    String.class, int.class, long.class
            });
            return m != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private static boolean revokeMsg() {
        try {
            Object MessageFacade = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            Object MsgCache = MMethod.CallMethodNoParam(HookEnv.AppInterface, "getMsgCache",
                    MClass.loadClass("com.tencent.mobileqq.service.message.MessageCache"));
            Method m = MMethod.FindMethod(MsgCache.getClass(), "b", void.class, new Class[]{boolean.class});
            return m != null;
        } catch (Exception e) {
            return false;
        }

    }

    private static boolean AddMsg() {
        try {
            Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    String.class
            });
            Object MessageFacade = MMethod.CallMethodNoParam(QQEnvUtils.getAppRuntime(), "getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            return InvokeMethod != null && MessageFacade != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean AddAndSendMsg() {
        try {
            Object MessageFacade = MMethod.CallMethodNoParam(QQEnvUtils.getAppRuntime(), "getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            Method mMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a", void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")
            });
            return MessageFacade != null && mMethod != null;
        } catch (Exception e) {
            return false;
        }
    }
}
