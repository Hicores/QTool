package com.hicore.qtool.QQMessage;

import java.util.ArrayList;

public class QQMsgSendUtils {
    public static void sendText(String GroupUin, String UserUin, String Text, ArrayList atList){

    }
    public static void decodeAndSendMsg(String GroupUin,String UserUin,String Message){

    }
    public static void sendCard(String GroupUin,String UserUin,String card){
        Object session = QQSessionUtils.Build_SessionInfo(GroupUin,UserUin);
        if (card.startsWith("{")){
            QQMsgSender.sendArkApp(session,QQMsgBuilder.build_arkapp(card));
        }else {
            QQMsgSender.sendArkApp(session,QQMsgBuilder.build_struct(card));
        }
    }
    public static void sendEffectShow(String GroupUin,String UserUin,String PicPath,int type){

    }
    public static void addTip(Object clipTo,String text){

    }
    public static void sendReply(String TroopUin,Object source,String mixText){

    }
}
