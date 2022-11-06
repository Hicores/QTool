package cc.hicore.qtool.ChatHook.GameHook;

import android.app.AlertDialog;
import android.content.Context;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "自定义骰子猜拳", itemType = XPItem.ITEM_Hook)
public class DictHook {
    int CurrentRamdonDict = -1;

    private static void ReInvokeMethod(XC_MethodHook.MethodHookParam params) {
        try {
            XposedBridge.invokeOriginalMethod(params.method, params.thisObject, params.args);
        } catch (Exception e) {
        }
    }

    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "自定义骰子猜拳";
        ui.groupName = "聊天辅助";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }

    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1() {
        return param -> {
            String Name = MField.GetField(param.args[3], "name");
            if (Name.contains("骰子") && CurrentRamdonDict == -1) {
                param.setResult(null);
                selectDictGame(param);
            } else if (Name.equals("猜拳") && CurrentRamdonDict == -1) {
                param.setResult(null);
                selectCFGGame(param);
            }
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2() {
        return param -> {
            if (CurrentRamdonDict == -1) return;
            if (CurrentRamdonDict == 666) {
                CurrentRamdonDict = -1;
                return;
            }
            int MaxValue = (int) param.args[0];
            if (MaxValue == 6 || MaxValue == 3) {
                param.setResult(CurrentRamdonDict);
                CurrentRamdonDict = -1;
            }
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_3")
    public BaseXPExecutor worker_3() {
        return param -> {
            param.setResult(true);
        };
    }

    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    public void getMethod(MethodContainer container) {
        container.addMethod("hook_1", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.emoticonview.sender.PicEmoticonInfoSender"), "sendMagicEmoticon", void.class, new Class[]{
                MClass.loadClass("com.tencent.common.app.business.BaseQQAppInterface"),
                Context.class,
                Classes.BaseSessionInfo(),
                MClass.loadClass("com.tencent.mobileqq.data.Emoticon"),
                MClass.loadClass("com.tencent.mobileqq.emoticon.StickerInfo")
        }));
        container.addMethod("hook_2", MMethod.FindMethod("com.tencent.mobileqq.magicface.drawable.PngFrameUtil", null, int.class, new Class[]{int.class}));
        container.addMethod("hook_3", MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.emoticon.api.impl.EmojiManagerServiceImpl"), "getMagicFaceSendAccessControl", boolean.class, new Class[0]));
    }

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void getMethod_890(MethodContainer container) {
        container.addMethod("hook_1", MMethod.FindMethodByName(MClass.loadClass("com.tencent.mobileqq.emoticonview.sender.PicEmoticonInfoSender"), "sendMagicEmoticon"));
        container.addMethod(MethodFinderBuilder.newFinderByString("hook_2", "func checkRandomPngExist, exception:", m -> MMethod.FindMethod(m.getDeclaringClass(), null, int.class, new Class[]{int.class})));
        container.addMethod("hook_3", MMethod.FindMethodByName(MClass.loadClass("com.tencent.mobileqq.emoticon.api.impl.EmojiManagerServiceImpl"), "getMagicFaceSendAccessControl"));
    }

    private void selectCFGGame(XC_MethodHook.MethodHookParam params) {
        final String[] SelectItem = new String[]{"石头", "剪刀", "布"};
        new AlertDialog.Builder(Utils.getTopActivity(), 3)
                .setTitle("设置猜拳的内容")
                .setItems(SelectItem, (dialog, which) -> {
                    CurrentRamdonDict = which;
                    ReInvokeMethod(params);
                }).setNegativeButton("随机", (dialog, which) -> {
                    CurrentRamdonDict = 666;
                    ReInvokeMethod(params);
                }).show();
    }

    private void selectDictGame(XC_MethodHook.MethodHookParam params) {
        final String[] SelectItem = new String[]{"1", "2", "3", "4", "5", "6"};
        new AlertDialog.Builder(Utils.getTopActivity(), 3)
                .setTitle("设置骰子的点数")
                .setItems(SelectItem, (dialog, which) -> {
                    CurrentRamdonDict = which;
                    ReInvokeMethod(params);
                }).setNegativeButton("随机", (dialog, which) -> {
                    CurrentRamdonDict = 666;
                    ReInvokeMethod(params);
                }).show();

    }
}
