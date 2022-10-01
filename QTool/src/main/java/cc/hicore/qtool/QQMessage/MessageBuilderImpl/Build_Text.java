package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

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
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "Builder_Text",itemType = XPItem.ITEM_Api)
public class Build_Text {
    CoreLoader.XPItemInfo info;
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("method","createMsgRecordFromDB", m->true));
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object build(String GroupUin, String text) throws Exception {
        Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory", null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), new Class[]{
                MClass.loadClass("com.tencent.common.app.AppInterface"),
                String.class,
                String.class,
                String.class,
                int.class,
                byte.class,
                byte.class,
                short.class,
                String.class
        });

        return InvokeMethod.invoke(null, HookEnv.AppInterface, "", GroupUin, QQEnvUtils.getCurrentUin(), 1, (byte) 0, (byte) 0, (short) 0, text);
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object build_890(String GroupUin, String text) throws Exception {
        Method InvokeMethod = MMethod.FindMethod(info.scanResult.get("method").getDeclaringClass(), null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), new Class[]{
                MClass.loadClass("com.tencent.common.app.AppInterface"),
                String.class,
                String.class,
                String.class,
                int.class,
                byte.class,
                byte.class,
                short.class,
                String.class
        });
        return InvokeMethod.invoke(null, HookEnv.AppInterface, "", GroupUin, QQEnvUtils.getCurrentUin(), 1, (byte) 0, (byte) 0, (short) 0, text);
    }
}
