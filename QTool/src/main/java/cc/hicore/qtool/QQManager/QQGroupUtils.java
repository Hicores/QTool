package cc.hicore.qtool.QQManager;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QQGroupUtils {
    public static String Group_Get_Name(String GroupUin){
        GroupInfo info = Group_Get_Info(GroupUin);
        return info == null?GroupUin : info.Name;
    }
    public static String Group_Get_Member_Name(String GroupUin,String UserUin){
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.utils.ContactUtils","b",String.class,new Class[]{
                    MClass.loadClass("com.tencent.common.app.AppInterface"),
                    String.class,
                    String.class
            });
            return (String) CallMethod.invoke(null, QQEnvUtils.getAppRuntime(),GroupUin,UserUin);
        }catch (Exception e){
            return UserUin;
        }

    }
    public static String Group_Get_Member_Title(String GroupUin,String UserUin){
        GroupMemberInfo info = Group_Get_Member_Info(GroupUin,UserUin);
        return info == null ? "" : info.Title;
    }
    public static GroupMemberInfo Group_Get_Member_Info(String GroupUin,String UserUin){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));
            Object GroupMemberInfoR = MMethod.CallMethodParams(TroopManager,"g", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberInfo"),GroupUin,UserUin);
            GroupInfo gInfo = Group_Get_Info(GroupUin);

            GroupMemberInfo NewItem = new GroupMemberInfo();
            NewItem.Uin = MField.GetField(GroupMemberInfoR,"memberuin");
            NewItem.Nick = MField.GetField(GroupMemberInfoR,"troopnick");
            NewItem.Name = MField.GetField(GroupMemberInfoR,"friendnick");
            NewItem.join_time = MField.GetField(GroupMemberInfoR,"join_time");
            NewItem.last_active = MField.GetField(GroupMemberInfoR,"last_active_time");
            NewItem.isCreator = gInfo.Creator.equals(NewItem.Uin);
            NewItem.isAdmin = gInfo.adminList.contains(NewItem.Uin);
            NewItem.Title = MField.GetField(GroupMemberInfoR,"mUniqueTitle");
            return NewItem;
        }catch (Exception e){
            LogUtils.error("Group_Get_Member_Info",e);
            return null;
        }
    }
    public static ArrayList<GroupInfo> Group_Get_List(){
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

            ArrayList<GroupInfo> NewList = new ArrayList<>();
            for (Object item : rawList){
                GroupInfo NewItem = new GroupInfo();
                NewItem.Uin = MField.GetField(item,"troopuin");
                NewItem.Name = MField.GetField(item,"troopname");
                NewItem.Creator = MField.GetField(item,"troopowneruin");
                String admins = MField.GetField(item,"Administrator");
                NewItem.adminList = new ArrayList<>(Arrays.asList(admins.split("\\|")));
                NewList.add(NewItem);
            }
            return NewList;
        }catch (Exception e){
            LogUtils.error("Group_Get_List",e);
            return null;
        }
    }
    public static GroupInfo Group_Get_Info(String GroupUin){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));
            Object GroupInfoR = MMethod.CallMethod(TroopManager,TroopManager.getClass(),"g", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopInfo"),new Class[]{String.class},GroupUin);
            GroupInfo NewItem = new GroupInfo();
            NewItem.Uin = MField.GetField(GroupInfoR,"troopuin");
            NewItem.Name = MField.GetField(GroupInfoR,"troopname");
            NewItem.Creator = MField.GetField(GroupInfoR,"troopowneruin");
            String admins = MField.GetField(GroupInfoR,"Administrator");
            NewItem.adminList = new ArrayList<>(Arrays.asList(admins.split("\\|")));
            return NewItem;
        }catch (Exception e){
            LogUtils.error("Group_Get_Info",e);
            return null;
        }

    }
    public static ArrayList<GroupMemberInfo> Group_Get_Member_List(String GroupUin){
        try{
            Object TroopManager = MMethod.CallMethodSingle(QQEnvUtils.getAppRuntime(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    MField.GetStaticField(MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER"));
            ArrayList<?> MyList;
            try{
                MyList = MMethod.CallMethodSingle(TroopManager,"w", List.class,GroupUin);
            }catch (Exception e){
                MyList = MMethod.CallMethodSingle(TroopManager,"b", List.class,GroupUin);
            }

            ArrayList<GroupMemberInfo> Infos = new ArrayList<>();
            GroupInfo gInfo = Group_Get_Info(GroupUin);
            for (Object item : MyList){
                GroupMemberInfo NewItem = new GroupMemberInfo();
                NewItem.Uin = MField.GetField(item,"memberuin");
                NewItem.Nick = MField.GetField(item,"troopnick");
                NewItem.Name = MField.GetField(item,"friendnick");
                NewItem.join_time = MField.GetField(item,"join_time");
                NewItem.last_active = MField.GetField(item,"last_active_time");
                NewItem.isCreator = gInfo.Creator.equals(NewItem.Uin);
                NewItem.isAdmin = gInfo.adminList.contains(NewItem.Uin);
                NewItem.Title = MField.GetField(item,"mUniqueTitle");
                Infos.add(NewItem);
            }
            return Infos;
        }
        catch (Throwable e)
        {
            LogUtils.error("GetGroupMemberList",e);
            return null;
        }
    }
    public static ArrayList<MuteList> Group_Get_Mute_List(String GroupUin){
        return null;
    }


    public static class MuteList{
        public String Uin;
        public long delayTime;
    }

    public static class GroupInfo{
        public String Creator;
        public ArrayList<String> adminList;
        public String Name;
        public String Uin;
        public Object source;
    }
    public static class GroupMemberInfo{
        public String Uin;
        public String Name;
        public String Nick;
        public String Title;
        public boolean isAdmin;
        public boolean isCreator;
        public int level;
        public long join_time;
        public long last_active;
        public Object source;
    }
}
