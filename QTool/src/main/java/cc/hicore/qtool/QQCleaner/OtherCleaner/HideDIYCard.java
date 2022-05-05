package cc.hicore.qtool.QQCleaner.OtherCleaner;

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
@UIItem(name = "屏蔽DIY名片",targetID = 2,groupName = "其他净化",type = 1,id = "HideDIYCard")
public class HideDIYCard extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            if (IsEnable){
                MField.SetField(param.args[0],"lCurrentStyleId",(long)0);
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
        if (IsCheck) HookLoader.CallHookStart(HideDIYCard.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.profilecard.processor.TempProfileBusinessProcessor","updateCardTemplate", void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.Card"),
                String.class,
                MClass.loadClass("SummaryCardTaf.SSummaryCardRsp")
        });
    }

}
