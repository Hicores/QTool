package cc.hicore.qtool.QQMessage.MessageImpl;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XposedInit.HostInfo;

@XPItem(name = "MsgApi_SendFakeMultiMsg",itemType = XPItem.ITEM_Api)
public class MsgApi_SendFakeMultiMsg {
    private static final String fakeMsgXML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"35\" templateID=\"1\" action=\"viewMultiMsg\" brief=\"[聊天记录]\" tSum=\"1\" sourceMsgId=\"0\" url=\"\" flag=\"3\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"1\" advertiser_id=\"0\" aid=\"0\"><title size=\"34\" maxLines=\"2\" lineSpace=\"12\">聊天记录</title><title size=\"26\" color=\"#777777\" maxLines=\"2\" lineSpace=\"12\">新消息</title><hr hidden=\"false\" style=\"0\" /><summary size=\"26\" color=\"#777777\">查看1条转发消息</summary></item><source name=\"聊天记录\" icon=\"\" action=\"\" appid=\"-1\" /></msg>";
    private static final String replaceXML = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"35\" templateID=\"1\" action=\"viewMultiMsg\" brief=\"[聊天记录]\" m_resid=\"REPLACE\" m_fileName=\"587781278678697\" tSum=\"1\" sourceMsgId=\"0\" url=\"\" flag=\"3\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"1\" advertiser_id=\"0\" aid=\"0\"><title size=\"34\" maxLines=\"2\" lineSpace=\"12\">聊天记录</title><title size=\"26\" color=\"#777777\" maxLines=\"2\" lineSpace=\"12\">新消息</title><hr hidden=\"false\" style=\"0\" /><summary size=\"26\" color=\"#777777\">查看1条转发消息</summary></item><source name=\"聊天记录\" icon=\"\" action=\"\" appid=\"-1\" /></msg>";
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void onSend(String fakeGroup, String fakeUin, List messageRecords, Object session, String ShowTag, String fakeName) throws Exception {
        if (HostInfo.getVerCode() > 8000){
            Object multiRequest = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgRequest"));
            Object struct = QQMsgBuilder.build_struct(fakeMsgXML);
            Object structContainer = QQMsgBuilder.build_MessageForStruct(struct, QQSessionUtils.Build_SessionInfo(fakeGroup,fakeUin));
            Field f= MField.FindFirstField(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgRequest"),MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing"));
            f.set(multiRequest,structContainer);

            HashMap<String,String> uinContainer = new HashMap<>();
            if (!TextUtils.isEmpty(fakeName)){
                uinContainer.put(fakeUin, fakeName);
            }else {
                uinContainer.put(fakeUin, QQGroupUtils.Group_Get_Member_Name(fakeGroup,fakeUin));
            }

            MField.SetField(multiRequest,"c",uinContainer);
            List chatMessageContainer = new ArrayList();
            chatMessageContainer.addAll(messageRecords);

            MField.SetField(multiRequest,"b",chatMessageContainer);
            MField.SetField(multiRequest,"a",session);

            Object controller = MMethod.CallMethodNoParam(HookEnv.AppInterface,"getMultiMsgController",MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgController"));
            XPBridge.HookBeforeOnce(MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgController"),null,void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pic.UpCallBack$SendResult")}), param -> {
                Object result = param.args[0];
                param.setResult(null);
                int code = MField.GetField(result,"a",int.class);
                if (code == 0){
                    String resid = MField.GetField(result,"f",String.class);
                    String willSendResult = replaceXML.replace("REPLACE",resid);
                    if (!TextUtils.isEmpty(ShowTag)){
                        willSendResult = willSendResult.replace("新消息",ShowTag);
                    }
                    QQMsgSender.sendStruct(session,QQMsgBuilder.build_struct(willSendResult));
                }
            });
            MMethod.CallMethodSingle(controller,HostInfo.getVerCode() >8000 ? "r":"e",void.class,multiRequest);
        }else {
            Object multiRequest = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgRequest"));
            Object struct = QQMsgBuilder.build_struct(fakeMsgXML);
            Object structContainer = QQMsgBuilder.build_MessageForStruct(struct,QQSessionUtils.Build_SessionInfo(fakeGroup,fakeUin));
            MField.SetField(multiRequest,"d",struct);
            MField.SetField(multiRequest,"e",structContainer);

            HashMap<String,String> uinContainer = new HashMap<>();
            if (!TextUtils.isEmpty(fakeName)){
                uinContainer.put(fakeUin, fakeName);
            }else {
                uinContainer.put(fakeUin, QQGroupUtils.Group_Get_Member_Name(fakeGroup,fakeUin));
            }

            MField.SetField(multiRequest,"c",uinContainer);

            List chatMessageContainer = new ArrayList();
            chatMessageContainer.addAll(messageRecords);
            MField.SetField(multiRequest,"b",chatMessageContainer);
            MField.SetField(multiRequest,"a",session);

            Object controller = MMethod.CallMethodNoParam(HookEnv.AppInterface,"getMultiMsgController",MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgController"));
            XPBridge.HookBeforeOnce(MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgController"),"b",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pic.UpCallBack$SendResult")}),param -> {
                Object result = param.args[0];
                param.setResult(null);
                int code = MField.GetField(result,"a",int.class);
                if (code == 0){
                    String resid = MField.GetField(result,"f",String.class);
                    String willSendResult = replaceXML.replace("REPLACE",resid);
                    if (!TextUtils.isEmpty(ShowTag)){
                        willSendResult = willSendResult.replace("新消息",ShowTag);
                    }
                    QQMsgSender.sendStruct(session,QQMsgBuilder.build_struct(willSendResult));
                }
            });
            MMethod.CallMethodSingle(controller,"e",void.class,multiRequest);
        }
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void onSend_890(String fakeGroup, String fakeUin, List messageRecords, Object session, String ShowTag, String fakeName) throws Exception {
            Object multiRequest = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.multimsg.h"));
            Object struct = QQMsgBuilder.build_struct(fakeMsgXML);
            Object structContainer = QQMsgBuilder.build_MessageForStruct(struct, QQSessionUtils.Build_SessionInfo(fakeGroup,fakeUin));
            Field f= MField.FindFirstField(MClass.loadClass("com.tencent.mobileqq.multimsg.h"),MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing"));
            f.set(multiRequest,structContainer);

            HashMap<String,String> uinContainer = new HashMap<>();
            if (!TextUtils.isEmpty(fakeName)){
                uinContainer.put(fakeUin, fakeName);
            }else {
                uinContainer.put(fakeUin, QQGroupUtils.Group_Get_Member_Name(fakeGroup,fakeUin));
            }

            MField.SetField(multiRequest,"c",uinContainer);
            List chatMessageContainer = new ArrayList();
            chatMessageContainer.addAll(messageRecords);

            MField.SetField(multiRequest,"b",chatMessageContainer);
            MField.SetField(multiRequest,"a",session);

            Object controller = MMethod.CallMethodNoParam(HookEnv.AppInterface,"getMultiMsgController",MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgController"));
            XPBridge.HookBeforeOnce(MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgController"),null,void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pic.ae$a")}), param -> {
                Object result = param.args[0];
                param.setResult(null);
                int code = MField.GetField(result,"a",int.class);
                if (code == 0){
                    String resid = MField.GetField(result,"f",String.class);
                    String willSendResult = replaceXML.replace("REPLACE",resid);
                    if (!TextUtils.isEmpty(ShowTag)){
                        willSendResult = willSendResult.replace("新消息",ShowTag);
                    }
                    QQMsgSender.sendStruct(session,QQMsgBuilder.build_struct(willSendResult));
                }
            });
            MMethod.CallMethodSingle(controller,"r",void.class,multiRequest);
    }
}
