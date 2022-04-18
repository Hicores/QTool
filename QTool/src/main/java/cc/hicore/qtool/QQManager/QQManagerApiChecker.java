package cc.hicore.qtool.QQManager;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;

@HookItem(isRunInAllProc = false,isDelayInit = false)
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
        if (!getFriendName())builder.append("FRIENDS_MANAGER\n");
        if (!sendLike())builder.append("sendLike\n");
        if (!FriendManager())builder.append("FriendManager\n");
        if (!Group_Kick())builder.append("Group_Kick\n");
        if (!Group_Mute())builder.append("Group_Mute\n");
        if (!Group_Change_Title())builder.append("Group_Change_Title\n");
        if (!Group_Change_Name())builder.append("Group_Change_Name\n");
        if (!Group_Get_Member_Info())builder.append("Group_Get_Member_Info\n");
        if (!Group_Get_List())builder.append("Group_Get_List\n");
        if (!Group_Get_Info())builder.append("Group_Get_Info\n");
        if (!Group_Get_Member_List())builder.append("Group_Get_Member_List\n");
        if (!Guild_Revoke())builder.append("Guild_Revoke\n");

        return builder.length() == 0;
    }
    private static boolean Guild_Revoke(){
        try{
            Object RevokeHelper = QQEnvUtils.getBusinessHandler("com.tencent.mobileqq.guild.message.api.impl.GuildRevokeMessageHandler");
            Method m = MMethod.FindMethod(RevokeHelper.getClass(),"a",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")});
            return RevokeHelper != null && m != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Get_Member_List(){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));
            Method m = MMethod.FindMethod(TroopManager.getClass(),"w", List.class,new Class[]{String.class});
            if (m == null) m = MMethod.FindMethod(TroopManager.getClass(),"b", List.class,new Class[]{String.class});
            return m != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Get_Info(){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));
            Method m = MMethod.FindMethod(TroopManager.getClass(),"g", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopInfo"),new Class[]{String.class});
            return m != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Get_List(){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));
            ArrayList<?> rawList;
            try{
                rawList =  MMethod.CallMethodNoParam(TroopManager,"g", ArrayList.class);
            }catch (Exception e){
                rawList =  MMethod.CallMethodNoParam(TroopManager,"a", ArrayList.class);
            }
            return rawList != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Get_Member_Info(){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));

            Method m = MMethod.FindMethod(TroopManager.getClass(),"g", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberInfo"),
                    new Class[]{String.class,String.class});
            return TroopManager != null && m != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Change_Name(){
        try{
            Object mCallObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler"),new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")}, QQEnvUtils.getAppRuntime());
            Object TroopCardObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberCardInfo"),new Class[0],new Object[0]);
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler","a",void.class,new Class[]{
                    String.class,
                    ArrayList.class,
                    ArrayList.class
            });

            return mCallObj != null && TroopCardObj != null && CallMethod != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Change_Title(){
        try {
            Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.biz.troop.EditUniqueTitleActivity"),"a",void.class,new Class[]{
                    HookEnv.AppInterface.getClass(),String.class,String.class,String.class,MClass.loadClass("mqq.observer.BusinessObserver")
            });
            return m != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Mute(){
        try{
            Method m1 = MMethod.FindMethod("com.tencent.mobileqq.troop.troopgag.api.impl.TroopGagHandler",
                    "a",void.class,new Class[]{String.class,long.class});
            Method m2 = MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr",
                    "a",boolean.class,new Class[]{String.class,String.class,long.class});
            return m1 != null && m2 != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean Group_Kick(){
        try{
            Object ManagerObject = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"), QQEnvUtils.getAppRuntime());
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler","a",void.class,new Class[]{
                    long.class,
                    List.class,
                    boolean.class,
                    boolean.class
            });
            return ManagerObject != null && CallMethod != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean FriendManager(){
        try{
            Object FriendsManager = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl"),"getService",
                    MClass.loadClass("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl"),new Class[]{MClass.loadClass("mqq.app.AppRuntime")},QQEnvUtils.getAppRuntime()
            );
            return FriendsManager != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean sendLike(){
        try{
            Method m = MMethod.FindMethod("com.tencent.mobileqq.app.CardHandler","a",void.class,new Class[]{
                    long.class,long.class,byte[].class,int.class,int.class,int.class
            });
            Object CardHandler = MMethod.CallMethodSingle(HookEnv.AppInterface,"getBusinessHandler",
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessHandler"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"),"CARD_HANLDER"));

            return m != null && CardHandler != null;
        }catch (Exception e){
            return false;
        }
    }
    private static boolean getFriendName(){
        try{
            Object FriendManager = MMethod.CallMethod(HookEnv.AppInterface,"getManager",
                    XposedHelpers.findClass("mqq.manager.Manager",HookEnv.mLoader),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null, MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"FRIENDS_MANAGER",int.class)}
            );
            Object mService = MField.GetFirstField(FriendManager,FriendManager.getClass(),MClass.loadClass("com.tencent.mobileqq.friend.api.IFriendDataService"));
            return mService != null;
        }catch (Exception e){
            return false;
        }
    }

}
