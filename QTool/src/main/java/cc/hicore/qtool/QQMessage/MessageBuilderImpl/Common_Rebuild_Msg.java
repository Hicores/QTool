package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

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

@XPItem(name = "Common_Rebuild_Msg", itemType = XPItem.ITEM_Api)
public class Common_Rebuild_Msg {
    CoreLoader.XPItemInfo info;

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getMethod(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("method", " MessageForWantGiftMsg.GIFT_SENDER_UIN  ", m -> true));
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object build(Object record) throws Exception {
        return MMethod.CallStaticMethod(MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"), null, MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), record);
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object build_890(Object record) throws Exception {
        return MMethod.CallStaticMethod(info.scanResult.get("method").getDeclaringClass(), null, MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), record);
    }
}
