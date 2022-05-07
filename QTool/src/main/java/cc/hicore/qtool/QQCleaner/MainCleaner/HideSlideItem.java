package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.app.AlertDialog;
import android.content.Context;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "侧滑净化",groupName = "主界面净化",targetID = 2,type = 2,id = "HideSlideItem")
public class HideSlideItem extends BaseHookItem implements BaseUiItem {
    static HashMap<String,String> cacheItemData = new HashMap<>();
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0],param -> {
            Object mArr = param.getResult();
            cacheItemData = new HashMap<>();
            List<String> HideSingle = HookEnv.Config.getList("Set","HideSlideItem",true);
            ArrayList saveArrays = new ArrayList();
            for (int i = 0;i< Array.getLength(mArr);i++){
                Object item = Array.get(mArr,i);
                String Signer = MField.GetField(item,"a",String.class);
                String Title = MField.GetField(MField.GetFirstField(item,MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean$Title")),"a",String.class);
                HideSlideItem.cacheItemData.put(Signer,Title);
                if (!HideSingle.contains(Signer))saveArrays.add(item);
            }
            Object newArr = Array.newInstance(MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean"),saveArrays.size());
            for (int i=0;i<saveArrays.size();i++){
                Array.set(newArr,i,saveArrays.get(i));
            }
            param.setResult(newArr);
        });

        XPBridge.HookBefore(m[1],param -> {
            List<String> HideSingle = HookEnv.Config.getList("Set","HideSlideItem",true);
            if (HideSingle.contains("d_vip_identity"))param.setResult(null);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        ArrayList<String> showText = new ArrayList<>();
        ArrayList<String> signleList = new ArrayList<>();
        List<String> HideSingle = HookEnv.Config.getList("Set","HideSlideItem",true);
        boolean[] check = new boolean[cacheItemData.size()];
        int j = 0;
        for (String Signer : cacheItemData.keySet()){
            showText.add(cacheItemData.get(Signer)+"("+Signer+")");
            if (HideSingle.contains(Signer))check[j] = true;
            j++;
            signleList.add(Signer);
        }
        String[] checkTitle = showText.toArray(new String[0]);

        new AlertDialog.Builder(context)
                .setMultiChoiceItems(checkTitle, check, (dialog, which, isChecked) -> {

                }).setNegativeButton("保存", (dialog, which) -> {
                    ArrayList<String> save = new ArrayList<>();
                    for (int i=0;i<check.length;i++){
                        if (check[i]){
                            save.add(signleList.get(i));
                        }
                    }
                    HookEnv.Config.setList("Set","HideSlideItem",save);
                }).show();
    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeMenuConfigBean");
        for (Method m1 : clz.getDeclaredMethods()){
            if (m1.getReturnType().isArray()){
                m[0] = m1;
                break;
            }
        }

        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQSettingMe"),"j",void.class,new Class[0]);
        return m;
    }
}
