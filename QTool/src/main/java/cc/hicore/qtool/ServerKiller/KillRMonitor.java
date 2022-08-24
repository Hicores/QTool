package cc.hicore.qtool.ServerKiller;

import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@XPItem(name = "KillRMonitor", itemType = XPItem.ITEM_Api,max_targetVer = QQVersion.QQ_8_9_5,targetVer = QQVersion.QQ_8_9_5)
public class KillRMonitor {
    @VerController
    @CommonExecutor
    public void doWork(){
        XposedHelpers.findAndHookMethod("com.tencent.rmonitor.Magnifier$1", HookEnv.mLoader, "run", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.rmonitor.metrics.UVEventMonitor$1", HookEnv.mLoader, "run", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                param.setResult(null);
            }
        });
    }

}
