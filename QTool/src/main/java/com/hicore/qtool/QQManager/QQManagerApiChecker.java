package com.hicore.qtool.QQManager;

import com.hicore.HookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isRunInAllProc = false,isDelayInit = false)
public class QQManagerApiChecker extends BaseHookItem {
    private static final String TAG = "QQManagerApiChecker";
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public boolean check() {
        return false;
    }
}
