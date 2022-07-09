package cc.hicore.qtool.QQManager.ManagerImpl;

import android.text.TextUtils;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPChecker;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "Group_Mute",itemType = XPItem.ITEM_Api)
public class Group_Mute {
    @VerController
    @ApiExecutor
    public void mute(String GroupUin, String UserUin, long time) throws Exception {
        if (TextUtils.isEmpty(UserUin)) {
            if (time != 0 && time < 60)time = 268435455;
            Object TroopGagManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(), "getBusinessHandler",
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessHandler"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"), "TROOP_GAG_HANDLER"));
            MMethod.CallMethodParams(TroopGagManager, null, void.class, GroupUin, time);
        } else {
            Object TroopGagManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(), "getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"), "TROOP_GAG_MANAGER"));
            MMethod.CallMethodParams(TroopGagManager, null, boolean.class, GroupUin, UserUin, time);
        }
    }
    @VerController
    @XPChecker
    public void check() throws Exception {
        Object TroopGagManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(), "getBusinessHandler",
                MClass.loadClass("com.tencent.mobileqq.app.BusinessHandler"),
                MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"), "TROOP_GAG_HANDLER"));
        Assert.notNull(TroopGagManager,"TroopGagManager is NULL,1");

        TroopGagManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(), "getManager",
                MClass.loadClass("mqq.manager.Manager"),
                MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"), "TROOP_GAG_MANAGER"));
        Assert.notNull(TroopGagManager,"TroopGagManager is NULL,2");

        Method m = MMethod.FindMethod(TroopGagManager.getClass(),null,void.class,new Class[]{
                String.class,long.class
        });
        Assert.notNull(m,"Can't find mute_all method.");

        m = MMethod.FindMethod(TroopGagManager.getClass(),null,boolean.class,new Class[]{
                String.class,String.class,long.class
        });
        Assert.notNull(m,"Can't find mute_method method.");
    }
}
