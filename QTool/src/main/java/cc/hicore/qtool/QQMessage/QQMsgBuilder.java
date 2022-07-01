package cc.hicore.qtool.QQMessage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import cc.hicore.HookItemLoader.core.ApiHelper;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.NameUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.MessageBuilderImpl.Builder_Pic;
import cc.hicore.qtool.XposedInit.EnvHook;
import cc.hicore.qtool.XposedInit.HostInfo;

public class QQMsgBuilder {
    private static final String TAG = "QQMsgBuilder";
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_PIC = 2;
    private static final int TYPE_AT = 3;
    private static final int TYPE_MUTE = 4;
    private static final int TYPE_UNMUTE = 4;

    public static Object build_MessageForStruct(Object struct,Object session){
        try{
            return MMethod.CallStaticMethod(MClass.loadClass("com.tencent.mobileqq.utils.ShareMsgHelper"),"a",Classes.MessageRecord(),
                    HookEnv.AppInterface,"",1,struct);
        }catch (Exception e){
            LogUtils.error("build_messgeforStruct",e);
            return null;
        }
    }
    public static Object build_struct(String xml) {
        try {
            Method BuildStructMsg = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.structmsg.TestStructMsg"), "a",
                    MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), new Class[]{String.class});
            return BuildStructMsg.invoke(null, new Object[]{xml});
        } catch (Throwable th) {
            LogUtils.error(TAG, "build_struct:\n" + th);
            return null;
        }
    }
    public static Object build_arkapp(String json) {
        try {
            Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage", "fromAppXml",
                    boolean.class, new Class[]{String.class});
            Constructor<?> cons = MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage").getConstructor();
            Object _ArkAppMsg = cons.newInstance();
            med.invoke(_ArkAppMsg, json);
            return _ArkAppMsg;
        } catch (Throwable th) {
            LogUtils.error("TAG", "build_json:\n" + th);
            return null;
        }
    }
    public static Object buildPic(Object _Session, String PicPath) {
        if (PicPath.toLowerCase(Locale.ROOT).startsWith("http")) {
            String CachePath = HookEnv.ExtraDataPath + "/Cache/" + NameUtils.GetRandomName();
            HttpUtils.DownloadToFile(PicPath, CachePath);
            checkAndCastPic(CachePath);
            return buildPic0(_Session, CachePath);
        } else {
            return buildPic0(_Session, checkAndGetCastPic(PicPath));
        }
    }
    private static String checkAndGetCastPic(String Path) {
        File f = new File(Path);
        if (f.exists() && f.length() > 128) {
            try {
                byte[] buffer = new byte[4];
                FileInputStream ins = new FileInputStream(f);
                ins.read(buffer);
                ins.close();
                if (buffer[0] == 'R' && buffer[1] == 'I' && buffer[2] == 'F' && buffer[3] == 'F') {
                    Bitmap bitmap = BitmapFactory.decodeFile(Path);
                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bOut);
                    if (bOut.size() > 128) {
                        EnvHook.requireCachePath();
                        String CachePath = HookEnv.ExtraDataPath + "/Cache/Img_" + NameUtils.getRandomString(16);
                        FileUtils.WriteToFile(CachePath, bOut.toByteArray());
                        return CachePath;
                    }
                }
            } catch (Exception e) {

            }

        }
        return Path;
    }
    private static void checkAndCastPic(String Path) {
        File f = new File(Path);
        if (f.exists() && f.length() > 128) {
            try {
                byte[] buffer = new byte[4];
                FileInputStream ins = new FileInputStream(f);
                ins.read(buffer);
                ins.close();
                if (buffer[0] == 'R' && buffer[1] == 'I' && buffer[2] == 'F' && buffer[3] == 'F') {
                    Bitmap bitmap = BitmapFactory.decodeFile(Path);
                    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bOut);
                    if (bOut.size() > 128) {
                        FileUtils.WriteToFile(Path, bOut.toByteArray());
                    }
                }
            } catch (Exception e) {

            }
        }
    }
    public static Object buildPic0(Object _Session, String PicPath) {
        return ApiHelper.invoke(Builder_Pic.class,_Session,PicPath);
    }
    public static Object buildText(String GroupUin, String text) {
        try {
            Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), new Class[]{
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
            Object TextMessageRecord = InvokeMethod.invoke(null, HookEnv.AppInterface, "", GroupUin, QQEnvUtils.getCurrentUin(), 1, (byte) 0, (byte) 0, (short) 0, text);
            return TextMessageRecord;
        } catch (Exception e) {
            LogUtils.error("buildText", Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object buildAtInfo(String Useruin, String AtText, short StartPos) {
        try {
            Object AtInfoObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.AtTroopMemberInfo"));
            if (Useruin.isEmpty()) return null;
            if (Useruin.equals("0")) {
                MField.SetField(AtInfoObj, "flag", (byte) 1);
                MField.SetField(AtInfoObj, "startPos", StartPos);
                MField.SetField(AtInfoObj, "textLen", (short) AtText.length());
            } else {
                MField.SetField(AtInfoObj, "uin", Long.parseLong(Useruin));
                MField.SetField(AtInfoObj, "startPos", StartPos);
                MField.SetField(AtInfoObj, "textLen", (short) AtText.length());
            }
            return AtInfoObj;
        } catch (Exception e) {
            LogUtils.error("buildAtInfo", Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object buildMix(Object session, ArrayList msgElems) {
        try {
            Method m = null;
            Class<?> clz = MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory");
            for (Method ma : clz.getDeclaredMethods()){
                if (ma.getReturnType().equals(MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"))){
                    m = ma;
                    break;
                }
            }

            Object MixMessageRecord;
            if (QQSessionUtils.getSessionID(session) == 10014) {
                MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getChannelID(session), QQEnvUtils.getCurrentUin(), 10014);
            } else {
                MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getGroupUin(session), QQEnvUtils.getCurrentUin(), 1);
            }
            MField.SetField(MixMessageRecord, "msgElemList", msgElems);
            MixMessageRecord = MMethod.CallMethodNoParam(MixMessageRecord, "rebuildMixedMsg", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"));
            return MixMessageRecord;
        } catch (Exception e) {
            LogUtils.error("buildMix", e);
            return null;
        }
    }
    public static Object Copy_NewFlashChat(Object SourceChat) {
        try {
            Method ArkChatObj = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", null,
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForArkFlashChat"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            String.class, String.class, int.class,
                            MClass.loadClass("com.tencent.mobileqq.data.ArkFlashChatMessage")
                    }
            );
            Object sArk = MField.GetField(SourceChat, "ark_app_message");
            int isTroop = MField.GetField(SourceChat, "istroop", int.class);
            String FriendUin = MField.GetField(SourceChat, "frienduin", String.class);
            Object NewChat = ArkChatObj.invoke(null, HookEnv.AppInterface, FriendUin, QQEnvUtils.getCurrentUin(), isTroop, sArk);
            return NewChat;
        } catch (Exception e) {
            LogUtils.error("Copy_NewFlashChat", e);
            return null;
        }
    }
    public static Object CopyToTYMessage(Object SourceObj) throws Exception {
        Object mMessageRecord = build_common_message_record(-7001);
        MMethod.CallMethod(mMessageRecord, mMessageRecord.getClass().getSuperclass().getSuperclass(), "initInner", void.class,
                new Class[]{String.class, String.class, String.class, String.class, long.class, int.class, int.class, long.class},
                QQEnvUtils.getCurrentUin(), MField.GetField(SourceObj, "frienduin"), QQEnvUtils.getCurrentUin(), "[涂鸦]", System.currentTimeMillis() / 1000, -7001,
                MField.GetField(SourceObj, "istroop"), System.currentTimeMillis() / 1000
        );

        MField.SetField(mMessageRecord, "combineFileUrl", MField.GetField(SourceObj, SourceObj.getClass(), "combineFileUrl", String.class));
        MField.SetField(mMessageRecord, "combineFileMd5", MField.GetField(SourceObj, SourceObj.getClass(), "combineFileMd5", String.class));

        MField.SetField(mMessageRecord, "gifId", MField.GetField(SourceObj, SourceObj.getClass(), "gifId", int.class));

        MField.SetField(mMessageRecord, "offSet", MField.GetField(SourceObj, SourceObj.getClass(), "offSet", int.class));
        MField.SetField(mMessageRecord, "fileUploadStatus", MField.GetField(SourceObj, SourceObj.getClass(), "fileUploadStatus", int.class));
        MField.SetField(mMessageRecord, "fileDownloadStatus", MField.GetField(SourceObj, SourceObj.getClass(), "fileDownloadStatus", int.class));
        String mPath = MField.GetField(SourceObj, "localFildPath");
        if (!new File(mPath).exists()){
            mPath = HookEnv.AppContext.getExternalCacheDir()+ "/"+String.valueOf(Math.random()).substring(2);
            HttpUtils.DownloadToFile(MField.GetField(SourceObj,"combineFileUrl"),mPath);
        }
        MField.SetField(mMessageRecord, "localFildPath", mPath);
        MField.SetField(mMessageRecord, "extStr", MField.GetField(SourceObj, SourceObj.getClass(), "extStr", String.class));
        MField.SetField(mMessageRecord, "msg", "[涂鸦]");
        MMethod.CallMethodNoParam(mMessageRecord, "prewrite", void.class);
        MMethod.CallMethodNoParam(mMessageRecord, "parse", void.class);
        return rebuild_message(mMessageRecord);
    }
    public static Object CopyToMacketFaceMessage(Object SourceObj) throws Exception {
        Object mMessageRecord = build_common_message_record(-2007);
        MMethod.CallMethod(mMessageRecord, mMessageRecord.getClass().getSuperclass().getSuperclass(), "initInner", void.class,
                new Class[]{String.class, String.class, String.class, String.class, long.class, int.class, int.class, long.class},
                QQEnvUtils.getCurrentUin(), MField.GetField(SourceObj, "frienduin"), QQEnvUtils.getCurrentUin(), "[原创表情]", System.currentTimeMillis() / 1000, -2007,
                MField.GetField(SourceObj, "istroop"), System.currentTimeMillis() / 1000
        );
        MField.SetField(mMessageRecord, "msgData", MField.GetField(SourceObj, SourceObj.getClass(), "msgData", byte[].class));
        String strName = MField.GetField(SourceObj, "sendFaceName");
        if (strName != null) {
            MField.SetField(mMessageRecord, "sendFaceName", strName);
        }
        MMethod.CallMethodNoParam(mMessageRecord, "doParse", void.class);
        return rebuild_message(mMessageRecord);
    }
    public static Object Copy_PokeMsg(Object raw) {
        try {
            Object PokeEmo = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.MessageForPokeEmo"));
            MField.SetField(PokeEmo, "msgtype", -5018);
            MField.SetField(PokeEmo, "pokeemoId", 13);
            MField.SetField(PokeEmo, "pokeemoPressCount", MField.GetField(raw, "pokeemoPressCount"));
            MField.SetField(PokeEmo, "emoIndex", MField.GetField(raw, "emoIndex"));
            MField.SetField(PokeEmo, "summary", MField.GetField(raw, "summary"));
            MField.SetField(PokeEmo, "emoString", MField.GetField(raw, "emoString"));
            MField.SetField(PokeEmo, "emoCompat", MField.GetField(raw, "emoCompat"));
            MMethod.CallMethod(PokeEmo, "initMsg", void.class, new Class[0]);
            String friendInfo = MField.GetField(raw, "frienduin", String.class);
            int istroop = MField.GetField(raw, "istroop", int.class);
            MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),
                    null, void.class, new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface"), Classes.MessageRecord(), String.class, String.class, int.class},
                    HookEnv.AppInterface, PokeEmo, friendInfo, QQEnvUtils.getCurrentUin(), istroop);
            return PokeEmo;
        } catch (Exception e) {
            LogUtils.error("Copy_PokeMsg", e);
            return null;
        }

    }
    public static Object Build_RawMessageRecord_Troop(String GroupUin,int Type) throws Exception {
        Object mMessageRecord = build_common_message_record(Type);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"init",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                QQEnvUtils.getCurrentUin(),GroupUin,QQEnvUtils.getCurrentUin(),"",System.currentTimeMillis()/1000,Type,
                1,System.currentTimeMillis()/1000
        );
        return mMessageRecord;
    }
    public static Object Build_Fake_Mix(String fakeGroup,String fakeUin,String text) throws Exception {
        ArrayList<QQMsgSendUtils.DecodeResult> msgList = QQMsgSendUtils.decodeForResult(text);
        if (msgList.size() == 0)return null;

        if (msgList.size() == 1) {
            QQMsgSendUtils.DecodeResult result = msgList.get(0);
            if (result.msgType == TYPE_PIC) {
                Object record = QQMsgBuilder.buildPic(HookEnv.SessionInfo, result.content);
                MField.SetField(record,"frienduin",fakeGroup);
                MField.SetField(record,"senderuin",fakeUin);
                return record;
            } else if (result.msgType == TYPE_TEXT) {
                Object record =QQMsgBuilder.buildText(fakeUin, result.content);
                MField.SetField(record,"frienduin",fakeGroup);
                MField.SetField(record,"senderuin",fakeUin);
                return record;
            }
        }
        boolean HasPic = false;
        ArrayList records = new ArrayList();
        ArrayList atInfo = new ArrayList();
        int length = 0;
        StringBuilder summary = new StringBuilder();
        for (QQMsgSendUtils.DecodeResult result : msgList) {
            if (result.msgType == TYPE_TEXT) {
                length += result.content.length();
                records.add(QQMsgBuilder.buildText(fakeGroup, result.content));
                summary.append(result.content);
            } else if (result.msgType == TYPE_PIC) {
                records.add(QQMsgBuilder.buildPic(HookEnv.SessionInfo, result.content));
                HasPic = true;
            } else if (result.msgType == TYPE_AT) {
                String atText;
                if (result.content.equals("0")) {
                    atText = "@全体成员 ";
                } else {
                    atText = "@" + QQGroupUtils.Group_Get_Member_Name(fakeGroup, result.content) + " ";
                }
                summary.append(atText);
                atInfo.add(QQMsgBuilder.buildAtInfo(result.content, atText, (short) length));
                length += atText.length();
                records.add(QQMsgBuilder.buildText(fakeGroup, atText));
            }
        }

        if (HasPic) {
            Object mixRecord = QQMsgBuilder.buildMix(HookEnv.SessionInfo,records);
            MField.SetField(mixRecord,"frienduin",fakeGroup);
            MField.SetField(mixRecord,"senderuin",fakeUin);
            return mixRecord;
        } else {
            Object record =QQMsgBuilder.buildText(fakeGroup, summary.toString());
            MField.SetField(record,"frienduin",fakeGroup);
            MField.SetField(record,"senderuin",fakeUin);
            return record;
        }
    }

    public static Object build_common_message_record(int type){
        try{
            if (HostInfo.getVerCode() > 8000){
                Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","d", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                        int.class
                });
                return CallMethod.invoke(null,type);
            }else {
                Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                        int.class
                });
                return CallMethod.invoke(null,type);
            }
        }catch (Exception e){
            LogUtils.error("build_common_message_record",e);
            return null;
        }
    }
    public static Object rebuild_message(Object record){
        try{
            return MMethod.CallStaticMethod(MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),null,MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),record);
        }catch (Exception e){
            LogUtils.error("build_common_message_record",e);
            return null;
        }
    }
}
