package cc.hicore.qtool.XPWork.LittleHook;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isDelayInit = false,isRunInAllProc = true)
public class FakeDevInfo extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {
        Method m = MMethod.FindMethod(MClass.loadClass("NS_MOBILE_EXTRA.GetDeviceInfoRsp"),"readFrom",void.class,new Class[]{MClass.loadClass("com.qq.taf.jce.JceInputStream")});
        XPBridge.HookAfter(m,param -> {
            List devinfo_List = MField.GetField(param.thisObject,"vecDeviceInfo");
            for (Object devInfo : devinfo_List){

            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return true;
    }
}
