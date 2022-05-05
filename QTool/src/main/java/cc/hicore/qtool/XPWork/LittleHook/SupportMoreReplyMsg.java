package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false, isRunInAllProc = false)
@UIItem(name = "解锁更多类型消息左滑回复", type = 1, id = "SupportModeMsgReply", targetID = 1,groupName = "聊天界面增强")
public class SupportMoreReplyMsg extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(), param -> {
            if (IsEnable) param.setResult(true);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    public Method getMethod() {
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.bubble.LeftSwipeReplyHelper"), "h", boolean.class, new Class[0]);
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(SupportMoreReplyMsg.class.getName());
        }
    }

    @Override
    public void ListItemClick(Context context) {

    }
}
