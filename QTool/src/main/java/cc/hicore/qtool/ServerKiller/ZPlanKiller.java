package cc.hicore.qtool.ServerKiller;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "禁用ZPlan",groupName = "服务调节",targetID = 4,type = 1,id = "ZPlanKiller")
public class ZPlanKiller  extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable){
                Object o = param.args[2];
                if (o != null){
                    MMethod.CallMethodSingle(o,"a",void.class,false);
                }
            }
        });
        XPBridge.HookBefore(m[1],param -> {
            if (IsEnable)param.setResult(false);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(ZPlanKiller.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        if (HostInfo.getVerCode() > 8100){
            m[0] = MMethod.FindMethod("com.tencent.mobileqq.zplan.utils.api.impl.ZPlanAccessibleHelperImpl","getZPlanWhiteListFromNet",void.class,new Class[]{
                    List.class,List.class, MClass.loadClass("com.tencent.mobileqq.zplan.servlet.api.IZplanAccessableCallback")
            });
            m[1] = MMethod.FindMethod("com.tencent.mobileqq.zplan.utils.api.impl.ZPlanAccessibleHelperImpl","isZPlanAccessible",boolean.class,new Class[]{
                    long.class,long.class
            });
        }else {
            m[0] = MMethod.FindMethod("com.tencent.mobileqq.zplan.servlet.api.impl.ZPlanRequestImpl","getZPlanWhiteListFromNet",void.class,new Class[]{
                    List.class,List.class, MClass.loadClass("com.tencent.mobileqq.zplan.servlet.api.IZplanAccessableCallback")
            });
            m[1] = MMethod.FindMethod("com.tencent.mobileqq.zplan.servlet.api.impl.ZPlanRequestImpl","isInZplanWhiteList",boolean.class,new Class[]{
                    long.class,long.class
            });
        }
        return m;
    }
}
