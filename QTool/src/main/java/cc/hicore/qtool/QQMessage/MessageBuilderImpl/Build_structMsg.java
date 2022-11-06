package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import android.util.Log;

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
import de.robv.android.xposed.XposedBridge;

@XPItem(itemType = XPItem.ITEM_Api, name = "Build_StructMsg")
public class Build_structMsg {
    CoreLoader.XPItemInfo info;

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object invoke(String xml) {
        try {
            Method BuildStructMsg = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.structmsg.TestStructMsg"), "a",
                    MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), new Class[]{String.class});
            return BuildStructMsg.invoke(null, xml);
        } catch (Throwable th) {
            XposedBridge.log(Log.getStackTraceString(th));
            return null;
        }
    }

    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void MethodScaner(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("method", "getStructMsgFromXmlBuffByStream", m -> ((Method) m).getParameterCount() == 1));
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public Object invoke_8890(String xml) throws InvocationTargetException, IllegalAccessException {
        return ((Method) info.scanResult.get("method")).invoke(null, xml);
    }
}
