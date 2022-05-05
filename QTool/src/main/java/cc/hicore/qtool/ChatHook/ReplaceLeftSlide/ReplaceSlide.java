package cc.hicore.qtool.ChatHook.ReplaceLeftSlide;

import android.content.Context;

import java.lang.reflect.Member;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "替换群侧滑",desc = "替换群聊侧滑为成员探测器",targetID = 1,groupName = "聊天界面增强",id = "ReplaceSlide",type = 1)
public class ReplaceSlide extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            if (IsEnable){
                Object obj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.drawer.TroopMultiCardDrawer"),param.args);
                param.setResult(obj);
            }
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

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(ReplaceSlide.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Member getMethod(){
        return MClass.findCons(MClass.loadClass("com.tencent.mobileqq.activity.aio.drawer.TroopAppShortcutDrawer"),new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")
        });
    }
}
