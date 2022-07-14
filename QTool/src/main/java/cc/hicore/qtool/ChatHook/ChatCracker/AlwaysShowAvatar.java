package cc.hicore.qtool.ChatHook.ChatCracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.LayoutUtils;

@XPItem(name = "聊天消息强制显示头像",itemType = XPItem.ITEM_Hook)
public class AlwaysShowAvatar{

    @UIItem
    @VerController
    public UIInfo getUIInfo(){
        UIInfo ui = new UIInfo();
        ui.name = "聊天消息强制显示头像";
        ui.desc = "主要针对某些全屏卡片";
        ui.targetID = 2;
        ui.type = 1;
        ui.groupName = "聊天净化";
        return ui;
    }

    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor hookWorker(){
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
            View avatar = LayoutUtils.findView("VasAvatar",mLayout);
            if (avatar != null){
                if (avatar.getVisibility() != View.VISIBLE){
                    avatar.setVisibility(View.VISIBLE);
                }
            }
        };
    }



    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        }));
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void getHookMethod_890(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","AIO_ChatAdapter_getView", m -> m.getDeclaringClass().getName().startsWith("com.tencent.mobileqq.activity.aio")));
    }

}
