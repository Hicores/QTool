package cc.hicore.qtool.XPWork.LittleHook;

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

@XPItem(name = "强制平板模式",itemType = XPItem.ITEM_Hook,targetVer = QQVersion.QQ_8_9_15)
public class ForceTabletMode {
    CoreLoader.XPItemInfo info;
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "强制平板模式";
        ui.desc = "重启QQ生效";
        ui.groupName = "功能辅助";
        ui.targetID = 1;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void FindMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","initDeviceType type = ",m->true));
    }

    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor XPWork(){
        return param -> {
            MField.SetField(null,info.scanResult.get("hook").getDeclaringClass(),"b",DeviceType.class, DeviceType.TABLET);
        };
    }
}
