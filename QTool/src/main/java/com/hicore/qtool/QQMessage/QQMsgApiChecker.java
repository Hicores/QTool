package com.hicore.qtool.QQMessage;

import com.hicore.HookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class QQMsgApiChecker extends BaseHookItem {
    private static String TAG = "QQMsgApiChecker";
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
