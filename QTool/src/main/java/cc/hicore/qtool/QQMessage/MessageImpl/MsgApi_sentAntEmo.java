package cc.hicore.qtool.QQMessage.MessageImpl;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "MsgApi_sentAntEmo",itemType = XPItem.ITEM_Api)
public class MsgApi_sentAntEmo {
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void send_8820(Object _Session, int ID) throws InvocationTargetException, IllegalAccessException {
        Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack", "sendAniSticker",
                boolean.class, new Class[]{int.class, Classes.BaseSessionInfo(), int.class}
        );
        m.invoke(null, ID, _Session, 0);
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void send_890(Object _Session, int ID) throws InvocationTargetException, IllegalAccessException {
        Method m = MMethod.FindMethodByName(MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack"), "sendAniSticker");
        m.invoke(null, ID, _Session, 0);
    }

}
