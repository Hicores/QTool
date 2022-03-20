package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.hicore.qtool.BuildConfig;
import com.hicore.HookItem;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;
/*
Hook注入QQ的设置界面添加一个选项
 */
@HookItem(isRunInAllProc = false,isDelayInit = false)
public class EntryHook extends BaseHookItem {
    private static final String TAG = "EntryHook";
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method m = getHookMethod();
        XPBridge.HookAfter(m,param -> {
            try{
                Activity act = (Activity) param.thisObject;
                ResUtils.StartInject(act);

                View item = MField.GetFirstField(act,MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"));
                ViewGroup mRoot = (ViewGroup) item.getParent();

                mRoot.addView(FormItemUtils.createMultiItem(act,"QTool",BuildConfig.VERSION_NAME,v->{
                    MainMenu.createActivity(act);
                }));
            }catch (Exception e){

            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }


    @Override
    public boolean check() {
        return getHookMethod() != null;
    }
    public Method getHookMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQSettingSettingActivity"),"doOnCreate",boolean.class,new Class[]{Bundle.class});
    }
}
