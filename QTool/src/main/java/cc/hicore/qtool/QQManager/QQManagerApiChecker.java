package cc.hicore.qtool.QQManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.MethodFinder;
import de.robv.android.xposed.XposedHelpers;

@HookItem(isRunInAllProc = false, isDelayInit = false)
public class QQManagerApiChecker extends BaseHookItem {
    private static final String TAG = "QQManagerApiChecker";
    private static StringBuilder builder = new StringBuilder();

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public String getErrorInfo() {
        return builder.toString();
    }

    @Override
    public boolean check() {
        builder.setLength(0);
        if (!getFriendName()) builder.append("FRIENDS_MANAGER\n");
        if (!sendLike()) builder.append("sendLike\n");
        if (!FriendManager()) builder.append("FriendManager\n");
        if (!Group_Kick()) builder.append("Group_Kick\n");
        if (!Group_Mute()) builder.append("Group_Mute\n");
        if (!Group_Change_Title()) builder.append("Group_Change_Title\n");
        if (!Group_Change_Name()) builder.append("Group_Change_Name\n");
        if (!Group_Get_Member_Info()) builder.append("Group_Get_Member_Info\n");
        if (!Group_Get_List()) builder.append("Group_Get_List\n");
        if (!Group_Get_Info()) builder.append("Group_Get_Info\n");
        if (!Group_Get_Member_List()) builder.append("Group_Get_Member_List\n");
        if (!Guild_Revoke()) builder.append("Guild_Revoke\n");
        if (!Get_Group_Name_By_Contact())builder.append("Get_Group_Name_By_Contact\n");

        return builder.length() == 0;
    }

    private static boolean Guild_Revoke() {
        try {
            Object RevokeHelper = QQEnvUtils.getBusinessHandler("com.tencent.mobileqq.guild.message.api.impl.GuildRevokeMessageHandler");
            Method m = MMethod.FindMethod(RevokeHelper.getClass(), null, void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")});
            return RevokeHelper != null && m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Get_Member_List() {
        try {
            Object TroopManager = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopMemberInfoService"));

            Method m = MMethod.FindMethod(TroopManager.getClass(), "getAllTroopMembers", List.class, new Class[]{String.class});
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Get_Group_Name_By_Contact(){
        Method m = MethodFinder.findMethodFromCache("get_group_name_by_contact");
        if (m == null){
            MethodFinder.NeedReportToFindMethod("get_group_name_by_contact","msgApi","getTroopDisplayName()",ma -> ma.getDeclaringClass().getName().equals("com.tencent.mobileqq.utils.ContactUtils"));
        }
        return m != null;
    }

    private static boolean Group_Get_Info() {
        try {
            Object TroopInfoServer = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopInfoService"));
            Method m = MMethod.FindMethod(TroopInfoServer.getClass(),"getTroopInfo",MClass.loadClass("com.tencent.mobileqq.data.troop.TroopInfo"),new Class[]{String.class});
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Get_List() {
        try {
            Object TroopInfoServer = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopInfoService"));
            ArrayList<?> rawList = MMethod.CallMethodNoParam(TroopInfoServer,"getUiTroopListWithoutBlockedTroop",ArrayList.class);
            return rawList != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Get_Member_Info() {
        try {
            Object TroopManager = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopMemberInfoService"));

            Method m = MMethod.FindMethod(TroopManager.getClass(), "getTroopMember", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberInfo"),
                    new Class[]{String.class, String.class});
            return TroopManager != null && m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Change_Name() {
        try {
            Object mCallObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler"), new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")}, QQEnvUtils.getAppRuntime());
            Object TroopCardObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberCardInfo"), new Class[0], new Object[0]);
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler", null, void.class, new Class[]{
                    String.class,
                    ArrayList.class,
                    ArrayList.class
            });

            return mCallObj != null && TroopCardObj != null && CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Change_Title() {
        try {
            Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.biz.troop.EditUniqueTitleActivity"), null, void.class, new Class[]{
                    Classes.QQAppinterFace(), String.class, String.class, String.class, MClass.loadClass("mqq.observer.BusinessObserver")
            });
            return m != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Mute() {
        try {
            Method m1 = MMethod.FindMethod("com.tencent.mobileqq.troop.troopgag.api.impl.TroopGagHandler",
                    null, void.class, new Class[]{String.class, long.class});
            Method m2 = MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr",
                    null, boolean.class, new Class[]{String.class, String.class, long.class});
            return m1 != null && m2 != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean Group_Kick() {
        try {
            Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"), QQEnvUtils.getAppRuntime());
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler", null, void.class, new Class[]{
                    long.class,
                    List.class,
                    boolean.class,
                    boolean.class
            });
            return ManagerObject != null && CallMethod != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean FriendManager() {
        try {
            Object FriendsManager = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl"), "getService",
                    MClass.loadClass("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl"), new Class[]{MClass.loadClass("mqq.app.AppRuntime")}, QQEnvUtils.getAppRuntime()
            );
            return FriendsManager != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendLike() {
        try {
            Method m = MMethod.FindMethod("com.tencent.mobileqq.app.CardHandler", null, void.class, new Class[]{
                    long.class, long.class, byte[].class, int.class, int.class, int.class
            });
            Object CardHandler = MMethod.CallMethodSingle(HookEnv.AppInterface, "getBusinessHandler",
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessHandler"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"), "CARD_HANLDER"));

            return m != null && CardHandler != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean getFriendName() {
        try {
            Object FriendManager = MMethod.CallMethod(HookEnv.AppInterface, "getManager",
                    XposedHelpers.findClass("mqq.manager.Manager", HookEnv.mLoader),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null, MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"), "FRIENDS_MANAGER", int.class)}
            );
            Object mService = MField.GetFirstField(FriendManager, FriendManager.getClass(), MClass.loadClass("com.tencent.mobileqq.friend.api.IFriendDataService"));
            return mService != null;
        } catch (Exception e) {
            return false;
        }
    }

}
