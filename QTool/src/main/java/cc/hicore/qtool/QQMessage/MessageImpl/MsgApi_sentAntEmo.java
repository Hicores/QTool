package cc.hicore.qtool.QQMessage.MessageImpl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "MsgApi_sentAntEmo",itemType = XPItem.ITEM_Api)
public class MsgApi_sentAntEmo {
    @VerController
    @ApiExecutor
    public void send_8820(Object _Session, int ID) throws InvocationTargetException, IllegalAccessException {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack", "sendAniSticker",
                boolean.class, new Class[]{int.class, MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"), int.class}
        );
        m.invoke(null, ID, _Session, 0);
    }

}
