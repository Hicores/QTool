package cc.hicore.qtool.EmoHelper.Hooker;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.StringUtils;
import cc.hicore.qtool.JavaPlugin.Controller.PluginController;
import cc.hicore.qtool.QQMessage.QQMsgSendUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "带图回复", itemType = XPItem.ITEM_Hook)
public class RepeatWithPic {
    private static final HashMap<String, String> picCookies = new HashMap<>();
    public static volatile boolean IsEnable;
    static EditText ed = null;
    static Object chatPie = null;
    static CoreLoader.XPItemInfo item;

    public static void AddToPreSendList(String LocalPath) {
        String cookie = Integer.toString(LocalPath.hashCode());
        picCookies.put(cookie, LocalPath);
        AddToEditText(cookie);
    }

    public static boolean IsAvailable() {
        return IsNowReplying() && (QQSessionUtils.getSessionID() == 1 || QQSessionUtils.getSessionID() == 0);
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
    private static Class<?> getHelperProviderClz(){
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.AIOJubaoDialogHelper");
        Constructor<?>[] cons = clz.getConstructors();
        for (Constructor<?> con : cons){
            if (con.getParameterCount() == 2){
                return con.getParameterTypes()[0];
            }
        }
        return null;
    }
    private static Class<?> getReplyProviderItemClz(){
        Class<?> sFather = item.scanResult.get("fatherClass").getDeclaringClass();
        Class<?>[] sInterface = sFather.getInterfaces();
        for (Class<?> inter : sInterface){
            if (inter.getSimpleName().length() < 5){
                if (inter.getMethods().length > 0){
                    return inter;
                }
            }
        }
        return null;
    }

    public static boolean IsNowReplying() {
        try {
            if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_33){
                Object HelperProvider = MField.GetFirstField(chatPie, getHelperProviderClz());
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, getReplyProviderItemClz(), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_28){
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.ce"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.cj"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_18){
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bx"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.cc"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_13) {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bw"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.cb"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_10) {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bv"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.ca"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_8) {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bv"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.ca"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_5) {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bs"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bx"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_3) {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bp"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bu"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            } else if (HostInfo.getVerCode() >= QQVersion.QQ_8_9_0) {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bk"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.bp"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);
                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;

            } else {
                Object HelperProvider = MField.GetFirstField(chatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.HelperProvider"));
                Method IsNowReplyingMethod = MMethod.FindMethod(HelperProvider.getClass(), null, MClass.loadClass("com.tencent.mobileqq.activity.aio.helper.IHelper"), new Class[]{int.class});
                Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider, 119);

                Object SourceInfo = MMethod.CallMethod(ReplyHelper, null, MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"), new Class[0]);
                return SourceInfo != null;
            }

        } catch (Exception e) {
            LogUtils.error("IsNowReplying", e);
            return false;
        }

    }

    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "带图回复";
        ui.groupName = "聊天辅助";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }
    @VerController(targetVer = QQVersion.QQ_8_9_33)
    @MethodScanner
    public void getReplyMScannerMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("fatherClass"," showSearchEmotionPanel set afRoot FitsSystemWindows: false",m->true));
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook_1", MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a", void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")}));
        container.addMethod("hook_2", MMethod.FindMethod("com.tencent.mobileqq.emoticonview.sender.CustomEmotionSenderUtil", "sendCustomEmotion", void.class, new Class[]{
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
        }));
        container.addMethod("hook_3", MMethod.FindMethod("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel", null, boolean.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),
                List.class, boolean.class}));
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        container.addMethod("hook_1", MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a", void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")}));


        container.addMethod("hook_2", MMethod.FindMethodByName(MClass.loadClass("com.tencent.mobileqq.emoticonview.sender.CustomEmotionSenderUtil"), "sendCustomEmotion", 11));
        container.addMethod("hook_3", MMethod.FindMethod("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel", null, boolean.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),
                List.class, boolean.class}));
    }

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieInit(MethodContainer container) {
        Finders.BaseChatPieInit_8893(container);
    }

    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieOld(MethodContainer container) {
        Finders.BaseChatPieInit(container);
    }

    @VerController
    @XPExecutor(methodID = "basechatpie_init", period = XPExecutor.After)
    public BaseXPExecutor basechatpie_init() {
        return param -> {
            ed = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.widget.XEditTextEx"));
            chatPie = param.thisObject;
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1() {
        return param -> {
            Object AddMsg = param.args[0];
            int istroop = MField.GetField(AddMsg, "istroop", int.class);
            if (!picCookies.isEmpty() && AddMsg.getClass().getName().contains("MessageForReplyText") && (istroop == 1 || istroop == 0)) {
                String Text = MField.GetField(AddMsg, "msg", String.class);
                if (TextUtils.isEmpty(Text)) {
                    Text = MField.GetFirstField(AddMsg, CharSequence.class) + "";
                }
                if (Text.contains("[PicCookie")) {
                    String strTo = Text.substring(0, Text.indexOf("["));
                    Text = Text.substring(Text.indexOf("["));
                    String GroupUin = QQSessionUtils.getGroupUin();
                    String UserUin = QQSessionUtils.getFriendUin();
                    MField.SetField(AddMsg, "msg", strTo.isEmpty() ? " " : strTo);
                    if (HostInfo.getVerCode() > 7685)
                        MField.SetField(AddMsg, "charStr", strTo.isEmpty() ? " " : strTo);
                    else MField.SetField(AddMsg, "sb", strTo.isEmpty() ? " " : strTo);

                    MMethod.CallMethod(AddMsg, "prewrite", void.class, new Class[0]);

                    String[] Cookies = StringUtils.GetStringMiddleMix(Text, "[PicCookie=", "]");
                    for (String ss : Cookies) {
                        if (picCookies.containsKey(ss)) {
                            Text = Text.replace("[PicCookie=" + ss + "]", "[PicUrl=" + picCookies.get(ss) + "]");
                        }
                    }
                    picCookies.clear();
                    QQMsgSendUtils.decodeAndSendMsg(GroupUin, UserUin, Text, AddMsg);
                    param.setResult(null);
                }
            }
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2() {
        return param -> {
            if (IsAvailable()) {
                String Path = (String) param.args[3];
                AddToPreSendList(Path);
                param.setResult(true);
            }
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_3")
    public BaseXPExecutor worker_3() {
        return param -> {
            if (IsAvailable()) {
                List<String> l = (List) param.args[1];
                for (String str : l) {
                    if (str.toLowerCase(Locale.ROOT).endsWith(".mp4")) continue;
                    AddToPreSendList(str);
                }
                param.setResult(true);
            }
        };
    }
}
