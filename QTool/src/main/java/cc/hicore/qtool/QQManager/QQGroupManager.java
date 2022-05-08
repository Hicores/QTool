package cc.hicore.qtool.QQManager;

import android.text.TextUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;


public class QQGroupManager {
    public static void Group_Kick(String GroupUin, String UserUin, boolean isBlack) {
        try {
            Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"), QQEnvUtils.getAppRuntime());
            ArrayList<Long> KickList = new ArrayList<>();
            KickList.add(Long.parseLong(UserUin));
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler", "a", void.class, new Class[]{
                    long.class,
                    List.class,
                    boolean.class,
                    boolean.class
            });
            CallMethod.invoke(ManagerObject,
                    Long.parseLong(GroupUin), KickList, isBlack, false
            );
        } catch (Throwable th) {
            LogUtils.error("Group_Kick", th);
        }
    }
    public static void WaitForRefreshGroupMember(String GroupUin){

    }

    public static void Group_Kick(String GroupUin, String[] UserUin, boolean isBlack) {
        try {
            Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"), QQEnvUtils.getAppRuntime());
            ArrayList<Long> KickList = new ArrayList<>();
            for (String Uin : UserUin) KickList.add(Long.parseLong(Uin));
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler", "a", void.class, new Class[]{
                    long.class,
                    List.class,
                    boolean.class,
                    boolean.class
            });
            CallMethod.invoke(ManagerObject,
                    Long.parseLong(GroupUin), KickList, isBlack, false
            );
        } catch (Throwable th) {
            LogUtils.error("Group_Kick_All", th);
        }
    }

    public static void Group_Mute(String GroupUin, String UserUin, long time) {
        if (GroupUin.contains("&")) {
            String[] GuildCut = GroupUin.split("&");
            QQGuildManager.Guild_Mute(GuildCut[0], UserUin, time);
            return;
        }
        try {

            if (TextUtils.isEmpty(UserUin)) {
                if (time != 0 && time < 60)time = 268435455;
                Object TroopGagManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(), "getBusinessHandler",
                        MClass.loadClass("com.tencent.mobileqq.app.BusinessHandler"),
                        MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"), "TROOP_GAG_HANDLER"));
                MMethod.CallMethodParams(TroopGagManager, "a", void.class, GroupUin, time);
            } else {
                Object TroopGagManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(), "getManager",
                        MClass.loadClass("mqq.manager.Manager"),
                        MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"), "TROOP_GAG_MANAGER"));
                MMethod.CallMethodParams(TroopGagManager, "a", boolean.class, GroupUin, UserUin, time);
            }
        } catch (Exception e) {
            LogUtils.error("Group_Mute", e);
        }
    }

    public static void Group_Change_Title(String GroupUin, String UserUin, String title) {
        try {
            Object mProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("mqq.observer.BusinessObserver")}, (proxy, method, args) -> null);
            MMethod.CallMethod(null, MClass.loadClass("com.tencent.biz.troop.EditUniqueTitleActivity"), "a", void.class, new Class[]{
                            HookEnv.AppInterface.getClass(), String.class, String.class, String.class, MClass.loadClass("mqq.observer.BusinessObserver")
                    },
                    HookEnv.AppInterface, GroupUin, UserUin, title, mProxy);
        } catch (Exception e) {
            LogUtils.error("Group_Change_Title", e);
        }

    }

    public static void Group_Change_Name(String GroupUin, String UserUin, String name) {
        try {
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
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler", "a", void.class, new Class[]{
                    String.class,
                    ArrayList.class,
                    ArrayList.class
            });
            CallMethod.invoke(mCallObj, GroupUin, mList, mList2);
        } catch (Exception e) {
            LogUtils.error("Group_Change_Name", e);
        }
    }

    public static void Group_Exit(String GroupUin) {
        //Nothing Here
    }
}
