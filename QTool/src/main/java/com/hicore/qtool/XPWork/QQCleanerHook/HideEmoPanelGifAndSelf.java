package com.hicore.qtool.XPWork.QQCleanerHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.bumptech.glide.util.Util;
import com.hicore.HookItem;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.UIItem;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


@UIItem(itemType = 2,itemName = "隐藏表情面板图标",mainItemID = 2,ID = "HideGIFAndSelfPicPanel")
@HookItem(isRunInAllProc = false,isDelayInit = true)
public class HideEmoPanelGifAndSelf extends BaseHookItem implements BaseUiItem {
    static HashMap<String,String> HideMap = new HashMap<>();
    {
        HideMap.put("加号","13");
        HideMap.put("自带表情","7");
        HideMap.put("收藏表情","4");
        HideMap.put("热门表情","12");
        HideMap.put("厘米秀","15");
        HideMap.put("超级QQ秀","17");
        HideMap.put("DIY表情","11");
        HideMap.put("魔法表情","9");
    }
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            List<String> mSetConf = HookEnv.Config.getList("QQCleaner","HideEmoPanel",true);

            List l = (List)param.getResult();
            Iterator it = l.iterator();
            while (it.hasNext()){
                Object item = it.next();
                int type = MField.GetField(item,"type");
                String typeStr = Integer.toString(type);
                if ( mSetConf.contains(typeStr))it.remove();
            }

        });
        return true;
    }
    private Method getMethod(){
        Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.EmoticonPanelController","getPanelDataList", List.class,new Class[0]);
        return m;
    }
    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick() {
        Activity act = Utils.getTopActivity();
        List<String> mSetConf = HookEnv.Config.getList("QQCleaner","HideEmoPanel",true);
        ArrayList<String> showStrs = new ArrayList<>();
        for(String s : HideMap.keySet()){
            showStrs.add(s);
        }
        String[] checkStr = showStrs.toArray(new String[0]);
        boolean[] checkStatus = new boolean[checkStr.length];
        for (int i=0;i<checkStr.length;i++){
            checkStatus[i] = mSetConf.contains(HideMap.get(checkStr[i]));
        }

        new AlertDialog.Builder(act,3)
                .setMultiChoiceItems(checkStr, checkStatus, (dialog, which, isChecked) -> {

                }).setNeutralButton("保存", (dialog, which) -> {
                    ArrayList<String> setSave = new ArrayList<>();
                    for(int i=0;i<checkStatus.length;i++){
                        if (checkStatus[i]){
                            String strIntType = HideMap.get(showStrs.get(i));
                            setSave.add(strIntType);
                        }
                    }
                    HookEnv.Config.setList("QQCleaner","HideEmoPanel",setSave);
                }).show();
    }
}
