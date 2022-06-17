package cc.hicore.HookItemLoader.bridge;

import de.robv.android.xposed.XC_MethodHook;

public interface BaseXPExecutor {
    void onInvoke(XC_MethodHook.MethodHookParam param);
}
