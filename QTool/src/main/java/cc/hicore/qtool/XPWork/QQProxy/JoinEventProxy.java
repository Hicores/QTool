package cc.hicore.qtool.XPWork.QQProxy;

import cc.hicore.HookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class JoinEventProxy extends BaseHookItem {

    @Override
    public boolean startHook() throws Throwable {
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
    public static void sendResponse(Object requestInfo,boolean isAccept,String reason,boolean isBlack){

    }
}
