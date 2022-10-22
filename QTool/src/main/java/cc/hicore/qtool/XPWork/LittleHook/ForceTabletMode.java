package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;

import com.tencent.common.config.DeviceType;

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
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "强制平板模式",itemType = XPItem.ITEM_Hook,targetVer = QQVersion.QQ_8_9_15,proc = XPItem.PROC_ALL)
public class ForceTabletMode {
    CoreLoader.XPItemInfo info;
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "强制平板模式";
        ui.desc = "重启QQ生效,不保证一定有用";
        ui.groupName = "功能辅助";
        ui.targetID = 4;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void FindMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","initDeviceType type = ",m->true));
        container.addMethod("hook1",MMethod.FindMethod(MClass.loadClass("com.tencent.hippy.qq.utils.HippyUtils"),"initDeviceType",void.class,new Class[]{Context.class}));
    }

    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor XPWork(){
        return param -> {
            Enum<?> type = MMethod.CallStaticMethod(MClass.loadClass("com.tencent.common.config.DeviceType"),"valueOf",MClass.loadClass("com.tencent.common.config.DeviceType"),
                    "TABLET");
            MField.SetField(null,info.scanResult.get("hook").getDeclaringClass(),"b",MClass.loadClass("com.tencent.common.config.DeviceType"), type);
        };
    }
    @VerController
    @XPExecutor(methodID = "hook1",period = XPExecutor.After)
    public BaseXPExecutor XPWork2(){
        return param -> {
            Enum<?> type = MMethod.CallStaticMethod(MClass.loadClass("com.tencent.common.config.DeviceType"),"valueOf",MClass.loadClass("com.tencent.common.config.DeviceType"),
                    "TABLET");
            MField.SetField(null,info.scanResult.get("hook1").getDeclaringClass(),"b",MClass.loadClass("com.tencent.common.config.DeviceType"), type);
        };
    }
}
