package com.hicore.qtool.QQMessage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.Classes;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.XposedInit.HostInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class QQMsgSender {
    public static void sendText(Object _Session, String text, ArrayList atList){
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    Context.class,
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    String.class,
                    ArrayList.class
            });
            CallMethod.invoke(null, HookEnv.AppInterface, HookEnv.AppContext,_Session,text,atList);
        } catch (Exception e) {
            LogUtils.error("sendText", Log.getStackTraceString(e));
        }
    }
    public static void sendPic(Object _Session,Object picRecord){
        try {
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForPic"),
                    int.class
            });
            hookMethod.invoke(null,
                    HookEnv.AppInterface,_Session,picRecord,0
            );
        } catch (Exception e) {
            LogUtils.error("sendPic",e);
        }
    }
    public static void sendStruct(Object _Session,Object structMsg){
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",
                    void.class,new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                            MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg")
                    });
            CallMethod.invoke(null, HookEnv.AppInterface,_Session,structMsg);
        }
        catch (Throwable th)
        {
            LogUtils.error("sendStruct", Log.getStackTraceString(th));
        }
    }
    public static void sendArkApp(Object _Session,Object arkAppMsg){
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",
                    boolean.class,new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                            MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage")
                    });
            CallMethod.invoke(null, HookEnv.AppInterface,_Session,arkAppMsg);
        }
        catch (Throwable th)
        {
            LogUtils.error("sendArkApp", Log.getStackTraceString(th));
        }
    }
    public static void sendVoice(Object _Session,String path){
        try{
            Method CallMethod =
                    HostInfo.getVerCode() < 5670 ?
                            MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a", long.class,new Class[]{Classes.QQAppinterFace(),Classes.SessionInfo(),String.class}):
                            MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","d", long.class,new Class[]{Classes.QQAppinterFace(),Classes.SessionInfo(),String.class});
            CallMethod.invoke(null,HookEnv.AppInterface, _Session,path);
        }catch (Exception e)
        {
            LogUtils.error("sendVoice",e);
        }
    }
    public static void sendMix(Object _Session,Object mixRecord){
        try{
            Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    int.class
            });
            Object Call = MMethod.CallStaticMethodNoParam(MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),"a",MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"));
            mMethod.invoke(Call,HookEnv.AppInterface,mixRecord,_Session,0);
        }catch (Exception e){
            LogUtils.error("sendMix",e);
        }

    }
    public static void sendReply(Object _Session,Object replyRecord){
        try{
            Object Call = MMethod.CallStaticMethodNoParam(MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),"a",MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"));
            Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                    int.class,
                    int.class,
                    boolean.class
            });
            mMethod.invoke(Call, QQEnvUtils.getAppRuntime(),replyRecord ,_Session,2,0,false);
        }catch (Exception e){
            LogUtils.error("sendReply",e);
        }
    }

    public static void sendTuya(Object _Session,Object strikeMsg){
        QQMessageUtils.AddAndSendMsg(strikeMsg);
    }
    public static void sendPaiyipai(String GroupUin,String UserUin){
        try{
            Method m = MMethod.FindMethod("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler","a",void.class,new Class[]{String.class,String.class,int.class});
            Object Handler = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler"),HookEnv.AppInterface);
            if(TextUtils.isEmpty(GroupUin))
            {
                m.invoke(Handler,UserUin,UserUin,0);
            }else
            {
                m.invoke(Handler,UserUin,GroupUin,1);
            }
        }catch (Throwable th)
        {
            LogUtils.error("sendPaiyipai",th);
        }
    }
    public static void sendVideo(Object _Session,Object videoRecord){
        try{
            MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.activity.ChatActivityFacade"),"a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForShortVideo")
            },HookEnv.AppInterface,_Session,videoRecord);
        }catch (Exception e){
            LogUtils.error("sendVideo",e);
        }
    }
    public static void sendFile(Object _Session,Object fileRecord){
        try{
            Object Instance = MMethod.CallStaticMethodNoParam(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgManager"),"a",MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgManager"));
            MMethod.CallMethodParams(Instance,"a",void.class,fileRecord,HookEnv.AppInterface,0,_Session);
        }catch (Exception e){
            LogUtils.error("sendFile",e);
        }
    }
    public static void sendShakeWindow(String GroupUin){
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                    int.class
            });
            Object MessageRecord =CallMethod.invoke(null,-2020);
            MField.SetField(MessageRecord,"msg","窗口抖动");
            Object mShakeParam = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.ShakeWindowMsg"));
            MField.SetField(mShakeParam,"mReserve",0);
            MField.SetField(mShakeParam,"mType",0);
            MField.SetField(MessageRecord,"mShakeWindowMsg",mShakeParam);
            MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"initInner",void.class,
                    new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                    QQEnvUtils.getCurrentUin(),GroupUin,QQEnvUtils.getCurrentUin(),"[窗口抖动]",System.currentTimeMillis()/1000,-2020,
                    1,System.currentTimeMillis()/1000
            );
            MMethod.CallMethodNoParam(MessageRecord,"prewrite",void.class);
            QQMessageUtils.AddAndSendMsg(MessageRecord);
        }catch (Exception e){
            LogUtils.error("sendShakeWindow",e);
        }
    }
    public static void sendAntEmo(Object _Session,int ID){
        try{

            if(HostInfo.getVerCode() < 5865)
            {
                Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack","sendAniSticker",
                        boolean.class,new Class[]{int.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo")}
                );
                m.invoke(null,ID,_Session);
            }else
            {
                Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack","sendAniSticker",
                        boolean.class,new Class[]{int.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),int.class}
                );
                m.invoke(null,ID,_Session,0);
            }

        }catch (Exception es)
        {
            LogUtils.error("sendAntEmo",es);
        }
    }
}
