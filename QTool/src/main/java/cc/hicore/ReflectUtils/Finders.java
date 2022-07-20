package cc.hicore.ReflectUtils;

import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.qtool.XposedInit.HostInfo;

public class Finders {
    public static void BaseChatPieInit(MethodContainer container){
        Method m;
        if (HostInfo.getVerCode() > 6440) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "s", void.class, new Class[0]);
        } else if (HostInfo.getVerCode() > 5870) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "r", void.class, new Class[0]);
        } else if (HostInfo.getVerCode() > 5570) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "q", void.class, new Class[0]);
        } else {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "f", void.class, new Class[0]);
        }
        container.addMethod("basechatpie_init",m);
    }
    public static void BaseChatPieInit_8893(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("basechatpie_init","AIO_doOnCreate_initUI", m ->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie")));
    }
    public static void AIOMessageListAdapter_getView(MethodContainer container){
        container.addMethod("onAIOGetView",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        }));
    }
    public static void AIOMessageListAdapter_getView_890(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("onAIOGetView","AIO_ChatAdapter_getView",m -> m.getDeclaringClass().getName().startsWith("com.tencent.mobileqq.activity.aio")));
    }
    public static void onTroopMessage(MethodContainer container){
        container.addMethod("troopMsgProxy",MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy",null, void.class,new Class[]{
                String.class,
                int.class,
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                boolean.class
        }));
    }
    public static void onTroopMessageNew(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("troopMsgProxy","insertToList ",m -> m.getDeclaringClass().getName().startsWith("com.tencent.imcore.message")));
    }
}
