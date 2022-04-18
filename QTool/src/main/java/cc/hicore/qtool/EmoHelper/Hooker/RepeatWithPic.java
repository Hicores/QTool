package cc.hicore.qtool.EmoHelper.Hooker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.StringUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.QQMessage.QQMsgSendUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XPWork.QQProxy.BaseChatPie;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@HookItem(isDelayInit = false, isRunInAllProc = false)
@UIItem(itemName = "带图回复", itemType = 1, ID = "RepeatWithPic", mainItemID = 1)
public class RepeatWithPic extends BaseHookItem implements BaseUiItem {
    private static final HashMap<String, String> picCookies = new HashMap<>();
    public static volatile boolean IsEnable;

    public static void AddToPreSendList(String LocalPath) {
        String cookie = Integer.toString(LocalPath.hashCode());
        picCookies.put(cookie, LocalPath);
        AddToEditText(cookie);
    }
    public static boolean IsAvailable(){
        return IsEnable && IsNowReplying() &&(QQSessionUtils.getSessionID() == 1 || QQSessionUtils.getSessionID() == 0);
    }
    private static void AddToEditText(String Cookie) {
        if (ed != null) {
            String Text = "[PicCookie=" + Cookie + "]";
            int pos = ed.getSelectionStart();
            Editable e = ed.getText();
            e.insert(pos, Text);
            ed.setText(e);
            ed.setSelection(pos + Text.length());
        }
    }

    static EditText ed = null;
    static Object chatPie = null;

    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[3], param -> {
            ed = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.widget.XEditTextEx"));
            chatPie = param.thisObject;
        });
        XPBridge.HookBefore(m[1], param -> {
            if (IsAvailable()) {
                String Path = (String) param.args[3];
                AddToPreSendList(Path);
                param.setResult(true);
            }
        });
        XPBridge.HookBefore(m[2], param -> {
            if (IsAvailable()) {
                List<String> l = (List) param.args[1];
                for (String str : l) {
                    if (str.toLowerCase(Locale.ROOT).endsWith(".mp4")) continue;
                    AddToPreSendList(str);
                }
                param.setResult(true);
            }
        });

        XPBridge.HookBefore(m[0], param -> {
            if (IsEnable) {
                Object AddMsg = param.args[0];
                int istroop = MField.GetField(AddMsg,"istroop",int.class);
                if (!picCookies.isEmpty() && AddMsg.getClass().getName().contains("MessageForReplyText") && (istroop==1 || istroop==0)){
                    String Text = MField.GetField(AddMsg,"msg",String.class);
                    if(TextUtils.isEmpty(Text))
                    {
                        Text = MField.GetField(AddMsg,"sb",CharSequence.class)+"";
                    }
                    if (Text.contains("PicCookie")){
                        String GroupUin = QQSessionUtils.getGroupUin();
                        String UserUin = QQSessionUtils.getFriendUin();
                        MField.SetField(AddMsg,"msg"," ");
                        MField.SetField(AddMsg,"sb"," ");
                        MMethod.CallMethod(AddMsg,"prewrite",void.class,new Class[0]);

                        String[] Cookies = StringUtils.GetStringMiddleMix(Text,"[PicCookie=","]");
                        for (String ss : Cookies){
                            if (picCookies.containsKey(ss)){
                                Text = Text.replace("[PicCookie="+ss+"]","[PicUrl="+picCookies.get(ss)+"]");
                            }
                        }
                        picCookies.clear();
                        QQMsgSendUtils.decodeAndSendMsg(GroupUin,UserUin,Text,QQMessageUtils.CopyReplyMessage(AddMsg));
                        param.setResult(null);
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
    StringBuilder builder;

    @Override
    public String getErrorInfo() {
        return builder.toString();
    }

    @Override
    public boolean check() {
        builder = new StringBuilder();
        Method[] m = getMethod();
        for (int i=0;i<m.length;i++){
            if (m[i] == null){
                builder.append("index:").append(i).append(";");
            }
        }
        return builder.length() == 0;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(RepeatWithPic.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }

    public Method[] getMethod() {
        Method[] m = new Method[4];
        m[0] = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a", void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")});

        m[1] = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.sender.CustomEmotionSenderUtil", "sendCustomEmotion", void.class, new Class[]{
                MClass.loadClass("com.tencent.common.app.business.BaseQQAppInterface"),
                Context.class,
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                String.class,
                boolean.class,
                boolean.class,
                boolean.class,
                String.class,
                MClass.loadClass("com.tencent.mobileqq.emoticon.StickerInfo"),
                String.class,
                Bundle.class
        });

        m[2] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel", "a", boolean.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),
                List.class, boolean.class});

        m[3] = BaseChatPie.getMethod();


        return m;
    }

    public static boolean IsNowReplying() {
        try {
            Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.HelperProvider"));
            Method IsNowReplyingMethod = null;
            for (Method sm : HelperProvider.getClass().getSuperclass().getSuperclass().getDeclaredMethods()) {
                if (sm.getName().equals("a") && sm.getParameterCount() == 1 && sm.getParameterTypes()[0] == int.class) {
                    if (sm.getReturnType() != Dialog.class && sm.getReturnType() != void.class && sm.getReturnType() != boolean.class) {
                        IsNowReplyingMethod = sm;
                        break;
                    }
                }
            }
            Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
            Object SourceInfo = MMethod.CallMethod(ReplyHelper, "a", MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
            return SourceInfo != null;
        } catch (Exception e) {
            LogUtils.error("IsNowReplying", e);
            return false;
        }

    }
}
