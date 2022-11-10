package cc.hicore.qtool.JavaPlugin.Controller;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;

public class PluginMessageProcessor {
    private static final ExecutorService messageProcessor = Executors.newSingleThreadExecutor();
    private static final ExecutorService pluginDispatcher = Executors.newSingleThreadExecutor();
    static HashMap<String, String> fileDLCache = new HashMap<>();

    {
        messageProcessor.submit(() -> Thread.currentThread().setName("QTool_MessageProcessor"));
    }

    public static void onMessage(Object msg) {
        onMessage0(msg);
    }

    public static void submit(Runnable run) {
        messageProcessor.submit(() -> {
            try {
                run.run();
            } catch (Exception e) {
                Log.e("PluginMessageProcessor", Log.getStackTraceString(e));
            }
        });
    }

    private static void submit2(Runnable run) {
        pluginDispatcher.submit(() -> {
            Thread.currentThread().setName("QTool_Plugin_Worker");
            run.run();
        });
    }

    //解析消息并封装成插件可用的消息对象
    private static void onMessage0(Object msg) {
        PluginInfo.EarlyInfo early = decodeEarly(msg);
        try {
            PluginInfo.MessageData data = new PluginInfo.MessageData();
            data.MessageTime = MField.GetField(msg, "time", long.class);
            data.IsGroup = early.istroop == 1 || early.istroop == 10014;
            data.IsChannel = early.istroop == 10014;
            if (data.IsChannel) {
                data.ChannelID = early.ChannelID;
                data.GuildID = early.GuildID;
            }
            data.GroupUin = early.GroupUin;
            data.UserUin = early.UserUin;
            data.AppInterface = HookEnv.AppInterface;
            data.msg = msg;
            data.IsSend = MMethod.CallMethodNoParam(msg, "isSendFromLocal", boolean.class);
            data.SessionInfo = HookEnv.SessionInfo;
            String clzName = msg.getClass().getSimpleName();
            data.SenderNickName = QQGroupUtils.Group_Get_Member_Name(data.GroupUin, data.UserUin);
            if (clzName.equals("MessageForText") || clzName.equals("MessageForLongTextMsg") || clzName.equals("MessageForFoldMsg")) {
                data.MessageType = 1;
                data.MessageContent = MField.GetField(msg, "msg", String.class);
                ArrayList<String> mAtList = new ArrayList<>();
                String extra = MField.GetField(msg, msg.getClass(), "extStr", String.class);
                try {
                    JSONObject mJson = new JSONObject(extra);
                    extra = mJson.optString("troop_at_info_list");
                    ArrayList atList = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), "getTroopMemberInfoFromExtrJson", ArrayList.class, new Class[]{String.class}, extra);
                    if (atList != null) {
                        for (Object mAtInfo : atList) {
                            mAtList.add("" + (long) MField.GetField(mAtInfo, "uin", long.class));
                        }
                    }
                } catch (Exception e) {
                }
                data.mAtList = mAtList;
                data.AtList = mAtList.toArray(new String[0]);
            } else if (clzName.equals("MessageForPic")) {
                data.MessageType = 1;
                String PicMd5 = MField.GetField(msg, "md5", String.class);
                String PicPath = MField.GetField(msg, "bigMsgUrl", String.class);
                if (TextUtils.isEmpty(PicPath)) {
                    PicPath = "https://gchat.qpic.cn/gchatpic_new/0/0-0-" + PicMd5 + "/0?term=2";
                }
                data.MessageContent = "[PicUrl=" + PicPath + "]";
                data.mAtList = new ArrayList();
                data.AtList = new String[0];
            } else if (clzName.equals("MessageForMixedMsg")) {
                data.MessageType = 3;
                List mEleList = MField.GetField(msg, "msgElemList", List.class);
                String MsgSummary = "";
                for (Object MessageRecord : mEleList) {
                    if (MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForText") ||
                            MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForLongTextMsg")) {
                        String str = MField.GetField(MessageRecord, "msg", String.class);
                        if (!TextUtils.isEmpty(str)) MsgSummary = MsgSummary + str;


                    } else if (MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForPic")) {

                        String PicMd5 = MField.GetField(MessageRecord, "md5", String.class);

                        String PicPath = MField.GetField(MessageRecord, "bigMsgUrl", String.class);

                        if (TextUtils.isEmpty(PicPath)) {
                            PicPath = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + PicMd5 + "/0?term=2";
                        }
                        MsgSummary = MsgSummary + "[PicUrl=" + PicPath + "]";
                    }
                }
                data.MessageContent = MsgSummary;
            } else if (clzName.equals("MessageForStructing") || clzName.equals("MessageForArkApp")) {
                data.MessageType = 2;
                data.MessageContent = QQMessageUtils.getCardMsg(msg);
            } else if (clzName.equals("MessageForPtt")) {
                data.MessageType = 4;
                data.MessageContent = "[语音]MD5=" + MField.GetField(msg, "md5", String.class);
                data.FileUrl = "https://grouptalk.c2c.qq.com" +
                        MField.GetField(msg, "directUrl", String.class);
                data.LocalPath = MField.GetField(msg, "fullLocalPath", String.class);
            } else if (clzName.equals("MessageForTroopFile")) {
                data.MessageType = 5;
                data.MessageContent = "[文件]" + MField.GetField(msg, "fileName", String.class);
                data.FileUrl = MField.GetField(msg, "url", String.class);
                data.FileName = MField.GetField(msg, "fileName", String.class);
                data.FileSize = MField.GetField(msg, "fileSize", long.class);
            } else if (clzName.equals("MessageForReplyText")) {
                Object SourceInfo = MField.GetField(msg, "mSourceMsgInfo");
                if (SourceInfo != null) {
                    data.MessageType = 6;
                    data.MessageContent = MField.GetField(msg, "msg", String.class);
                    long uin = MField.GetField(SourceInfo, "mSourceMsgSenderUin");
                    data.ReplyTo = String.valueOf(uin);
                }
            }
            submit2(() -> PluginController.onMessage(early, data));

        } catch (Exception e) {
            LogUtils.error("MessageDecoder0", new RuntimeException("Can't decode msg:(" + msg.getClass().getName() + ")", e));
        }

    }

    //解析出消息中的发送者和群聊等信息
    private static PluginInfo.EarlyInfo decodeEarly(Object msg) {
        PluginInfo.EarlyInfo info = new PluginInfo.EarlyInfo();
        try {
            int istroop = MField.GetField(msg, "istroop", int.class);
            info.istroop = istroop;
            if (istroop == 1) {
                info.GroupUin = MField.GetField(msg, "frienduin", String.class);
                info.UserUin = MField.GetField(msg, "senderuin", String.class);
                return info;
            } else if (istroop == 0) {
                info.GroupUin = "";
                info.UserUin = MField.GetField(msg, "senderuin", String.class);
                return info;
            } else if (istroop == 1000) {
                boolean IsOnline = MMethod.CallMethodNoParam(msg, "isSendFromLocal", boolean.class);
                if (IsOnline) {
                    info.GroupUin = MField.GetField(msg, "senderuin", String.class);
                    info.GroupUin = QQGroupUtils.convertGroupCodeToUin(info.GroupUin);
                    info.UserUin = QQEnvUtils.getCurrentUin();
                } else {
                    info.GroupUin = MField.GetField(msg, "senderuin", String.class);
                    info.GroupUin = QQGroupUtils.convertGroupCodeToUin(info.GroupUin);
                    info.UserUin = MField.GetField(msg, "frienduin", String.class);
                }
                return info;
            } else if (istroop == 10014) {
                JSONObject ChannelData = MField.GetField(msg, "mExJsonObject");
                String GUILD_ID = ChannelData.optString("GUILD_ID");
                String ChannelID = MField.GetField(msg, "frienduin");
                info.GroupUin = GUILD_ID + "&" + ChannelID;
                info.UserUin = MField.GetField(msg, "senderuin");
                info.GuildID = GUILD_ID;
                info.ChannelID = ChannelID;
            } else {
                info.GroupUin = "";
                info.UserUin = "";
            }
            return info;
        } catch (Exception e) {
            return null;
        }
    }

    //禁言事件
    public static void onMuteEvent(String GroupUin, String UserUin, String OPUin, long time) {
        PluginController.checkAndInvoke(GroupUin, "OnTroopEvent", GroupUin, UserUin, OPUin, time);
    }

    //退群事件
    public static void onExitEvent(String GroupUin, String UserUin, String OPUin) {
        PluginController.checkAndInvoke(GroupUin, "OnTroopEvent", GroupUin, UserUin, 1);
        PluginController.checkAndInvoke(GroupUin, "onMemberExit", GroupUin, UserUin, TextUtils.isEmpty(OPUin) ? 1 : 2, OPUin);
    }

    //加群事件
    public static void onJoinEvent(String GroupUin, String UserUin, String Invitor, String AcceptOPUin) {
        PluginController.checkAndInvoke(GroupUin, "OnTroopEvent", GroupUin, UserUin, 2);
    }

    //撤回事件
    public static void onRevoke(Object msg, String OPUin) {
        PluginInfo.EarlyInfo early = decodeEarly(msg);
        try {
            PluginInfo.MessageData data = new PluginInfo.MessageData();
            data.AdminUin = OPUin;
            data.MessageTime = MField.GetField(msg, "time", long.class);
            data.IsGroup = early.istroop == 1 || early.istroop == 10014;
            data.IsChannel = early.istroop == 10014;
            data.GroupUin = early.GroupUin;
            data.UserUin = early.UserUin;
            data.AppInterface = HookEnv.AppInterface;
            data.msg = msg;
            data.IsSend = MMethod.CallMethodNoParam(msg, "isSendFromLocal", boolean.class);
            data.SessionInfo = HookEnv.SessionInfo;
            String clzName = msg.getClass().getSimpleName();
            data.SenderNickName = QQGroupUtils.Group_Get_Member_Name(data.GroupUin, data.UserUin);
            if (clzName.equals("MessageForText") || clzName.equals("MessageForLongTextMsg") || clzName.equals("MessageForFoldMsg")) {
                data.MessageType = 1;
                data.MessageContent = MField.GetField(msg, "msg", String.class);
                ArrayList<String> mAtList = new ArrayList<>();
                String extra = MField.GetField(msg, msg.getClass(), "extStr", String.class);
                try {
                    JSONObject mJson = new JSONObject(extra);
                    extra = mJson.optString("troop_at_info_list");
                    ArrayList atList = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), "getTroopMemberInfoFromExtrJson", ArrayList.class, new Class[]{String.class}, extra);
                    if (atList != null) {
                        for (Object mAtInfo : atList) {
                            mAtList.add("" + (long) MField.GetField(mAtInfo, "uin", long.class));
                        }
                    }
                } catch (Exception e) {
                }
                data.mAtList = mAtList;
                data.AtList = mAtList.toArray(new String[0]);
            } else if (clzName.equals("MessageForPic")) {
                data.MessageType = 1;
                String PicMd5 = MField.GetField(msg, "md5", String.class);
                String PicPath = MField.GetField(msg, "bigMsgUrl", String.class);
                if (TextUtils.isEmpty(PicPath)) {
                    PicPath = "https://gchat.qpic.cn/gchatpic_new/0/0-0-" + PicMd5 + "/0?term=2";
                }
                data.MessageContent = "[PicUrl=" + PicPath + "]";
                data.mAtList = new ArrayList();
                data.AtList = new String[0];
            } else if (clzName.equals("MessageForMixedMsg")) {
                data.MessageType = 3;
                List mEleList = MField.GetField(msg, "msgElemList", List.class);
                String MsgSummary = "";
                for (Object MessageRecord : mEleList) {
                    if (MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForText") ||
                            MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForLongTextMsg")) {
                        String str = MField.GetField(MessageRecord, "msg", String.class);
                        if (!TextUtils.isEmpty(str)) MsgSummary = MsgSummary + str;


                    } else if (MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForPic")) {

                        String PicMd5 = MField.GetField(MessageRecord, "md5", String.class);

                        String PicPath = MField.GetField(MessageRecord, "bigMsgUrl", String.class);

                        if (TextUtils.isEmpty(PicPath)) {
                            PicPath = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + PicMd5 + "/0?term=2";
                        }
                        MsgSummary = MsgSummary + "[PicUrl=" + PicPath + "]";
                    }
                }
                data.MessageContent = MsgSummary;
            } else if (clzName.equals("MessageForStructing") || clzName.equals("MessageForArkApp")) {
                data.MessageType = 2;
                data.MessageContent = QQMessageUtils.getCardMsg(msg);
            } else if (clzName.equals("MessageForPtt")) {
                data.MessageType = 4;
                data.MessageContent = "[语音]MD5=" + MField.GetField(msg, "md5", String.class);
                data.FileUrl = "https://grouptalk.c2c.qq.com" +
                        MField.GetField(msg, "directUrl", String.class);
                data.LocalPath = MField.GetField(msg, "fullLocalPath", String.class);
            } else if (clzName.equals("MessageForTroopFile")) {
                data.MessageType = 5;
                data.MessageContent = "[文件]" + MField.GetField(msg, "fileName", String.class);
                data.FileUrl = MField.GetField(msg, "url", String.class);
                data.FileName = MField.GetField(msg, "fileName", String.class);
                data.FileSize = MField.GetField(msg, "fileSize", long.class);
                if (fileDLCache.containsKey(data.FileUrl)) {
                    data.FileUrl = fileDLCache.get(data.FileUrl);
                }
            } else if (clzName.equals("MessageForReplyText")) {
                Object SourceInfo = MField.GetField(msg, "mSourceMsgInfo");
                if (SourceInfo != null) {
                    data.MessageType = 6;
                    data.MessageContent = MField.GetField(msg, "msg", String.class);
                    long uin = MField.GetField(SourceInfo, "mSourceMsgSenderUin");
                    data.ReplyTo = String.valueOf(uin);
                }
            }
            if (data.MessageType != 0) {
                PluginController.checkAndInvoke(data.GroupUin, "onRevokeMsg", data);
            }
        } catch (Exception e) {
            LogUtils.error("MessageDecoder1", new RuntimeException("Can't decode msg:(" + msg.getClass().getName() + ")", e));
        }
    }

    //(未实装)请求加群
    public static void onRequestJoin(String GroupUin, String UserUin, String Invitor, String source, String ans, String raw_ans, Object callback) {
        PluginInfo.RequestInfo requestInfo = new PluginInfo.RequestInfo();
        requestInfo.RequestSource = source;
        requestInfo.RequestText = raw_ans;
        requestInfo.Answer = ans;
        requestInfo.GroupUin = GroupUin;
        requestInfo.UserUin = UserUin;
        requestInfo.source = callback;
        PluginController.checkAndInvoke(GroupUin, "onRequestJoin", requestInfo);
    }

    public static void onPbSendMessage(Object msgRecord){
        PluginInfo.EarlyInfo earlyInfo = decodeEarly(msgRecord);
        if (earlyInfo != null){
            PluginController.checkAndInvoke(earlyInfo.GroupUin, "onPbSendMessage", msgRecord);
        }
    }

}
