package cc.hicore.qtool.ChatHook.Repeater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(mainItemID = 1,itemType = 1,ID = "RepeaterHooker",itemName = "复读消息+1")
public class Hooker extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0],param -> {
            if (!IsEnable)return;
            Object mGetView = param.getResult();
            RelativeLayout baseChatItem = null;
            if (mGetView instanceof RelativeLayout)baseChatItem = (RelativeLayout) mGetView;else return;
            List MessageRecoreList = MField.GetField(param.thisObject,"a", List.class);
            if(MessageRecoreList==null)return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
            if (ChatMsg == null)return;
            String ActivityName = baseChatItem.getContext().getClass().getName();
            if (ActivityName.contains("MultiForwardActivity"))return;
            RepeaterHelper.createRepeatIcon(baseChatItem,ChatMsg);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod()[0] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(Hooker.class.getName());
    }

    @Override
    public void ListItemClick() {

    }
    private Method[] getMethod(){
        Method[] m = new Method[1];
        m[0] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1","getView", View.class,new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        return m;
    }
}
