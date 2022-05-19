package cc.hicore.qtool.ChatHook.ChatCracker;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;

import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import de.robv.android.xposed.XposedBridge;

public class UnlimitMulti extends BaseHookItem {
    static int aaaaa = 0;
    @Override
    public boolean startHook() throws Throwable {
        Method HookMethod = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.message.MultiMsgProxy"), "b",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing"),
                HashMap.class,int.class
        });


        /*
        Method hookMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", "a",
                void.class, new Class[]{
                        MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                        MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver"),
                        boolean.class
                });

         */


        XPBridge.HookBefore(HookMethod, param -> {
            XposedBridge.log("aaa:"+param.args[2]);
            param.args[2] = 0;
        });


        HookMethod = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.message.MultiMsgProxy"), "a",String.class,new Class[]{
                int.class,MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing")
        });

        XPBridge.HookBefore(HookMethod,param -> {
            String s = Log.getStackTraceString(new Throwable());
            if (s.contains(":1776")){
                param.args[0] = aaaaa++;
            }
        });
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public boolean check() {
        return false;
    }
}
