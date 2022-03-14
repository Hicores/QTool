package com.hicore.qtool.QQMessage;

import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.Classes;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.Utils.DataUtils;
import com.hicore.Utils.HttpUtils;
import com.hicore.Utils.NameUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.XposedInit.HostInfo;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class QQMsgBuilder {
    private static final String TAG = "QQMsgBuilder";
    public static Object build_struct(String xml){
        try{
            Method BuildStructMsg = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.structmsg.TestStructMsg"),"a",
                    MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),new Class[]{String.class});
            Object msgData = BuildStructMsg.invoke(null,new Object[]{xml});
            return msgData;
        } catch (Throwable th) {
            LogUtils.error(TAG, "build_struct:\n"+th);
            return null;
        }
    }
    public static Object build_arkapp(String json){
        try{
            Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage","fromAppXml",
                    boolean.class,new Class[]{String.class});
            Constructor<?> cons = MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage").getConstructor();
            Object _ArkAppMsg = cons.newInstance();
            med.invoke(_ArkAppMsg, json);
            return _ArkAppMsg;
        } catch (Throwable th) {
            LogUtils.error("TAG", "build_json:\n"+th);
            return null;
        }
    }
    public static Object buildPic(Object _Session,String PicPath){
        if (PicPath.toLowerCase(Locale.ROOT).startsWith("http")){
            String CachePath = HookEnv.ExtraDataPath+"/Cache/"+ NameUtils.GetRandomName();
            HttpUtils.DownloadToFile(PicPath,CachePath);
            return buildPic0(_Session,CachePath);
        }else {
            return buildPic0(_Session,PicPath);
        }
    }
    public static Object buildPic0(Object _Session,String PicPath){
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    String.class
            });
            Object PICMsg = CallMethod.invoke(null,
                    HookEnv.AppInterface,_Session,PicPath
            );
            MField.SetField(PICMsg,"md5", DataUtils.getFileMD5(new File(PicPath)));
            MField.SetField(PICMsg,"uuid", DataUtils.getFileMD5(new File(PicPath))+".jpg");
            MField.SetField(PICMsg,"localUUID", UUID.randomUUID().toString());
            MMethod.CallMethodNoParam(PICMsg,"prewrite",void.class);
            return PICMsg;
        }
        catch (Exception e)
        {
            LogUtils.error("buildPic0", Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object buildText(String GroupUin,String text){
        try{
            Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),new Class[]{
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
            Object TextMessageRecord = InvokeMethod.invoke(null,HookEnv.AppInterface,"",GroupUin, QQEnvUtils.getCurrentUin(),1,(byte)0,(byte)0,(short)0,text);
            return TextMessageRecord;
        }
        catch (Exception e)
        {
            LogUtils.error("buildText",Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object buildAtInfo(String Useruin,String AtText,short StartPos){
        try {
            Object AtInfoObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.AtTroopMemberInfo"));
            if(Useruin.isEmpty())return null;
            if(Useruin.equals("0")) {
                MField.SetField(AtInfoObj,"flag",(byte)1);
                MField.SetField(AtInfoObj,"startPos",StartPos);
                MField.SetField(AtInfoObj,"textLen",(short)AtText.length());
            } else {
                MField.SetField(AtInfoObj,"uin",Long.parseLong(Useruin));
                MField.SetField(AtInfoObj,"startPos",StartPos);
                MField.SetField(AtInfoObj,"textLen",(short)AtText.length());
            }
            return AtInfoObj;
        }catch (Exception e)
        {
            LogUtils.error("buildAtInfo",Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object buildMix(Object session, ArrayList msgElems){
        try{
            Method m =
                    HostInfo.getVerCode() < 5670 ?
                            MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),new Class[]{Classes.QQAppinterFace(),String.class,String.class,int.class}):
                            MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","g",MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),new Class[]{Classes.QQAppinterFace(),String.class,String.class,int.class});
            if (m == null) m = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","h",MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),new Class[]{Classes.QQAppinterFace(),String.class,String.class,int.class});
            Object MixMessageRecord;
            if (QQSessionUtils.getSessionID(session) ==10014){
                MixMessageRecord = m.invoke(null,HookEnv.AppInterface,QQSessionUtils.getChannelID(session),QQEnvUtils.getCurrentUin(),10014);
            }else {
                MixMessageRecord = m.invoke(null,HookEnv.AppInterface,QQSessionUtils.getGroupUin(session),QQEnvUtils.getCurrentUin(),1);
            }
            MField.SetField(MixMessageRecord,"msgElemList",msgElems);
            MixMessageRecord = MMethod.CallMethodNoParam(MixMessageRecord,"rebuildMixedMsg",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"));
            return MixMessageRecord;
        }catch (Exception e){
            LogUtils.error("buildMix",e);
            return null;
        }



    }
}
