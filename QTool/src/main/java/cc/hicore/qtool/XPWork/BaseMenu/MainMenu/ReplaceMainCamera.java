package cc.hicore.qtool.XPWork.BaseMenu.MainMenu;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.HookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

//(未实装)替换首页的相机按钮为QTool设置按钮
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
