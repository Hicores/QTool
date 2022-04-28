package cc.hicore.qtool.JavaPlugin.Controller;

import java.util.ArrayList;

import cc.hicore.qtool.QQManager.QQGroupUtils;

public class PluginApiHelper {
    public static ArrayList<PluginInfo.GroupInfo> getGroupList() {
        ArrayList<PluginInfo.GroupInfo> NewInfo = new ArrayList<>();
        ArrayList<QQGroupUtils.GroupInfo> infos = QQGroupUtils.Group_Get_List();
        for (QQGroupUtils.GroupInfo item : infos) {
            PluginInfo.GroupInfo NewItem = new PluginInfo.GroupInfo();
            NewItem.GroupUin = item.Uin;
            NewItem.GroupName = item.Name;
            NewItem.AdminList = item.adminList.toArray(new String[0]);
            NewItem.GroupOwner = item.Creator;
            NewItem.sourceInfo = item.source;
            NewInfo.add(NewItem);
        }
        return NewInfo;
    }

    public static ArrayList<PluginInfo.GroupMemberInfo> getMemberList(String Uin) {
        ArrayList<PluginInfo.GroupMemberInfo> NewInfo = new ArrayList<>();
        ArrayList<QQGroupUtils.GroupMemberInfo> infos = QQGroupUtils.Group_Get_Member_List(Uin);
        for (QQGroupUtils.GroupMemberInfo item : infos) {
            PluginInfo.GroupMemberInfo NewItem = new PluginInfo.GroupMemberInfo();
            NewItem.IsAdmin = item.isAdmin;
            NewItem.Join_Time = item.join_time;
            NewItem.NickName = item.Nick;
            NewItem.Last_AvtivityTime = item.last_active;
            NewItem.UserLevel = item.level;
            NewItem.UserUin = item.Uin;
            NewItem.UserName = item.Name;
            NewItem.sourceInfo = item.source;
            NewInfo.add(NewItem);
        }
        return NewInfo;
    }

    public static ArrayList<PluginInfo.GroupBanInfo> getMuteInfo(String GroupUin) {
        ArrayList<PluginInfo.GroupBanInfo> NewInfos = new ArrayList<>();
        ArrayList<QQGroupUtils.MuteList> infos = QQGroupUtils.Group_Get_Mute_List(GroupUin);
        for (QQGroupUtils.MuteList item : infos) {
            PluginInfo.GroupBanInfo NewInfo = new PluginInfo.GroupBanInfo();
            NewInfo.Endtime = System.currentTimeMillis() + item.delayTime;
            NewInfo.UserUin = item.Uin;
            NewInfo.UserName = QQGroupUtils.Group_Get_Member_Name(GroupUin, item.Uin);
            NewInfos.add(NewInfo);
        }
        return NewInfos;
    }
}
