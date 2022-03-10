package com.hicore.qtool.QQManager;

import java.util.ArrayList;

public class QQGroupUtils {
    public static String Group_Get_Name(String GroupUin){
        return null;
    }
    public static String Group_Get_Member_Name(String GroupUin,String UserUin){
        return null;
    }
    public static String Group_Get_Member_Title(String GroupUin,String UserUin){
        return null;
    }
    public static ArrayList<GroupInfo> Group_Get_List(){
        return null;
    }
    public static ArrayList<GroupMemberInfo> Group_Get_Member_List(String GroupUin){
        return null;
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
        public boolean isAdmin;
        public boolean isCreator;
        public int level;
        public long join_time;
        public long last_active;
        public Object source;
    }
}
