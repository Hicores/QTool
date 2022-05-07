package cc.hicore.qtool.ChatHook.PerventRevoke;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

@SuppressLint("ResourceType")
@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "消息防撤回",groupName = "聊天界面增强",type = 1,targetID = 1,id = "PreventRevoke")
public class PreventRevoke extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (!IsEnable)return;
            ArrayList msgList = (ArrayList) param.args[0];
            if(msgList==null || msgList.isEmpty())return;


            String GroupUin = MField.GetField(msgList.get(0),"c",String.class);
            String OpUin = MField.GetField(msgList.get(0),"d",String.class);
            String sender = MField.GetField(msgList.get(0),"h",String.class);
            int istroop = MField.GetField(msgList.get(0),"a",int.class);
            long shmsgseq = MField.GetField(msgList.get(0),"b",long.class);
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
        });
        XPBridge.HookAfter(m[1],param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
            List MessageRecoreList = MField.GetField(param.thisObject,param.thisObject.getClass() ,"a", List.class);
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
                    tv.setVisibility(View.VISIBLE);
                    tv.setClickable(false);
                }else {
                    if (tv != null){
                        tv.setVisibility(View.GONE);
                    }
                }
            }
        });
        XPBridge.HookBefore(m[2],param -> {
            if (!IsEnable)return;
            ArrayList msgList = (ArrayList) param.args[0];
            if(msgList==null || msgList.isEmpty())return;
            String GroupUin = MField.GetField(msgList.get(0),"c",String.class);
            String OpUin = MField.GetField(msgList.get(0),"d",String.class);
            String sender =  MField.GetField(msgList.get(0),"h",String.class);
            int istroop = MField.GetField(msgList.get(0),"a",int.class);
            long shmsgseq = MField.GetField(msgList.get(0),"b",long.class);


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
        });
        XPBridge.HookBefore(m[3],param -> {
            if (IsEnable){
                param.setResult(null);
                QQMessageUtils.revokeMsg(param.args[0]);
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        for (int i = 0; i < m.length; i++) {
            if (m[i] == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(PreventRevoke.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[4];
        m[0] = MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade","a",void.class,new Class[]{
                ArrayList.class,boolean.class
        });
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        m[2] = MMethod.FindMethod("com.tencent.imcore.message.BaseMessageManager","a",void.class,new Class[]{
                ArrayList.class
        });
        m[3] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.AIORevokeMsgHelper","c",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
        });


        return m;
    }
}
