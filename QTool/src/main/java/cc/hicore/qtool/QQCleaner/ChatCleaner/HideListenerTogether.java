package cc.hicore.qtool.QQCleaner.ChatCleaner;

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
import cc.hicore.qtool.XposedInit.MethodFinder;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "屏蔽一起听歌顶栏",groupName = "聊天界面净化",targetID = 2,type = 1,id = "HideListenerTogether")
public class HideListenerTogether extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            if (IsEnable){
                param.setResult(null);
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
        if (IsCheck) HookLoader.CallHookStart(HideListenerTogether.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.listentogether.ui.BaseListenTogetherPanel"),"a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.listentogether.ListenTogetherSession")
        });
        if (m == null){
            m = MethodFinder.findMethodFromCache("HideListenTogether");
            if (m == null){
                MethodFinder.NeedReportToFindMethod("HideListenTogether","屏蔽一起听歌顶栏","onUIModuleNeedRefresh, uidata=",a -> true);
            }
        }
        return m;
    }
}
