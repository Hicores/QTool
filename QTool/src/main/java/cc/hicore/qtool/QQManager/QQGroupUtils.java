package cc.hicore.qtool.QQManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;

public class QQGroupUtils {
    public static String Group_Get_Name(String GroupUin) {
        GroupInfo info = Group_Get_Info(GroupUin);
        return info == null ? GroupUin : info.Name;
    }

    public static String Group_Get_Member_Name(String GroupUin, String UserUin) {
        try {
            Object runTimeService = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopMemberNameService"));
            return MMethod.CallMethodParams(runTimeService,"getTroopMemberName",String.class,GroupUin,UserUin);
        } catch (Exception e) {
            return UserUin;
        }

    }

    public static String Group_Get_Member_Title(String GroupUin, String UserUin) {
        GroupMemberInfo info = Group_Get_Member_Info(GroupUin, UserUin);
        return info == null ? "" : info.Title;
    }

    public static GroupMemberInfo Group_Get_Member_Info(String GroupUin, String UserUin) {
        try {
            Object TroopManager = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopMemberInfoService"));
            Object GroupMemberInfoR = MMethod.CallMethodParams(TroopManager, "getTroopMember", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberInfo"), GroupUin, UserUin);
            GroupInfo gInfo = Group_Get_Info(GroupUin);

            GroupMemberInfo NewItem = new GroupMemberInfo();
            NewItem.Uin = MField.GetField(GroupMemberInfoR, "memberuin");
            NewItem.Nick = MField.GetField(GroupMemberInfoR, "troopnick");
            NewItem.Name = MField.GetField(GroupMemberInfoR, "friendnick");
            NewItem.join_time = MField.GetField(GroupMemberInfoR, "join_time");
            NewItem.last_active = MField.GetField(GroupMemberInfoR, "last_active_time");
            NewItem.isCreator = gInfo.Creator.equals(NewItem.Uin);
            NewItem.isAdmin = gInfo.adminList.contains(NewItem.Uin);
            NewItem.level = MField.GetField(GroupMemberInfoR,"newRealLevel",int.class);
            NewItem.Title = MField.GetField(GroupMemberInfoR, "mUniqueTitle");
            return NewItem;
        } catch (Exception e) {
            LogUtils.error("Group_Get_Member_Info", e);
            return null;
        }
    }
    public static boolean Is_In_Group(String GroupUin,String UserUin){
        GroupMemberInfo memberInfo = Group_Get_Member_Info(GroupUin,UserUin);
        return memberInfo != null;
    }

    public static ArrayList<GroupInfo> Group_Get_List() {
        try {
            Object TroopInfoServer = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopInfoService"));
            ArrayList<?> rawList = MMethod.CallMethodNoParam(TroopInfoServer,"getUiTroopListWithoutBlockedTroop",ArrayList.class);

            ArrayList<GroupInfo> NewList = new ArrayList<>();
            for (Object item : rawList) {
                GroupInfo NewItem = new GroupInfo();
                NewItem.Uin = MField.GetField(item, "troopuin");
                NewItem.Name = MField.GetField(item, "troopname");
                NewItem.Creator = MField.GetField(item, "troopowneruin");
                String admins = MField.GetField(item, "Administrator");
                NewItem.adminList = new ArrayList<>(Arrays.asList(admins.split("\\|")));
                NewList.add(NewItem);
            }
            return NewList;
        } catch (Exception e) {
            LogUtils.error("Group_Get_List", e);
            return null;
        }
    }
    public static ArrayList<GroupMemberInfo> waitForGetGroupInfo(String GroupUin){
        try{
            GroupInfo mInfo = Group_Get_Info(GroupUin);
            Object TroopObserve = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.api.observer.TroopObserver"));
            MMethod.CallMethodParams(HookEnv.AppInterface,"addObserver",void.class,TroopObserve,true);
            AtomicBoolean locker = new AtomicBoolean();
            AtomicReference<List> mGetList = new AtomicReference<>();
            XPBridge.HookBeforeOnce(MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.api.observer.TroopObserver"),"onUpdateTroopGetMemberList",void.class,new Class[]{
                    String.class,boolean.class,List.class,int.class,long.class,int.class
            }),param -> {
                mGetList.set((List) param.args[2]);
                locker.getAndSet(true);
                MMethod.CallMethodSingle(HookEnv.AppInterface,"removeObserver",void.class,TroopObserve);
            });
            MMethod.CallMethodParams(QQEnvUtils.getBusinessHandler("com.tencent.mobileqq.troop.handler.TroopMemberListHandler"),null,void.class,
                    true,GroupUin,mInfo.Code,true,1,System.currentTimeMillis(),0);
            for (int i=0;i<100;i++){
                if (locker.get())break;
                Thread.sleep(100);
            }
            List troopMemberList = mGetList.get();
            if (troopMemberList ==null)return Group_Get_Member_List(GroupUin);

            ArrayList<GroupMemberInfo> Infos = new ArrayList<>();
            GroupInfo gInfo = Group_Get_Info(GroupUin);
            for (Object item : troopMemberList) {
                GroupMemberInfo NewItem = new GroupMemberInfo();
                NewItem.Uin = MField.GetField(item, "memberuin");
                NewItem.Nick = MField.GetField(item, "troopnick");
                NewItem.Name = MField.GetField(item, "friendnick");
                NewItem.join_time = MField.GetField(item, "join_time");
                NewItem.last_active = MField.GetField(item, "last_active_time");
                NewItem.isCreator = gInfo.Creator.equals(NewItem.Uin);
                NewItem.isAdmin = gInfo.adminList.contains(NewItem.Uin);
                NewItem.Title = MField.GetField(item, "mUniqueTitle");
                NewItem.level = MField.GetField(item,"newRealLevel",int.class);
                Infos.add(NewItem);
            }
            return Infos;

        }catch (Exception e){
            return Group_Get_Member_List(GroupUin);
        }

    }
    public static String convertGroupCodeToUin(String Code){
        try{
            Object infoImpl = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopInfoService"));
            String result = MMethod.CallMethodSingle(infoImpl,"getTroopUinByTroopCode",String.class,Code);
            if (result != null)return  result;
        }catch (Exception e){

        }
        return Code;
    }

    public static GroupInfo Group_Get_Info(String GroupUin) {
        try {

            Object TroopInfoServer = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopInfoService"));
            Object GroupInfoR = MMethod.CallMethodSingle(TroopInfoServer,"getTroopInfo",MClass.loadClass("com.tencent.mobileqq.data.troop.TroopInfo"),GroupUin);

            GroupInfo NewItem = new GroupInfo();
            NewItem.Uin = MField.GetField(GroupInfoR, "troopuin");
            NewItem.Code = MField.GetField(GroupInfoR, "troopcode");
            NewItem.Name = MField.GetField(GroupInfoR, "troopname");
            NewItem.Creator = MField.GetField(GroupInfoR, "troopowneruin");
            String admins = MField.GetField(GroupInfoR, "Administrator");
            if (admins != null){
                NewItem.adminList = new ArrayList<>(Arrays.asList(admins.split("\\|")));
            }else {
                NewItem.adminList = new ArrayList<>();
            }

            return NewItem;
        } catch (Exception e) {
            LogUtils.error("Group_Get_Info", e);
            return null;
        }

    }
    public static String GetTroopNameByContact(String GroupUin) {
        try{
            String mStr = MMethod.CallStaticMethod(MClass.loadClass("com.tencent.mobileqq.utils.ContactUtils"),
                    "a",String.class,HookEnv.AppInterface,GroupUin,true);
            return mStr;
        }catch (Exception ex) {
            LogUtils.error("GetTroopNameByContact",ex);
            return GroupUin;
        }

    }

    public static ArrayList<GroupMemberInfo> Group_Get_Member_List(String GroupUin) {
        try {
            Object TroopManager = QQEnvUtils.getRuntimeService(MClass.loadClass("com.tencent.mobileqq.troop.api.ITroopMemberInfoService"));
            ArrayList MyList = MMethod.CallMethodParams(TroopManager, "getAllTroopMembers",List.class, GroupUin);

            ArrayList<GroupMemberInfo> Infos = new ArrayList<>();
            GroupInfo gInfo = Group_Get_Info(GroupUin);
            for (Object item : MyList) {
                GroupMemberInfo NewItem = new GroupMemberInfo();
                NewItem.Uin = MField.GetField(item, "memberuin");
                NewItem.Nick = MField.GetField(item, "troopnick");
                NewItem.Name = MField.GetField(item, "friendnick");
                NewItem.join_time = MField.GetField(item, "join_time");
                NewItem.last_active = MField.GetField(item, "last_active_time");
                NewItem.isCreator = gInfo.Creator.equals(NewItem.Uin);
                NewItem.isAdmin = gInfo.adminList.contains(NewItem.Uin);
                NewItem.Title = MField.GetField(item, "mUniqueTitle");
                NewItem.level = MField.GetField(item,"newRealLevel",int.class);
                Infos.add(NewItem);
            }
            return Infos;
        } catch (Throwable e) {
            LogUtils.error("GetGroupMemberList", e);
            return null;
        }
    }
    public static boolean IsCreator(String GroupUin,String UserUin){
        GroupMemberInfo info = Group_Get_Member_Info(GroupUin,UserUin);
        if (info != null){
            return info.isCreator;
        }
        return false;
    }
    public static boolean IsAdmin(String GroupUin,String UserUin){
        GroupMemberInfo info = Group_Get_Member_Info(GroupUin,UserUin);
        if (info != null){
            return info.isAdmin;
        }
        return false;
    }

    public static ArrayList<MuteList> Group_Get_Mute_List(String GroupUin) {
        try {
            Object Manager = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"), new Class[]{HookEnv.AppInterface.getClass()}, HookEnv.AppInterface);
            ArrayList TheList;
            try {
                TheList = MMethod.CallMethod(Manager, "a", ArrayList.class, new Class[]{String.class, boolean.class}, GroupUin, true);
            } catch (Exception e) {
                TheList = MMethod.CallMethod(Manager, null, ArrayList.class, new Class[]{String.class, boolean.class}, GroupUin, true);
            }

            if (TheList != null) {
                ArrayList<MuteList> newList = new ArrayList<>();
                for (Object item : TheList) {
                    MuteList newItem = new MuteList();
                    String UserUin = MField.GetField(item, "a", String.class);
                    long TimeStamp;
                    try {
                        TimeStamp = MField.GetField(item, "a", long.class);
                    } catch (Exception e) {
                        TimeStamp = MField.GetField(item, "b", long.class);
                    }
                    newItem.Uin = UserUin;
                    newItem.delayTime = TimeStamp * 1000 - System.currentTimeMillis();
                    newList.add(newItem);
                }
                return newList;
            }
            return null;
        } catch (Exception e) {
            return new ArrayList();
        }
    }


    public static class MuteList {
        public String Uin;
        public long delayTime;
    }

    public static class GroupInfo {
        public String Creator;
        public ArrayList<String> adminList;
        public String Name;
        public String Uin;
        public Object source;
        public String Code;
    }

    public static class GroupMemberInfo {
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
