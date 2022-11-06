package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;

@XPItem(name = "Build_Mix", itemType = XPItem.ITEM_Api)
public class Build_Mix {
    CoreLoader.XPItemInfo info;

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void MethodScaner(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("method", " MessageForWantGiftMsg.GIFT_SENDER_UIN  ", m -> true));
    }

    @ApiExecutor
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public Object build_890(Object session, ArrayList msgElems) throws Exception {
        Method m = null;
        Class<?> clz = info.scanResult.get("method").getDeclaringClass();
        for (Method ma : clz.getDeclaredMethods()) {
            if (ma.getReturnType().equals(MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"))) {
                m = ma;
                break;
            }
        }

        Object MixMessageRecord;
        if (QQSessionUtils.getSessionID(session) == 10014) {
            MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getChannelID(session), QQEnvUtils.getCurrentUin(), 10014);
        } else {
            MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getGroupUin(session), QQEnvUtils.getCurrentUin(), 1);
        }
        MField.SetField(MixMessageRecord, "msgElemList", msgElems);
        MixMessageRecord = MMethod.CallMethodNoParam(MixMessageRecord, "rebuildMixedMsg", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"));
        return MixMessageRecord;
    }

    @ApiExecutor
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    public Object build(Object session, ArrayList msgElems) throws Exception {
        Method m = null;
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory");
        for (Method ma : clz.getDeclaredMethods()) {
            if (ma.getReturnType().equals(MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"))) {
                m = ma;
                break;
            }
        }

        Object MixMessageRecord;
        if (QQSessionUtils.getSessionID(session) == 10014) {
            MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getChannelID(session), QQEnvUtils.getCurrentUin(), 10014);
        } else {
            MixMessageRecord = m.invoke(null, HookEnv.AppInterface, QQSessionUtils.getGroupUin(session), QQEnvUtils.getCurrentUin(), 1);
        }
        MField.SetField(MixMessageRecord, "msgElemList", msgElems);
        MixMessageRecord = MMethod.CallMethodNoParam(MixMessageRecord, "rebuildMixedMsg", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"));
        return MixMessageRecord;
    }
}
