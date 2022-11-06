package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "Build_Common_Msg", itemType = XPItem.ITEM_Api)
public class Build_Common_Msg {
    CoreLoader.XPItemInfo info;

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getMethod(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("method", " MessageForWantGiftMsg.GIFT_SENDER_UIN  ", m -> true));
    }

    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    @ApiExecutor
    public Object build(int type) throws InvocationTargetException, IllegalAccessException {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", "a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                int.class
        });
        return CallMethod.invoke(null, type);
    }

    @VerController(targetVer = QQVersion.QQ_8_8_93, max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object build_8_8_93(int type) throws InvocationTargetException, IllegalAccessException {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", "d", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                int.class
        });
        return CallMethod.invoke(null, type);
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object build_8_9_0(int type) throws InvocationTargetException, IllegalAccessException {
        Method CallMethod = MMethod.FindMethod(info.scanResult.get("method").getDeclaringClass(), "d", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new Class[]{
                int.class
        });
        return CallMethod.invoke(null, type);
    }
}
