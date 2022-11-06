package cc.hicore.qtool.QQMessage.MessageImpl;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "MsgApi_AddMsg", itemType = XPItem.ITEM_Api)
public class MsgApi_AddMsg {
    @VerController
    @ApiExecutor
    public void onInvoke(Object msg) throws Exception {
        Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", null, void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                String.class, boolean.class, boolean.class, boolean.class, boolean.class
        });
        Object MessageFacade = MMethod.CallMethodNoParam(QQEnvUtils.getAppRuntime(), "getMessageFacade",
                MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
        InvokeMethod.invoke(MessageFacade, msg, QQEnvUtils.getCurrentUin(), false, false, false, true);
    }
}
