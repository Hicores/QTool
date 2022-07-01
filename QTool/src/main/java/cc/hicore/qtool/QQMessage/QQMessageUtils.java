package cc.hicore.qtool.QQMessage;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.hicore.HookItemLoader.core.ApiHelper;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.MessageImpl.MsgApi_AddAndSendMsg;
import cc.hicore.qtool.QQMessage.MessageImpl.MsgApi_AddMsg;
import cc.hicore.qtool.QQMessage.MessageImpl.MsgApi_GetMessageByTimeSeq;
import cc.hicore.qtool.QQMessage.MessageImpl.MsgApi_SendFakeMultiMsg;
import cc.hicore.qtool.QQMessage.MessageImpl.MsgApi_revokeMsg;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.MethodFinder;

public class QQMessageUtils {
    public static Object GetMessageByTimeSeq(String uin, int istroop, long msgseq) {
        return ApiHelper.invoke(MsgApi_GetMessageByTimeSeq.class,uin,istroop,msgseq);
    }

    public static void revokeMsg(Object msg) {
        if (msg.getClass().toString().contains("MessageForTroopFile")) {
            RevokeTroopFile(msg);
        }
        ApiHelper.invoke(MsgApi_revokeMsg.class,msg);

    }

    public static void AddMsg(Object MessageRecord) {
       ApiHelper.invoke(MsgApi_AddMsg.class,MessageRecord);
    }

    public static void AddAndSendMsg(Object MessageRecord) {
        ApiHelper.invoke(MsgApi_AddAndSendMsg.class,MessageRecord);
    }

    private static void RevokeTroopFile(Object MessageRecord) {
        try {
            Object RevokeHelper = QQEnvUtils.GetRevokeHelper();
            MMethod.CallMethod(RevokeHelper, null, void.class, new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForTroopFile")
            }, MessageRecord);
        } catch (Exception ex) {
            LogUtils.error("RevokeTroopFile", ex);
        }
    }
    public static String getCardMsg(Object msg) {
        try {
            String clzName = msg.getClass().getSimpleName();
            if (clzName.equalsIgnoreCase("MessageForStructing")) {
                Object Structing = MField.GetField(msg, "structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                return MMethod.CallMethodNoParam(Structing, "getXml", String.class);
            }
            if (clzName.equalsIgnoreCase("MessageForArkApp")) {
                Object ArkAppMsg = MField.GetField(msg, "ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                return MMethod.CallMethodNoParam(ArkAppMsg, "toAppXml", String.class);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static int DecodeAntEmoCode(int EmoCode) {
        try {
            String s = FileUtils.ReadFileString(HookEnv.AppContext.getFilesDir() + "/qq_emoticon_res/face_config.json");
            JSONObject j = new JSONObject(s);
            JSONArray arr = j.getJSONArray("sysface");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.has("AniStickerType")) {
                    if (obj.optString("QSid").equals(EmoCode + "")) {
                        String sId = obj.getString("AQLid");
                        return (Integer.parseInt(sId));
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
    public static void sendFakeMultiMsg(String fakeGroup,String fakeUin,List messageRecords,Object session,String ShowTag,String fakeName){
        ApiHelper.invoke(MsgApi_SendFakeMultiMsg.class,fakeGroup,fakeUin,messageRecords,session,ShowTag,fakeName);
    }

}
