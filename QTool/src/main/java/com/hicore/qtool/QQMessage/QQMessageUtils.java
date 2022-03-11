package com.hicore.qtool.QQMessage;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.qtool.HookEnv;

public class QQMessageUtils {
    public static Object GetMessageByTimeSeq(String uin,int istroop,long msgseq)
    {
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
}
