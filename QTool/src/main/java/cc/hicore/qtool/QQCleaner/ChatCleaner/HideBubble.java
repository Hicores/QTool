package cc.hicore.qtool.QQCleaner.ChatCleaner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "屏蔽聊天特殊气泡",groupName = "聊天净化",type = 1,id = "ChatBubbleHider",targetID = 2)
public class HideBubble extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;

    @Override
    public String getTag() {
        return "屏蔽聊天气泡";
    }

    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0],param -> {
            if (IsEnable){
                List list = MField.GetField(param.thisObject,"a", List.class);
                if(list==null)return;
                Object ChatMsg = list.get((int) param.args[0]);
                MField.SetField(ChatMsg,"vipBubbleID",(long)0);
                MField.SetField(ChatMsg,"vipBubbleDiyTextId",0);
                MField.SetField(ChatMsg,"vipSubBubbleId",0);
            }
        });
        XPBridge.HookBefore(m[1],param -> {
            if (IsEnable){
                Object ChatMsg = param.args[2];
                MField.SetField(ChatMsg,"vipBubbleID",(long)0);
                MField.SetField(ChatMsg,"vipBubbleDiyTextId",0);
                MField.SetField(ChatMsg,"vipSubBubbleId",0);
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
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(HideBubble.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1","getView", View.class,new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });;
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy","a", void.class,new Class[]{
                String.class,
                int.class,
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                boolean.class
        });
        return m;
    }
}
