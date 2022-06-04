package cc.hicore.qtool.QQGroupHelper.AvatarMenu;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.lxj.xpopup.XPopup;

import java.util.ArrayList;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.UIViews.MyTimePicker;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupManager;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.QQTools.ContUtil;

public class AvatarMenuBuilder {

    public static void showAvatarMenu(Context context,Object chatMsg){
        try{
            ResUtils.StartInject(context);
            int istroop = MField.GetField(chatMsg,"istroop",int.class);
            String GroupUin = MField.GetField(chatMsg,"frienduin",String.class);
            String UserUin = MField.GetField(chatMsg,"senderuin",String.class);
            ArrayList<String> menuItems = new ArrayList<>();
            menuItems.add("复制QQ号");
            menuItems.add("艾特此人");
            menuItems.add("复制名片");
            if (!TextUtils.isEmpty(QQGroupUtils.Group_Get_Member_Title(GroupUin,UserUin))){
                menuItems.add("复制头衔");
            }
            if (QQGroupUtils.IsCreator(GroupUin,QQEnvUtils.getCurrentUin())){
                menuItems.add("撤回此消息");
                if (!UserUin.equals(QQEnvUtils.getCurrentUin())){
                    menuItems.add("踢出此人");
                    menuItems.add("禁言此人");
                }
                menuItems.add("设置名片");
                menuItems.add("设置头衔");
            }else if (QQGroupUtils.IsAdmin(GroupUin,QQEnvUtils.getCurrentUin())){
                if (!QQGroupUtils.IsAdmin(GroupUin,UserUin) && !QQGroupUtils.IsCreator(GroupUin,UserUin)){
                    menuItems.add("撤回此消息");
                    menuItems.add("踢出此人");
                    menuItems.add("禁言此人");
                }else if (UserUin.equals(QQEnvUtils.getCurrentUin())){
                    menuItems.add("撤回此消息");
                }
                menuItems.add("设置名片");
            }else if (UserUin.equals(QQEnvUtils.getCurrentUin())){
                menuItems.add("撤回此消息");
            }



            new XPopup.Builder(ContUtil.getFixContext(context))
                    .asBottomList(QQGroupUtils.Group_Get_Member_Name(GroupUin,UserUin)+"("+UserUin+")", menuItems.toArray(new String[0]), (position, text) -> {
                        InvokeForName(text,chatMsg,ContUtil.getFixContext(context));
                    }).show();
        }catch (Throwable e){
            LogUtils.error("showAvatarMenu",e);
            Utils.ShowToast("无法创建便捷菜单:"+e);
        }
    }
    private static void InvokeForName(String Name,Object chatMsg,Context context){
        try{
            String GroupUin = MField.GetField(chatMsg,"frienduin",String.class);
            String UserUin = MField.GetField(chatMsg,"senderuin",String.class);
            switch (Name){
                case "复制QQ号":{
                    Utils.SetTextClipboard(UserUin);
                    Utils.ShowToast("已复制");
                    return;
                }
                case "艾特此人":{
                    Add_At_Text(GroupUin,UserUin);
                    return;
                }
                case "复制名片":{
                    Utils.SetTextClipboard(QQGroupUtils.Group_Get_Member_Name(GroupUin,UserUin));
                    Utils.ShowToast("已复制");
                    return;
                }
                case "复制头衔":{
                    Utils.SetTextClipboard(QQGroupUtils.Group_Get_Member_Title(GroupUin,UserUin));
                    Utils.ShowToast("已复制");
                    return;
                }
                case "撤回此消息":{
                    QQMessageUtils.revokeMsg(chatMsg);
                    return;
                }
                case "禁言此人":{
                    AlertDialog mAlert = new AlertDialog.Builder(context, 3).create();
                    mAlert.setTitle("禁言时间");
                    LinearLayout mLL = new LinearLayout(context);
                    mAlert.setView(mLL);
                    mLL.setOrientation(LinearLayout.VERTICAL);

                    MyTimePicker mPicker = new MyTimePicker(context);
                    mLL.addView(mPicker);

                    mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (dialog, which) -> {
                        try {
                            QQGroupManager.Group_Mute(GroupUin,UserUin,mPicker.GetSecond());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    mAlert.show();
                    return;
                }
                case "踢出此人":{
                    new XPopup.Builder(context)
                            .asConfirm("是否踢出?", "你确定要踢出此人吗?",
                                    "踢出", "踢出并拉黑",
                                    () -> QQGroupManager.Group_Kick(GroupUin,UserUin,true),
                                    () -> QQGroupManager.Group_Kick(GroupUin,UserUin,false),false).show();
                    return;
                }
                case "设置头衔":{
                    new XPopup.Builder(context)
                            .asInputConfirm("设置头衔", "请输入需要设置的头衔", QQGroupUtils.Group_Get_Member_Title(GroupUin, UserUin),
                                    "", text -> QQGroupManager.Group_Change_Title(GroupUin,UserUin,text)).show();
                    return;
                }
                case "设置名片":{
                    new XPopup.Builder(context)
                            .asInputConfirm("设置名片", "请输入需要设置的名片", QQGroupUtils.Group_Get_Member_Name(GroupUin, UserUin),
                                    "", text -> QQGroupManager.Group_Change_Name(GroupUin,UserUin,text)).show();
                    return;
                }
            }
        }catch (Throwable e){
            LogUtils.error("InvokeForName->"+Name,e);
            Utils.ShowToast("发生错误:"+e);
        }
    }
    public static void Add_At_Text(String GroupUin,String UserUin){
        //只有在群聊的时候才会添加艾特信息,私聊时不进行添加
        if(AvatarMenuHooker.chatPie.getClass().getName().equals("com.tencent.mobileqq.activity.aio.core.TroopChatPie")){
            try {
                MMethod.CallMethod(AvatarMenuHooker.chatPie,null,void.class,new Class[]{String.class,String.class,boolean.class,int.class},
                        UserUin, QQGroupUtils.Group_Get_Member_Name(GroupUin,UserUin),false,1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
