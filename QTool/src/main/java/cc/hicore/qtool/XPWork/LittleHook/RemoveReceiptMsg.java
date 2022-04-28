package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;

import java.lang.reflect.Member;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedHelpers;

@HookItem(isDelayInit = false, isRunInAllProc = false)
@UIItem(itemName = "屏蔽回执消息提示", itemType = 1, mainItemID = 2, ID = "RemoveReceiptMsg")
public class RemoveReceiptMsg extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {
        Member[] m = getMethod();
        XPBridge.HookBefore(m[1], param -> {
            if (IsEnable) {
                Object Message = param.args[0];
                int NeedRemove = HostInfo.getVerCode() > 7540 ? 13 : 12;
                int sr = MField.GetField(Message, "bizType", int.class);
                if (sr == NeedRemove) {
                    param.setResult(null);
                }
            }
        });
        XPBridge.HookAfter(m[0], param -> {
            if (IsEnable) {
                MField.SetField(param.thisObject, "c", String.class, "");
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
        Member[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(RemoveReceiptMsg.class.getName());
    }

    @Override
    public void ListItemClick() {

    }

    public Member[] getMethod() {
        Member[] m = new Member[2];

        m[0] = XposedHelpers.findConstructorBestMatch(MClass.loadClass("com.tencent.mobileqq.activity.recent.msg.TroopReceiptMsg"), Context.class);
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"), "notifyMessageReceived", void.class, new Class[]{MClass.loadClass("com.tencent.imcore.message.Message"), boolean.class, boolean.class});
        return m;
    }
}
