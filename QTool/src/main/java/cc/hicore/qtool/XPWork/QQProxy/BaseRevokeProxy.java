package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.XposedInit.HostInfo;

@XPItem(name = "BaseRevokeProxy",itemType = XPItem.ITEM_Hook)
public class BaseRevokeProxy{
    private static final String TAG = "BaseRevokeProxy";
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook1",MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"), null, void.class, new Class[]{
                ArrayList.class, boolean.class
        }));
        container.addMethod("hook2",MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.BaseMessageManager"), null, void.class, new Class[]{
                ArrayList.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook1")
    public BaseXPExecutor hook_1(){
        return param -> {
            ArrayList msgList = (ArrayList) param.args[0];
            if (msgList == null || msgList.isEmpty()) return;

            String GroupUin = (String) Table_RevokeInfo_Field.GroupUin().get(msgList.get(0));
            String OpUin = (String) Table_RevokeInfo_Field.OpUin().get(msgList.get(0));
            String sender = (String) Table_RevokeInfo_Field.Sender().get(msgList.get(0));
            int istroop = (int) Table_RevokeInfo_Field.IsTroop().get(msgList.get(0));
            long shmsgseq = (long) Table_RevokeInfo_Field.shmsgseq().get(msgList.get(0));
            String FriendUin;
            if (istroop == 1) {
                FriendUin = GroupUin;
            } else if (istroop == 0) {
                if (OpUin.equals(QQEnvUtils.getCurrentUin())) {
                    FriendUin = GroupUin;
                } else {
                    FriendUin = OpUin;
                }
            } else {
                if (OpUin.equals(QQEnvUtils.getCurrentUin())) {
                    FriendUin = GroupUin;
                } else {
                    FriendUin = OpUin;
                }

            }
            Object RevokeMsg = QQMessageUtils.GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);
            if (RevokeMsg != null) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onRevoke(RevokeMsg, OpUin));
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook2")
    public BaseXPExecutor hook_2(){
        return param -> {
            ArrayList msgList = (ArrayList) param.args[0];
            if (msgList == null || msgList.isEmpty()) return;

            String GroupUin = (String) Table_RevokeInfo_Field.GroupUin().get(msgList.get(0));
            String OpUin = (String) Table_RevokeInfo_Field.OpUin().get(msgList.get(0));
            String sender = (String) Table_RevokeInfo_Field.Sender().get(msgList.get(0));
            int istroop = (int) Table_RevokeInfo_Field.IsTroop().get(msgList.get(0));
            long shmsgseq = (long) Table_RevokeInfo_Field.shmsgseq().get(msgList.get(0));


            String FriendUin;
            if (istroop == 1 || istroop == 0) {
                FriendUin = GroupUin;
            } else {
                FriendUin = sender;
            }
            Object RevokeMsg = QQMessageUtils.GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);

            if (RevokeMsg != null) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onRevoke(RevokeMsg, OpUin));
            }
        };
    }
    public static class Table_RevokeInfo_Field{
        public static Class RevokeMsgInfo() {
            return MClass.loadClass("com.tencent.mobileqq.revokemsg.RevokeMsgInfo");
        }

        public static Field GroupUin() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "a", String.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "c", String.class) :
                            MField.FindField(RevokeMsgInfo(), "g", String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field OpUin() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "b", String.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "d", String.class):
                            MField.FindField(RevokeMsgInfo(),"h",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field Sender() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "d", String.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "h", String.class):
                            MField.FindField(RevokeMsgInfo(),"n",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field IsTroop() {
            Field f = HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "a", int.class):
                    MField.FindField(RevokeMsgInfo(),"e",int.class);;
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field shmsgseq() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "a", long.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "b", long.class):
                            MField.FindField(RevokeMsgInfo(),"f",long.class);;
            if (f != null) f.setAccessible(true);
            return f;
        }
    }

}
