package cc.hicore.qtool.QQManager.ManagerImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPChecker;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "Group_Change_Name", itemType = XPItem.ITEM_Api)
public class Group_Change_Name {
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void change(String GroupUin, String UserUin, String name) throws Exception {
        Object mCallObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler"), new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")}, QQEnvUtils.getAppRuntime());
        Object TroopCardObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberCardInfo"), new Class[0], new Object[0]);
        MField.SetField(TroopCardObj, "name", name);
        MField.SetField(TroopCardObj, "troopuin", GroupUin);

        MField.SetField(TroopCardObj, "memberuin", UserUin);
        MField.SetField(TroopCardObj, "email", "");
        MField.SetField(TroopCardObj, "memo", "");
        MField.SetField(TroopCardObj, "tel", "");
        ArrayList mList = new ArrayList();
        ArrayList mList2 = new ArrayList();
        mList.add(TroopCardObj);
        mList2.add(1);
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler", null, void.class, new Class[]{
                String.class,
                ArrayList.class,
                ArrayList.class
        });
        CallMethod.invoke(mCallObj, GroupUin, mList, mList2);
    }

    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void change_new(String GroupUin, String UserUin, String name) throws Exception {
        Object mCallObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.j"), new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")}, QQEnvUtils.getAppRuntime());
        Object TroopCardObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberCardInfo"), new Class[0], new Object[0]);
        MField.SetField(TroopCardObj, "name", name);
        MField.SetField(TroopCardObj, "troopuin", GroupUin);

        MField.SetField(TroopCardObj, "memberuin", UserUin);
        MField.SetField(TroopCardObj, "email", "");
        MField.SetField(TroopCardObj, "memo", "");
        MField.SetField(TroopCardObj, "tel", "");
        ArrayList mList = new ArrayList();
        ArrayList mList2 = new ArrayList();
        mList.add(TroopCardObj);
        mList2.add(1);
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.j", null, void.class, new Class[]{
                String.class,
                ArrayList.class,
                ArrayList.class
        });
        CallMethod.invoke(mCallObj, GroupUin, mList, mList2);
    }

    @VerController
    @XPChecker
    public void check() {
        Object callMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler", null, void.class, new Class[]{
                String.class,
                ArrayList.class,
                ArrayList.class
        });
        Assert.notNull(callMethod, "callMethod is NULL");
    }
}
