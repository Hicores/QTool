package cc.hicore.qtool.XPWork.LittleHook;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import de.robv.android.xposed.XposedBridge;

@XPItem(itemType = XPItem.ITEM_Hook, name = "VipStatusFucker")
public class VipStatusFucker {
    @UIItem
    @VerController
    public UIInfo UIInit() {
        UIInfo info = new UIInfo();
        info.name = "伪装VIP状态";
        info.desc = "可用来免VIP贴图";
        info.targetID = 3;
        info.type = 1;
        return info;
    }

    @MethodScanner
    @VerController
    public void MethodFiner(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("hook", "getPrivilegeFlags Friends is null", member -> ((Method) member).getParameterCount() == 1));
    }

    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor hookWorker() {
        return param -> {
            String uin = (String) param.args[0];
            if (uin == null) {
                int i = (int) XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                param.setResult(i | 2 | 4 | 8);
            }
        };
    }
}
