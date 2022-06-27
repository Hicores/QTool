package cc.hicore.qtool.QQCleaner.QQCleanerHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIClick;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
@XPItem(name = "隐藏表情面板图标",itemType = XPItem.ITEM_Hook)
public class HideEmoPanelGifAndSelf{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "隐藏表情面板图标";
        ui.type = 1;
        ui.targetID = 2;
        ui.groupName = "聊天界面净化";
        return ui;
    }

    static HashMap<String, String> HideMap = new HashMap<>();{
        HideMap.put("加号", "13");
        HideMap.put("自带表情", "7");
        HideMap.put("收藏表情", "4");
        HideMap.put("热门表情", "12");
        HideMap.put("厘米秀", "15");
        HideMap.put("超级QQ秀", "17");
        HideMap.put("DIY表情", "11");
        HideMap.put("魔法表情", "9");
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.emoticonview.EmoticonPanelController", "getPanelDataList", List.class, new Class[0]));
    }
    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor hookWorker(){
        return param -> {
            List<String> mSetConf = HookEnv.Config.getList("QQCleaner", "HideEmoPanel", true);
            List l = (List) param.getResult();
            Iterator it = l.iterator();
            while (it.hasNext()) {
                Object item = it.next();
                int type = MField.GetField(item, "type");
                String typeStr = Integer.toString(type);
                if (mSetConf.contains(typeStr)) it.remove();
            }
        };
    }
    @VerController
    @UIClick
    public void uiClick(Context context){
        Activity act = Utils.getTopActivity();
        List<String> mSetConf = HookEnv.Config.getList("QQCleaner", "HideEmoPanel", true);
        ArrayList<String> showStrs = new ArrayList<>();
        for (String s : HideMap.keySet()) {
            showStrs.add(s);
        }
        String[] checkStr = showStrs.toArray(new String[0]);
        boolean[] checkStatus = new boolean[checkStr.length];
        for (int i = 0; i < checkStr.length; i++) {
            checkStatus[i] = mSetConf.contains(HideMap.get(checkStr[i]));
        }

        new AlertDialog.Builder(act, 3)
                .setMultiChoiceItems(checkStr, checkStatus, (dialog, which, isChecked) -> {

                }).setNeutralButton("保存", (dialog, which) -> {
                    ArrayList<String> setSave = new ArrayList<>();
                    for (int i = 0; i < checkStatus.length; i++) {
                        if (checkStatus[i]) {
                            String strIntType = HideMap.get(showStrs.get(i));
                            setSave.add(strIntType);
                        }
                    }
                    HookEnv.Config.setList("QQCleaner", "HideEmoPanel", setSave);
                }).show();
    }
}
