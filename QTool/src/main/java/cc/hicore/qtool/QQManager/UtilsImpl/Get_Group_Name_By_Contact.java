package cc.hicore.qtool.QQManager.UtilsImpl;

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
import cc.hicore.qtool.XposedInit.HostInfo;

@XPItem(name = "get_Group_Name_By_Contact", itemType = XPItem.ITEM_Api)
public class Get_Group_Name_By_Contact {
    CoreLoader.XPItemInfo info;

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void methodFinder(MethodContainer container) {
        container.addMethod(MethodFinderBuilder.newFinderByString("invokeMethod", "getNewTroopNameOrTroopName()", m -> true));
    }

    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public String GetTroopNameByContact(String GroupUin) throws Exception {
        return MMethod.CallStaticMethod(MClass.loadClass("com.tencent.mobileqq.utils.ContactUtils"),
                HostInfo.getVerCode() > 8000 ? "W" : "a", String.class, HookEnv.AppInterface, GroupUin, true);
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public String GetTroopNameByContact_New(String GroupUin) throws Exception {
        return (String) ((Method) info.scanResult.get("invokeMethod")).invoke(null, HookEnv.AppInterface, GroupUin, true);
    }
}
