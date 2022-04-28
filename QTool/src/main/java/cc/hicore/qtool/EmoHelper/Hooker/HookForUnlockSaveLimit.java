package cc.hicore.qtool.EmoHelper.Hooker;


import android.os.Message;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;


@HookItem(isDelayInit = true, isRunInAllProc = false)
@UIItem(itemName = "表情收藏上限后存在本地", itemType = 1, mainItemID = 1, ID = "HookForUnlockSaveLimit")
public class HookForUnlockSaveLimit extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;

    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0], param -> {
            if (IsEnable) {
                MField.SetField(null, MClass.loadClass("com.tencent.mobileqq.emosm.favroaming.FavEmoConstant"), "a", int.class, 2000);
                MField.SetField(null, MClass.loadClass("com.tencent.mobileqq.emosm.favroaming.FavEmoConstant"), "b", int.class, 2000);
            }

        });
        XPBridge.HookBefore(m[1], param -> {
            if (IsEnable) {
                Message message = (Message) param.args[0];
                int code = message.what;
                Object emoData = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.mobileqq.data.CustomEmotionData"));
                String emoMD5 = MField.GetField(emoData, "md5", String.class);
                if (code == 2) {
                    message.what = 3;
                    AddToLocal(emoMD5);
                }
            }
        });

        return true;
    }

    private void AddToLocal(String md5) {
        try {
            Object manager = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.emosm.favroaming.EmoticonFromGroupManager"), HookEnv.AppInterface);
            Object entity = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.EmoticonFromGroupEntity"));

            MField.SetField(entity, "bigURL", "chatimg:" + md5);
            MField.SetField(entity, "md5", md5);
            MField.SetField(entity, "thumbURL", "chatthumb:" + md5);

            MMethod.CallMethodSingle(manager, "e", void.class, entity);
        } catch (Exception e) {
            LogUtils.error("AddPicToLocal", e);
        }


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
        if (IsCheck) {
            HookLoader.CallHookStart(HookForUnlockSaveLimit.class.getName());
        }
    }

    @Override
    public void ListItemClick() {

    }

    public Method[] getMethod() {
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod("com.tencent.mobileqq.emosm.api.impl.FavroamingDBManagerServiceImpl", "getEmoticonDataList", List.class, new Class[0]);
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.emosm.favroaming.EmoAddedAuthCallback", "handleMessage", boolean.class, new Class[]{Message.class});
        return m;
    }
}