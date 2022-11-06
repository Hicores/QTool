package cc.hicore.qtool.ChatHook.Repeater;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.List;

import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIClick;
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
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;

@XPItem(name = "消息复读", itemType = XPItem.ITEM_Hook)
public class Hooker {
    public static Drawable cacheDrawable;

    @VerController
    @UIItem
    public UIInfo getUIInfo() {
        UIInfo ui = new UIInfo();
        ui.groupName = "聊天辅助";
        ui.name = "消息复读+1";
        ui.desc = "点击进行更多设置";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }

    @VerController
    @UIClick
    public void SetClick(Context context) {
        RepeaterSet.startShow(context);
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        Finders.AIOMessageListAdapter_getView(container);
        container.addMethod("hide_def_icon", MMethod.FindMethod("com.tencent.mobileqq.data.ChatMessage", "isFollowMessage", boolean.class, new Class[0]));
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        Finders.AIOMessageListAdapter_getView_890(container);
        container.addMethod("hide_def_icon", MMethod.FindMethod("com.tencent.mobileqq.data.ChatMessage", "isFollowMessage", boolean.class, new Class[0]));

    }

    @XPExecutor(methodID = "onAIOGetView", period = XPExecutor.After)
    @VerController
    public BaseXPExecutor hook_work() {
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout baseChatItem = null;
            if (mGetView instanceof RelativeLayout) baseChatItem = (RelativeLayout) mGetView;
            else return;
            Context context = baseChatItem.getContext();
            if (context.getClass().getName().contains("MultiForwardActivity")) return;
            List MessageRecoreList = MField.GetFirstField(param.thisObject, List.class);
            Assert.notNull(MessageRecoreList, "List is NULL");
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
            Assert.notNull(MessageRecoreList, "ChatMessage is NULL");
            String ActivityName = baseChatItem.getContext().getClass().getName();
            if (ActivityName.contains("MultiForwardActivity")) return;
            RepeaterHelper.createRepeatIcon(baseChatItem, ChatMsg);
        };
    }

    @XPExecutor(methodID = "hide_def_icon")
    @VerController
    public BaseXPExecutor hide_def_icon() {
        return param -> {
            param.setResult(false);
        };
    }

    @VerController
    @CommonExecutor
    public void requestRepeatIcon() {
        String iconPath = HookEnv.ExtraDataPath + "res/repeat.png";
        try {
            if (!new File(iconPath).exists()) {
                File path = new File(iconPath).getParentFile();
                path.mkdirs();
                throw new RuntimeException("Can't load Repeat Icon");
            } else {
                cacheDrawable = Drawable.createFromPath(iconPath);
                if (cacheDrawable == null) {
                    throw new RuntimeException("Can't load Repeat Icon");
                }
            }

        } catch (Exception e) {
            LogUtils.warning("Repeater", "Not Found res File,use default icon");
            cacheDrawable = HookEnv.AppContext.getDrawable(R.drawable.repeat);
        }

    }

}
