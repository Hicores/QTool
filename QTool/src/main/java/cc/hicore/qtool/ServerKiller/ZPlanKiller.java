package cc.hicore.qtool.ServerKiller;

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
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
@XPItem(name = "禁用ZPlan",itemType = XPItem.ITEM_Hook,targetVer = QQVersion.QQ_8_8_80)
public class ZPlanKiller{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "禁用ZPlan";
        ui.groupName = "服务调节";
        ui.targetID = 4;
        ui.type = 1;
        return ui;
    }
    @VerController(max_targetVer = QQVersion.QQ_8_8_90)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod("com.tencent.mobileqq.zplan.servlet.api.impl.ZPlanRequestImpl","getZPlanWhiteListFromNet",void.class,new Class[]{
                List.class,List.class, MClass.loadClass("com.tencent.mobileqq.zplan.servlet.api.IZplanAccessableCallback")
        }));
        container.addMethod("hook_2",MMethod.FindMethod("com.tencent.mobileqq.zplan.servlet.api.impl.ZPlanRequestImpl","isInZplanWhiteList",boolean.class,new Class[]{
                long.class,long.class
        }));
    }
    @VerController(targetVer = QQVersion.QQ_8_8_90)
    @MethodScanner
    public void getHookMethod_8_8_95(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod("com.tencent.mobileqq.zplan.utils.api.impl.ZPlanAccessibleHelperImpl","getZPlanWhiteListFromNet",void.class,new Class[]{
                List.class,List.class, MClass.loadClass("com.tencent.mobileqq.zplan.servlet.api.IZplanAccessableCallback")
        }));
        container.addMethod("hook_2",MMethod.FindMethod("com.tencent.mobileqq.zplan.utils.api.impl.ZPlanAccessibleHelperImpl","isZPlanAccessible",boolean.class,new Class[]{
                long.class,long.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1(){
        return param -> {
            Object o = param.args[2];
            if (o != null){
                MMethod.CallMethodSingle(o,"a",void.class,false);
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> param.setResult(false);
    }
}
