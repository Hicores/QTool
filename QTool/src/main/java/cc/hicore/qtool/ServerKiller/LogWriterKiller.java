package cc.hicore.qtool.ServerKiller;

import java.util.List;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
@XPItem(name = "禁用LoggerWriter",itemType = XPItem.ITEM_Hook,proc = XPItem.PROC_ALL)
public class LogWriterKiller{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "禁用LoggerWriter";
        ui.groupName = "服务调节";
        ui.targetID = 4;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.QLogItemManager"),"addLogItem",void.class,new Class[]{
                MClass.loadClass("com.tencent.qphone.base.util.QLogItem")
        }));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.LogWriterManager"),"writeLogItems",boolean.class,new Class[]{
                List.class
        }));
        container.addMethod("hook_3",MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.QLogItemManager"),"init",void.class,new Class[]{
                long.class
        }));
        container.addMethod("hook_4",MMethod.FindMethod(MClass.loadClass("com.tencent.qphone.base.util.QLogItemManager$WriteHandler"),"tryInit",void.class,new Class[0]));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1(){
        return param -> param.setResult(null);
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> param.setResult(true);
    }
    @VerController
    @XPExecutor(methodID = "hook_3")
    public BaseXPExecutor worker_3(){
        return param -> param.setResult(null);
    }
    @VerController
    @XPExecutor(methodID = "hook_4")
    public BaseXPExecutor worker_4(){
        return param -> param.setResult(null);
    }
}
