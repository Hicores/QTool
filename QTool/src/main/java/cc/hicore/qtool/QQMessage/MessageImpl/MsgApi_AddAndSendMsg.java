package cc.hicore.qtool.QQMessage.MessageImpl;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(itemType = XPItem.ITEM_Api, name = "MsgApi_AddAndSendMsg")
public class MsgApi_AddAndSendMsg {
    @VerController
    @ApiExecutor
    public void onInvoke(Object MessageRecord) throws Exception {
        Object MessageFacade = MMethod.CallMethodNoParam(QQEnvUtils.getAppRuntime(), "getMessageFacade",
                MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"));
        Method mMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade", null, void.class, new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver"), boolean.class
        });
        mMethod.invoke(MessageFacade, MessageRecord, null, false);
    }
}
