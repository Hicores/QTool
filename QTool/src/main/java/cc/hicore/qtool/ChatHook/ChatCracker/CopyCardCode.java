package cc.hicore.qtool.ChatHook.ChatCracker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Utils;

@SuppressLint("ResourceType")
@XPItem(itemType = XPItem.ITEM_Hook,name = "长按复制卡片代码")
public class CopyCardCode{
    @UIItem
    @VerController
    public UIInfo getUIInfo(){
        UIInfo info = new UIInfo();
        info.name = "长按复制卡片代码";
        info.type = 1;
        info.targetID = 1;
        info.groupName = "聊天界面增强";
        return info;
    }

    @VerController
    @XPExecutor(methodID = "onAIOGetView",period = XPExecutor.After)
    public BaseXPExecutor xpWorker(){
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if (mGetView instanceof RelativeLayout) {
                mLayout = (RelativeLayout) mGetView;
            } else {
                return;
            }
            List MessageRecoreList = MField.GetFirstField(param.thisObject,  List.class);
            if (MessageRecoreList == null) return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
            if (ChatMsg.getClass().getSimpleName().equals("MessageForArkApp") ||
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(ChatMsg.getClass()) ||
                    ChatMsg.getClass().getSimpleName().equals("MessageForStarLeague")) {
                //复制卡片消息的标题
                TextView tv = mLayout.findViewById(445588);
                if (tv == null) {
                    //长按标签,位于Parent顶部中央,最大化
                    RelativeLayout.LayoutParams RLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    RLP.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    tv = new TextView(mLayout.getContext());
                    mLayout.addView(tv, RLP);
                    tv.setText("长按复制卡片代码");
                    tv.setGravity(Gravity.CENTER);//居中显示
                    tv.setTextColor(Color.RED);
                    tv.setId(445588);
                }


                tv.setTag(ChatMsg);//保存消息对象
                tv.setOnLongClickListener(view -> {
                    Object ChatMessage = view.getTag();
                    try {
                        if (ChatMessage.getClass().getSimpleName().equals("MessageForArkApp")) {
                            Object ArkAppMsg = MField.GetField(ChatMessage, ChatMessage.getClass(), "ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                            String json = MMethod.CallMethod(ArkAppMsg, MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"), "toAppXml", String.class, new Class[0], new Object[0]);
                            Utils.SetTextClipboard(json);
                            Utils.ShowToast("已复制");
                        } else if (ChatMessage.getClass().getSimpleName().equals("MessageForStarLeague")) {
                            String xml = MMethod.CallMethodSingle(ChatMessage, "getExtInfoFromExtStr", String.class, "SavedXml");
                            if (TextUtils.isEmpty(xml)) {
                                Utils.ShowToast("未找到卡片描述信息,此类型消息必须开着模块接收才能复制代码");
                            } else {
                                Utils.SetTextClipboard(xml);
                                Utils.ShowToast("已复制");
                            }

                        } else if (MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(ChatMessage.getClass())) {
                            Object Structing = MField.GetField(ChatMessage, ChatMessage.getClass(), "structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                            String xml = MMethod.CallMethod(Structing, MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), "getXml", String.class, new Class[0], new Object[0]);
                            Utils.SetTextClipboard(xml);
                            Utils.ShowToast("已复制");
                        }

                    } catch (Throwable e) {
                        LogUtils.error("CopyXml", Log.getStackTraceString(e));
                    }
                    return false;
                });
            }
        };
    }

    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    public void getHookMethod(MethodContainer container){
        Finders.AIOMessageListAdapter_getView(container);
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void getHookMethod_890(MethodContainer container){
        Finders.AIOMessageListAdapter_getView_890(container);
    }
}
