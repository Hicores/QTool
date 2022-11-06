package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.app.AlertDialog;
import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIClick;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.qtool.HookEnv;

@XPItem(name = "主界面侧滑净化", itemType = XPItem.ITEM_Hook)
public class HideSlideItem {
    static HashMap<String, String> cacheItemData = new HashMap<>();

    @VerController
    @UIItem
    public UIInfo getUI() {
        UIInfo ui = new UIInfo();
        ui.name = "主界面侧滑净化";
        ui.groupName = "主界面净化";
        ui.desc = "点击设置净化的项目";
        ui.type = 1;
        ui.targetID = 2;
        return ui;
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        Method[] m = new Method[2];
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeMenuConfigBean");
        for (Method m1 : clz.getDeclaredMethods()) {
            if (m1.getReturnType().isArray()) {
                m[0] = m1;
                break;
            }
        }

        container.addMethod("hook1", m[0]);
        container.addMethod(MethodFinderBuilder.newFinderByString("hook2", "VipInfoHandler payRuleUin changed", m2 -> m2.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.QQSettingMe")));
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("hook1", "parse() group == null || group.length() == 0", m -> {
            for (Method m1 : m.getDeclaringClass().getDeclaredMethods()) {
                if (m1.getReturnType().isArray()) {
                    return m1;
                }
            }
            return null;
        }));
        container.addMethod(MethodFinderBuilder.newFinderByString("hook2", "VipInfoHandler payRuleUin changed", m2 -> m2.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.QQSettingMe")));

    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @XPExecutor(methodID = "hook1", period = XPExecutor.After)
    public BaseXPExecutor worker1_890() {
        return param -> {
            Object mArr = param.getResult();
            cacheItemData = new HashMap<>();
            List<String> HideSingle = HookEnv.Config.getList("Set", "HideSlideItem", true);
            ArrayList saveArrays = new ArrayList();
            for (int i = 0; i < Array.getLength(mArr); i++) {
                Object item = Array.get(mArr, i);
                String Signer = MField.GetFirstField(item, String.class);
                String Title = MField.GetField(MField.GetField(item, "f"), "a", String.class);
                HideSlideItem.cacheItemData.put(Signer, Title);
                if (!HideSingle.contains(Signer)) saveArrays.add(item);
            }
            Object newArr = Array.newInstance(mArr.getClass().getComponentType(), saveArrays.size());
            for (int i = 0; i < saveArrays.size(); i++) {
                Array.set(newArr, i, saveArrays.get(i));
            }
            param.setResult(newArr);
        };
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @XPExecutor(methodID = "hook1", period = XPExecutor.After)
    public BaseXPExecutor worker1() {
        return param -> {
            Object mArr = param.getResult();
            cacheItemData = new HashMap<>();
            List<String> HideSingle = HookEnv.Config.getList("Set", "HideSlideItem", true);
            ArrayList saveArrays = new ArrayList();
            for (int i = 0; i < Array.getLength(mArr); i++) {
                Object item = Array.get(mArr, i);
                String Signer = MField.GetFirstField(item, String.class);
                String Title = MField.GetField(MField.GetFirstField(item, MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean$Title")), "a", String.class);
                HideSlideItem.cacheItemData.put(Signer, Title);
                if (!HideSingle.contains(Signer)) saveArrays.add(item);
            }
            Object newArr = Array.newInstance(MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean"), saveArrays.size());
            for (int i = 0; i < saveArrays.size(); i++) {
                Array.set(newArr, i, saveArrays.get(i));
            }
            param.setResult(newArr);
        };
    }

    @VerController
    @XPExecutor(methodID = "hook2")
    public BaseXPExecutor worker2() {
        return param -> {
            List<String> HideSingle = HookEnv.Config.getList("Set", "HideSlideItem", true);
            if (HideSingle.contains("d_vip_identity")) param.setResult(null);
        };
    }

    @VerController
    @UIClick
    public void UIClick(Context context) {
        ArrayList<String> showText = new ArrayList<>();
        ArrayList<String> signleList = new ArrayList<>();
        List<String> HideSingle = HookEnv.Config.getList("Set", "HideSlideItem", true);
        boolean[] check = new boolean[cacheItemData.size()];
        int j = 0;
        for (String Signer : cacheItemData.keySet()) {
            showText.add(cacheItemData.get(Signer) + "(" + Signer + ")");
            if (HideSingle.contains(Signer)) check[j] = true;
            j++;
            signleList.add(Signer);
        }
        String[] checkTitle = showText.toArray(new String[0]);

        new AlertDialog.Builder(context)
                .setMultiChoiceItems(checkTitle, check, (dialog, which, isChecked) -> {

                }).setNegativeButton("保存", (dialog, which) -> {
                    ArrayList<String> save = new ArrayList<>();
                    for (int i = 0; i < check.length; i++) {
                        if (check[i]) {
                            save.add(signleList.get(i));
                        }
                    }
                    HookEnv.Config.setList("Set", "HideSlideItem", save);
                }).show();
    }
}
