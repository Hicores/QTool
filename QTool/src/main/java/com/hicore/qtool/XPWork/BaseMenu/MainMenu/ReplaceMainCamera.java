package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import com.hicore.ConfigUtils.GlobalConfig;
import com.hicore.HookItem;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class ReplaceMainCamera extends BaseHookItem {
    private static final String TAG = "ReplaceMainCamera";
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
        return GlobalConfig.Get_Boolean("Switch_Main_Camera",false);
    }

    @Override
    public boolean check() {
        return getHookMethod() != null;
    }
    private Method getHookMethod(){
        return null;
    }
}
