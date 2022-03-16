package com.hicore.qtool.EmoHelper.Hooker;

import android.util.Log;

import com.hicore.HookItem;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;

import bsh.classpath.BshClassPath;

@HookItem(isDelayInit = true,isRunInAllProc = false)
public class HookInjectEmoTabView extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.BasePanelView","convertEmoticonTabItem", MClass.loadClass("com.tencent.mobileqq.emoticonview.EmoticonTabAdapter$EmoticonTabItem"),
                new Class[]{MClass.loadClass("com.tencent.mobileqq.emoticonview.EmotionPanelInfo")});

        XPBridge.HookAfter(m,param -> {
            Object Info = param.getResult();
            LogUtils.debug("HookInjectEmoTabView", MField.GetField(Info,"description"));
            LogUtils.debug("HookInjectEmoTabView", Log.getStackTraceString(new Throwable()));
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return false;
    }

}
