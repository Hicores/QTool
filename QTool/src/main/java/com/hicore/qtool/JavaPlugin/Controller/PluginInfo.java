package com.hicore.qtool.JavaPlugin.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import bsh.Interpreter;

public class PluginInfo {
    public String PluginID;
    public String PluginVerifyID;
    public String LocalPath;
    public boolean IsBlackMode;
    public ArrayList<String> ListStr;

    public boolean IsRunning;
    public boolean IsLoading;

    public String PluginName;
    public String PluginAuthor;
    public String PluginVersion;
    public Interpreter Instance;
    public HashMap<String, PluginController.ItemInfo> ItemFunctions = new HashMap<>();
    public String ItemClickFunctionName = "";

    public String AccessToken = "";


    public boolean IsAvailable(String GroupUin){
        return true;
        //return IsBlackMode != ListStr.contains(GroupUin);
    }
    public static class EarlyInfo{
        public int istroop;
        public String GroupUin;
        public String UserUin;
    }
    public static class MessageData{
        public String MessageContent;
        public String GroupUin;
        public String UserUin;
        public int MessageType;
        public boolean IsGroup;
        public boolean IsChannel;
        public String SenderNickName;
        public Object SessionInfo;
        public Object AppInterface;
        public long MessageTime;
        public String[] AtList;
        public ArrayList mAtList;
        public Object msg;
        public String[] PicList;
        public String AdminUin;

        public boolean IsSend;

        public String FileName;
        public String FileUrl;
        public long FileSize;
        public String LocalPath;
        public String md5;

        public String ReplyTo;

        public String ChannelID;
        public String GuildID;
    }
    public static class GroupBanInfo
    {
        public String UserUin;
        public String UserName;
        public long Endtime;
    }
    public static class GroupMemberInfo
    {
        public String UserUin;
        public String UserName;
        public String NickName;
        public int UserLevel;
        public long Join_Time;
        public long Last_AvtivityTime;
        public Object sourceInfo;

        public boolean IsAdmin;
    }
    public static class GroupInfo
    {
        public String GroupUin;
        public String GroupName;
        public String GroupOwner;
        public String[] AdminList;
        public Object sourceInfo;
    }
    public static class RequestInfo{
        public String GroupUin;
        public String UserUin;
        public String Answer;
        public String RequestText;
        public String RequestSource;

        public Object source;
    }

}
