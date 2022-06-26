package cc.hicore.qtool.EmoHelper.Hooker;


import android.content.Context;
import android.os.Message;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@XPItem(name = "表情收藏上限后存在本地",itemType = XPItem.ITEM_Hook,period = XPItem.Period_InitData)
public class HookForUnlockSaveLimit{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "表情收藏上限后存在本地";
        ui.type = 1;
        ui.targetID = 1;
        ui.groupName = "功能辅助";
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod("com.tencent.mobileqq.emosm.api.impl.FavroamingDBManagerServiceImpl", "getEmoticonDataList", List.class, new Class[0]));
        container.addMethod("hook_2",MMethod.FindMethod("com.tencent.mobileqq.emosm.favroaming.EmoAddedAuthCallback", "handleMessage", boolean.class, new Class[]{Message.class}));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker(){
        return param -> {
            MField.SetField(null, MClass.loadClass("com.tencent.mobileqq.emosm.favroaming.FavEmoConstant"), "a", int.class, 2000);
            MField.SetField(null, MClass.loadClass("com.tencent.mobileqq.emosm.favroaming.FavEmoConstant"), "b", int.class, 2000);
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> {
            Message message = (Message) param.args[0];
            int code = message.what;
            Object emoData = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.mobileqq.data.CustomEmotionData"));
            String emoMD5 = MField.GetField(emoData, "md5", String.class);
            if (code == 2) {
                message.what = 3;
                AddToLocal(emoMD5);
            }
        };
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
}