package cc.hicore.qtool.ChatHook.ChatCracker;

import android.content.Intent;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

public class RemoveCloudControl {
    @UIItem
    @VerController
    public UIInfo getUIInfo(){
        UIInfo info = new UIInfo();
        info.name = "启用部分被禁用的功能";
        info.desc = "比如贴表情,闪照";
        info.type = 1;
        info.targetID = 1;
        info.groupName = "聊天界面增强";
        return info;
    }
    @MethodScanner
    @VerController
    public void findMethod(MethodContainer container){
        container.addMethod("hook sticker", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.emoticon.EmojiStickerManager"),"l",boolean.class,new Class[0]));
        container.addMethod(MethodFinderBuilder.newFinderByString("hook flashpic"," mSelectedSendParams size:",m -> true));
    }

    @XPExecutor(methodID = "hook sticker")
    @VerController
    public BaseXPExecutor sticker_work(){
        return param -> {
            param.setResult(true);
        };
    }
    @XPExecutor(methodID = "hook flashpic")
    @VerController
    public BaseXPExecutor flashpic_work(){
        return param -> {
            Intent intent = (Intent) param.args[0];
            intent.putExtra("showFlashPic",true);
        };
    }
}
