package cc.hicore.qtool.ChatHook.ChatCracker;

import android.content.Context;

import java.lang.reflect.Method;

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
@UIItem(name = "状态消息隐藏",groupName = "聊天净化",desc = "阻止状态消息显示在自己的消息记录中",type = 1,id = "ChangeTipToSystemMsg",targetID = 2)
public class ChangeTipToSystemMsg extends BaseHookItem implements BaseUiItem {
    @Override
    public String getTag() {
        return "状态消息隐藏";
    }

    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            if (IsEnable){
                Object ChatMsg = param.args[2];
                if (ChatMsg.getClass().getName().contains("MessageForUniteGrayTip")){
                    try {
                        MField.SetField(ChatMsg,"senderuin","10000");
                        MMethod.CallMethod(ChatMsg,"prewrite",void.class,new Class[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
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
        if (IsCheck) HookLoader.CallHookStart(ChangeTipToSystemMsg.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy","a", void.class,new Class[]{
                String.class,
                int.class,
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                boolean.class
        });
    }
}
