package cc.hicore.qtool.ChatHook.PerventRevoke;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XPWork.QQProxy.BaseRevokeProxy;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import cc.hicore.qtool.XposedInit.MethodFinder;

@SuppressLint("ResourceType")
@XPItem(name = "消息防撤回",itemType = XPItem.ITEM_Hook)
public class PreventRevoke{
    @UIItem
    @VerController
    public UIInfo getUIInfo(){
        UIInfo ui = new UIInfo();
        ui.groupName = "聊天界面增强";
        ui.name = "消息防撤回";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getMethod_1(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade",null,void.class,new Class[]{
                ArrayList.class,boolean.class
        }));
        container.addMethod("hook_2",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        }));
        container.addMethod("hook_3",MMethod.FindMethod("com.tencent.imcore.message.BaseMessageManager",null,void.class,new Class[]{
                ArrayList.class
        }));
        container.addMethod("hook_4",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.AIORevokeMsgHelper","c",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor hook_1(){
        return param -> {
            ArrayList msgList = (ArrayList) param.args[0];
            if(msgList==null || msgList.isEmpty())return;


            String GroupUin = (String) BaseRevokeProxy.Table_RevokeInfo_Field.GroupUin().get(msgList.get(0));
            String OpUin = (String) BaseRevokeProxy.Table_RevokeInfo_Field.OpUin().get(msgList.get(0));
            String sender = (String) BaseRevokeProxy.Table_RevokeInfo_Field.Sender().get(msgList.get(0));
            int istroop = (int) BaseRevokeProxy.Table_RevokeInfo_Field.IsTroop().get(msgList.get(0));
            long shmsgseq = (long) BaseRevokeProxy.Table_RevokeInfo_Field.shmsgseq().get(msgList.get(0));
            String FriendUin;
            if(istroop == 1){
                FriendUin = GroupUin;
            }else if(istroop == 0){
                if(OpUin.equals(QQEnvUtils.getCurrentUin())){
                    FriendUin = GroupUin;
                }else{
                    FriendUin = OpUin;
                }
            } else{
                if(OpUin.equals(QQEnvUtils.getCurrentUin())){
                    FriendUin = GroupUin;
                }else{
                    FriendUin = OpUin;
                }

            }
            Object mRawmsg = QQMessageUtils.GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);
            if (mRawmsg !=null){
                long shmsg = MField.GetField(mRawmsg,"shmsgseq",long.class);
                long msgTime = MField.GetField(mRawmsg,"time",long.class);
                List<String> l = HookEnv.Config.getList("RevokeStore","RevokeMsgList",true);
                if (!l.contains(msgTime + ":" + shmsg)) l.add(msgTime + ":" + shmsg);
                if (l.size() > 1000)l.remove(0);
                HookEnv.Config.setList("RevokeStore","RevokeMsgList",l);
            }
            param.setResult(null);
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor hook_2(){
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
            List MessageRecoreList = MField.GetFirstField(param.thisObject,List.class);
            if(MessageRecoreList==null)return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
            String Extstr = MField.GetField(ChatMsg,"extStr",String.class);
            if (!TextUtils.isEmpty(Extstr)){
                ImageView tv = mLayout.findViewById(753951);
                List<String> l = HookEnv.Config.getList("RevokeStore","RevokeMsgList",true);
                long shmsg = MField.GetField(ChatMsg,"shmsgseq",long.class);
                long msgTime = MField.GetField(ChatMsg,"time",long.class);
                if (l.contains(msgTime + ":" + shmsg)){
                    if (tv == null) {
                        ResUtils.StartInject(mLayout.getContext());
                        //长按标签,位于Parent顶部中央,最大化
                        RelativeLayout.LayoutParams RLP = new RelativeLayout.LayoutParams(Utils.dip2px(mLayout.getContext(),80), Utils.dip2px(mLayout.getContext(),24));
                        RLP.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        RLP.topMargin = Utils.dip2px(mLayout.getContext(), 18);
                        tv = new ImageView(mLayout.getContext());
                        mLayout.addView(tv, RLP);
                        tv.setImageResource(R.drawable.revoke);
                        tv.setId(753951);
                        tv.setMaxHeight(Utils.dip2px(mLayout.getContext(),24));
                        tv.setMaxWidth(Utils.dip2px(mLayout.getContext(),80));
                        tv.setBackgroundColor(Color.TRANSPARENT);
                    }
                    if (tv.getVisibility() != View.VISIBLE)
                        tv.setVisibility(View.VISIBLE);
                    tv.setClickable(false);
                }else {
                    if (tv != null){
                        if (tv.getVisibility() != View.GONE)
                            tv.setVisibility(View.GONE);
                    }
                }
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_3")
    public BaseXPExecutor hook_3(){
        return param -> {
            ArrayList msgList = (ArrayList) param.args[0];
            if(msgList==null || msgList.isEmpty())return;
            String GroupUin = (String) BaseRevokeProxy.Table_RevokeInfo_Field.GroupUin().get(msgList.get(0));
            String OpUin = (String) BaseRevokeProxy.Table_RevokeInfo_Field.OpUin().get(msgList.get(0));
            String sender = (String) BaseRevokeProxy.Table_RevokeInfo_Field.Sender().get(msgList.get(0));
            int istroop = (int) BaseRevokeProxy.Table_RevokeInfo_Field.IsTroop().get(msgList.get(0));
            long shmsgseq = (long) BaseRevokeProxy.Table_RevokeInfo_Field.shmsgseq().get(msgList.get(0));


            String FriendUin;
            if(istroop == 1 || istroop == 0){
                FriendUin = GroupUin;
            }else{
                FriendUin = sender;
            }
            Object mRawmsg =QQMessageUtils. GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);
            if(OpUin.equals(QQEnvUtils.getCurrentUin()))
            {
                if(istroop==1 || (istroop==0 && !mRawmsg.getClass().getName().contains("MessageForTroopFile"))
                        || (istroop==1000 && !mRawmsg.getClass().getName().contains("MessageForTroopFile"))) {
                    param.setResult(null);
                }
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_4")
    public BaseXPExecutor hook_4(){
        return param -> {
            param.setResult(null);
            QQMessageUtils.revokeMsg(param.args[0]);
        };
    }
}
