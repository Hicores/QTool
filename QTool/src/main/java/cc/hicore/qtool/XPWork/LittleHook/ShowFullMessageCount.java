package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "显示完整消息数量",targetID = 1,groupName = "功能辅助",id = "ShowFullMessageCount",type = 1)
public class ShowFullMessageCount extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XposedBridge.hookMethod(getMethod(), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (IsEnable){
                    param.args[4]=Integer.MAX_VALUE;
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (IsEnable && HostInfo.getVerCode() > 5570){
                    int Type = (int) param.args[1];
                    if(Type == 4 || Type == 7 || Type == 9|| Type == 3) {
                        TextView t = (TextView) param.args[0];
                        int Count = (int) param.args[2];
                        String str = ""+Count;
                        ViewGroup.LayoutParams params = t.getLayoutParams();
                        params.width = Utils.dip2px(HookEnv.AppContext,9+7*str.length());
                        t.setLayoutParams(params);
                    }
                }
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
        if (IsCheck) HookLoader.CallHookStart(ShowFullMessageCount.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.widget.CustomWidgetUtil"),"a",void.class,new Class[]{
                TextView.class, int.class, int.class, int.class, int.class, String.class
        });
    }
}
