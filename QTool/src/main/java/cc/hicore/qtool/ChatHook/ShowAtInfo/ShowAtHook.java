package cc.hicore.qtool.ChatHook.ShowAtInfo;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "在消息下方显示艾特对象",targetID = 1,groupName = "聊天界面增强",id = "ShowAtInfo",type = 1)
public class ShowAtHook extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            if (IsEnable){
                Object mGetView = param.getResult();
                RelativeLayout mLayout;
                if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
                List MessageRecoreList = MField.GetFirstField(param.thisObject,List.class);
                if(MessageRecoreList==null)return;
                Object ChatMsg = MessageRecoreList.get((int) param.args[0]);

                StringBuilder builder = new StringBuilder();
                String ClzName = ChatMsg.getClass().getSimpleName();
                if (ClzName.equals("MessageForText") || ClzName.equals("MessageForLongTextMsg")){
                    String Extstr = MField.GetField(ChatMsg,"extStr",String.class);
                    JSONObject atJson = new JSONObject(Extstr);
                    String mStr = atJson.optString("troop_at_info_list");
                    ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                    String GroupUin = MField.GetField(ChatMsg,ChatMsg.getClass(),"frienduin",String.class);
                    if (AtList3 != null){
                        for (Object obj : AtList3){
                            long mLongData = MField.GetField(obj,"uin",long.class);
                            if (mLongData == 0){
                                builder.append("AtQQ:全体成员\n");
                                continue;
                            }
                            builder.append("AtQQ:").append(QQGroupUtils.Group_Get_Member_Name(GroupUin,String.valueOf(mLongData))).append("(").append(mLongData).append(")\n");
                        }
                    }
                }
                if (ClzName.equals("MessageForReplyText")){
                    HashSet<String> showText = new LinkedHashSet<>();
                    Object replyTo = MField.GetField(ChatMsg,"mSourceMsgInfo");
                    long replyToUin = MField.GetField(replyTo,"mSourceMsgSenderUin");
                    long replyToU = MField.GetField(replyTo,"mSourceMsgToUin");
                    showText.add("ReplyQQ:"+QQGroupUtils.Group_Get_Member_Name(""+replyToU,""+replyToUin)+"("+replyToUin+")");

                    String Extstr = MField.GetField(ChatMsg,"extStr",String.class);
                    JSONObject atJson = new JSONObject(Extstr);
                    String mStr = atJson.optString("troop_at_info_list");
                    ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                    String GroupUin = MField.GetField(ChatMsg,ChatMsg.getClass(),"frienduin",String.class);
                    if (AtList3 != null){
                        for (Object obj : AtList3){
                            long mLongData = MField.GetField(obj,"uin",long.class);
                            if (mLongData == 0){
                                builder.append("AtQQ:全体成员\n");
                                continue;
                            }
                            showText.add("AtQQ:"+QQGroupUtils.Group_Get_Member_Name(GroupUin,String.valueOf(mLongData))+"("+mLongData+")");
                        }
                    }
                    for (String text : showText){
                        builder.append(text).append("\n");
                    }
                }

                if (ClzName.equals("MessageForMixedMsg")){
                    ArrayList items = MField.GetField(ChatMsg,"msgElemList");

                    for (Object objs : items){
                        String Extstr = MField.GetField(objs,"extStr",String.class);
                        if (TextUtils.isEmpty(Extstr))continue;
                        JSONObject atJson = new JSONObject(Extstr);
                        String mStr = atJson.optString("troop_at_info_list");
                        if (TextUtils.isEmpty(mStr))continue;
                        ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                        String GroupUin = MField.GetField(ChatMsg,ChatMsg.getClass(),"frienduin",String.class);
                        if (AtList3 != null){
                            for (Object obj : AtList3){
                                long mLongData = MField.GetField(obj,"uin",long.class);
                                if (mLongData == 0){
                                    builder.append("AtQQ:全体成员\n");
                                    continue;
                                }
                                builder.append("AtQQ:").append(QQGroupUtils.Group_Get_Member_Name(GroupUin,String.valueOf(mLongData))).append("(").append(mLongData).append(")\n");
                            }
                        }
                    }
                }

                if (builder.length() != 0){
                    builder.setLength(builder.length()-1);
                    MMethod.CallMethod(mLayout,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,builder.toString(),null);
                }else {
                    TextView tailView = mLayout.findViewById(QQEnvUtils.getTargetID("chat_item_tail_message"));
                    if (tailView != null){
                        String text = tailView.getText().toString();
                        if (text.startsWith("AtQQ") || text.startsWith("ReplyQQ:")){
                            MMethod.CallMethod(mLayout,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},false,"",null);
                        }
                    }
                }
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
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(ShowAtHook.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
    }
}
