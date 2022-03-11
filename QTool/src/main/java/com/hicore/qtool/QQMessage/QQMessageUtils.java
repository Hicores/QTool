package com.hicore.qtool.QQMessage;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.Classes;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.XposedInit.HostInfo;

import java.lang.reflect.Method;

public class QQMessageUtils {
    public static Object GetMessageByTimeSeq(String uin,int istroop,long msgseq) {
        try{
            if(HookEnv.AppInterface==null)return null;
            Object MessageFacade = MMethod.CallMethod(HookEnv.AppInterface, "getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            return MMethod.CallMethod(MessageFacade,"c", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                    String.class,int.class,long.class
            },uin,istroop,msgseq);
        } catch (Exception e) {
            LogUtils.error("QQMessageUtils","GetMessageByTimeSeq error:\n"+e);
            return null;
        }
    }
    public static void revokeMsg(Object msg){
        try{
            Object MessageFacade = MMethod.CallMethod(HookEnv.AppInterface,"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            if(msg.getClass().toString().contains("MessageForTroopFile"))
            {
                RevokeTroopFile(msg);
            }

            Object MsgCache = MMethod.CallMethod(HookEnv.AppInterface,"getMsgCache",
                    MClass.loadClass("com.tencent.mobileqq.service.message.MessageCache"));

            MMethod.CallMethod(MsgCache,"b",void.class,new Class[]{boolean.class},true);
            MessageFacade_RevokeMessage().invoke(MessageFacade,msg);
        }catch (Exception e){
            LogUtils.error("revokeMsg",e);
        }

    }
    public static void AddMsg(Object MessageRecord)
    {
        try{
            Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    String.class
            });
            Object MessageFacade = MMethod.CallMethod(QQEnvUtils.getAppRuntime(),"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            InvokeMethod.invoke(MessageFacade,MessageRecord,QQEnvUtils.getCurrentUin());
        } catch (Throwable th) {
            LogUtils.error("AddMsg",th);
        }
    }
    public static void AddAndSendMsg(Object MessageRecord)
    {
        try {
            Object MessageFacade = MMethod.CallMethod(QQEnvUtils.getAppRuntime(),"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
            Method mMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")
            });
            mMethod.invoke(MessageFacade,MessageRecord,null);
        } catch (Exception e) {
            LogUtils.error("AddAndSendMsg", e);
        }

    }
    private static void RevokeTroopFile(Object MessageRecord)
    {
        try{
            Object RevokeHelper = QQEnvUtils.GetRevokeHelper();
            MMethod.CallMethod(RevokeHelper,"a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForTroopFile")
            },MessageRecord);
        }catch (Exception ex) {
           LogUtils.error("RevokeTroopFile",ex);
        }
    }
    private static Method MessageFacade_RevokeMessage(){
        Method m =
                HostInfo.getVerCode() < 5670 ?
                        MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade","d",void.class,new Class[]{Classes.MessageRecord()}):
                        MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade","f",void.class,new Class[]{Classes.MessageRecord()});
        return m;
    }

}
