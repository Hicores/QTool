package cc.hicore.qtool.QQMessage;

import android.os.Bundle;
import android.text.TextUtils;

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Field;

public class QQSessionUtils {
    private static final String TAG = "QQSessionUtils";
    public static Object Build_SessionInfo(String GroupUin,String UserUin){
        try{
            if(GroupUin.contains("&")){
                String[] Cut = GroupUin.split("&");
                if(Cut.length>1){
                    return Build_SessionInfo_Guild(Cut[0],Cut[1],!UserUin.isEmpty());
                }
                return null;
            }
            Object mObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
            if(TextUtils.isEmpty(UserUin))
            {
                Table_Session_Field.isTroop().set(mObj,1);
                Table_Session_Field.friendUin().set(mObj,GroupUin);
                Table_Session_Field.TroopCode().set(mObj,GroupUin);

            }
            else if (TextUtils.isEmpty(GroupUin))
            {
                Table_Session_Field.isTroop().set(mObj,0);
                Table_Session_Field.friendUin().set(mObj,UserUin);
            }
            else
            {
                Table_Session_Field.isTroop().set(mObj,1000);
                Table_Session_Field.friendUin().set(mObj,UserUin);
                Table_Session_Field.InTroopUin().set(mObj,GroupUin);
                Table_Session_Field.TroopCode().set(mObj,GroupUin);
            }
            return mObj;
        }catch (Exception e){
            LogUtils.error(TAG,"Can't not build session:\n"+e);
            return null;
        }
    }
    public static String getGroupUin(Object Session){
        try {
            int SessionType = (int) Table_Session_Field.isTroop().get(Session);
            if(SessionType==0)return "";
            if(SessionType==1)return (String) Table_Session_Field.friendUin().get(Session);
            if(SessionType==1000)return (String) Table_Session_Field.InTroopUin().get(Session);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String getGroupUin(){
        return getGroupUin(HookEnv.SessionInfo);
    }
    public static String getFriendUin(){
        return getFriendUin(HookEnv.SessionInfo);
    }
    public static String getFriendUin(Object Session){
        try {
            int SessionType = (int)Table_Session_Field.isTroop().get(Session);
            if(SessionType==0)return (String) Table_Session_Field.friendUin().get(Session);
            if(SessionType==1)return "";
            if(SessionType==1000)return (String) Table_Session_Field.friendUin().get(Session);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static int getSessionID(Object Session) {
        try {
            return (int) Table_Session_Field.isTroop().get(Session);
        } catch (Exception e) {
            return -1;
        }
    }
    public static int getSessionID() {
        return getSessionID(HookEnv.SessionInfo);
    }
    public static String getGuildID(Object Session){
        try {
            int SessionType = (int)Table_Session_Field.isTroop().get(Session);
            if(SessionType==10014)return (String) Table_Session_Field.GuildID().get(Session);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String getGuildID(){
        return getGuildID(HookEnv.SessionInfo);
    }
    public static String getChannelID(Object session){
        try {
            int SessionType = (int)Table_Session_Field.isTroop().get(session);
            if(SessionType==10014)return (String) Table_Session_Field.ChannelID().get(session);
            return "";
        } catch (Exception e) {
            return "";
        }
    }
    public static String getChannelID(){
        return getChannelID(HookEnv.SessionInfo);
    }
    private static Object Build_SessionInfo_Guild(String GuildID,String ChannelID,boolean IsDirectMsg){
        try{
            if(IsDirectMsg){
                Object mObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
                Table_Session_Field.isTroop().set(mObj,10014);
                Table_Session_Field.GuildID().set(mObj,GuildID);
                Table_Session_Field.ChannelID().set(mObj,ChannelID);
                Table_Session_Field.CodeName().set(mObj,ChannelID);
                Bundle bundle = MField.GetField(mObj,"J",Bundle.class);
                bundle.putInt("guild_direct_message_flag",1);
                return mObj;
            }else {
                Object mObj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
                Table_Session_Field.isTroop().set(mObj,10014);
                Table_Session_Field.GuildID().set(mObj,GuildID);
                Table_Session_Field.ChannelID().set(mObj,ChannelID);
                return mObj;
            }
        }catch (Exception e){
            LogUtils.error(TAG,"Can't not build guild session:\n"+e);
            return null;
        }

    }
    @HookItem(isDelayInit = false,isRunInAllProc = false)
    public static class Table_Session_Field extends BaseHookItem {
        private static Class<?> SessionInfo(){
            return MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo");
        }
        private static Field isTroop(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"a", int.class) :
                    MField.FindField(SessionInfo(),"a",int.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field friendUin(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"a", String.class) :
                    MField.FindField(SessionInfo(),"b",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field TroopCode(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"b", String.class) :
                    MField.FindField(SessionInfo(),"c",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field InTroopUin(){
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(SessionInfo(),"c", String.class) :
                    MField.FindField(SessionInfo(),"d",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }
        private static Field GuildID(){
            return TroopCode();
        }
        private static Field ChannelID(){
            return friendUin();
        }
        private static Field CodeName(){
            Field f = MField.FindField(SessionInfo(),"e",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        private StringBuilder checkResult = new StringBuilder();
        @Override
        public String getTag() {
            return "Session_Field_Table";
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
            return checkResult.toString();
        }

        @Override
        public boolean check() {
            checkResult = new StringBuilder();
            if (isTroop()==null)checkResult.append("isTroop is null");
            if (friendUin()==null)checkResult.append("friendUin is null");
            if (TroopCode()==null)checkResult.append("TroopCode is null");
            if (InTroopUin()==null)checkResult.append("InTroopUin is null");
            if (CodeName()==null)checkResult.append("CodeName is null");
            return checkResult.length() == 0;
        }
    }
}
