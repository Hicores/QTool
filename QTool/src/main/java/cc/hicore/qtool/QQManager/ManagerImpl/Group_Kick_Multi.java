package cc.hicore.qtool.QQManager.ManagerImpl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPChecker;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(name = "Manager_Group_Kick_Multi",itemType = XPItem.ITEM_Api)
public class Group_Kick_Multi {
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void kick_all(String GroupUin, String[] UserUin, boolean isBlack)throws Exception{
        Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"), QQEnvUtils.getAppRuntime());
        ArrayList<Long> KickList = new ArrayList<>();
        for (String Uin : UserUin) KickList.add(Long.parseLong(Uin));
        Method callMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler", null, void.class, new Class[]{
                long.class,
                List.class,
                boolean.class,
                boolean.class
        });
        callMethod.invoke(ManagerObject,
                Long.parseLong(GroupUin), KickList, isBlack, false
        );
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @ApiExecutor
    public void kick_all_890(String GroupUin, String[] UserUin, boolean isBlack)throws Exception{
        Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.l"), QQEnvUtils.getAppRuntime());
        ArrayList<Long> KickList = new ArrayList<>();
        for (String Uin : UserUin) KickList.add(Long.parseLong(Uin));
        Method callMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.l", null, void.class, new Class[]{
                long.class,
                List.class,
                boolean.class,
                boolean.class
        });
        callMethod.invoke(ManagerObject,
                Long.parseLong(GroupUin), KickList, isBlack, false
        );
    }
    @VerController
    @XPChecker
    public void check()throws Exception{
        Object runTime = QQEnvUtils.getAppRuntime();
        Assert.notNull(runTime,"AppRuntime is NULL");
        Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"), QQEnvUtils.getAppRuntime());
        Assert.notNull(ManagerObject,"ManagerObject is NULL");
        Method callMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler", null, void.class, new Class[]{
                long.class,
                List.class,
                boolean.class,
                boolean.class
        });
        Assert.notNull(callMethod,"callMethod is NULL");
    }
}
