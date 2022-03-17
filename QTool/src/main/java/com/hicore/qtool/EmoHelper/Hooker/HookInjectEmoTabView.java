package com.hicore.qtool.EmoHelper.Hooker;


import com.hicore.HookItem;

import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;


@HookItem(isDelayInit = true,isRunInAllProc = false)
public class HookInjectEmoTabView extends BaseHookItem {
    public static boolean IsEnable = true;
    @Override
    public boolean startHook() throws Throwable {
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return true;
    }

}
