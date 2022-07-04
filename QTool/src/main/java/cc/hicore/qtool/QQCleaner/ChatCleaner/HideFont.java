package cc.hicore.qtool.QQCleaner.ChatCleaner;

import android.view.View;

import java.lang.reflect.Method;

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
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
@XPItem(name = "屏蔽聊天特效字体",itemType = XPItem.ITEM_Hook)
public class HideFont{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽聊天特效字体";
        ui.groupName = "聊天净化";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook_1","key_message_extra_info_flag",m->((Method)m).getParameterTypes()[0].equals(View.class) && ((Method)m).getParameterCount() == 2));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.etrump.mixlayout.ETTextView"),"setFont",void.class,new Class[]{
                MClass.loadClass("com.etrump.mixlayout.ETFont"), long.class,int.class,
        }));
    }
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getCommonMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.TextItemBuilder"),"b",void.class,new Class[]{
                View.class, Classes.ChatMessage()
        }));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.etrump.mixlayout.ETTextView"),"setFont",void.class,new Class[]{
                MClass.loadClass("com.etrump.mixlayout.ETFont"), long.class,int.class,
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1(){
        return param -> param.setResult(null);
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> param.setResult(null);
    }
}
