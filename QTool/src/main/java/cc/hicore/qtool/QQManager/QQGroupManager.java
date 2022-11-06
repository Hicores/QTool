package cc.hicore.qtool.QQManager;

import cc.hicore.HookItemLoader.core.ApiHelper;
import cc.hicore.qtool.QQManager.ManagerImpl.Group_Change_Name;
import cc.hicore.qtool.QQManager.ManagerImpl.Group_Change_Title;
import cc.hicore.qtool.QQManager.ManagerImpl.Group_Kick;
import cc.hicore.qtool.QQManager.ManagerImpl.Group_Kick_Multi;
import cc.hicore.qtool.QQManager.ManagerImpl.Group_Mute;


public class QQGroupManager {
    public static void Group_Kick(String GroupUin, String UserUin, boolean isBlack) {
        ApiHelper.invoke(Group_Kick.class, GroupUin, UserUin, isBlack);
    }

    public static void Group_Kick(String GroupUin, String[] UserUin, boolean isBlack) {
        ApiHelper.invoke(Group_Kick_Multi.class, GroupUin, UserUin, isBlack);
    }

    public static void Group_Mute(String GroupUin, String UserUin, long time) {
        ApiHelper.invoke(Group_Mute.class, GroupUin, UserUin, time);
    }

    public static void Group_Change_Title(String GroupUin, String UserUin, String title) {
        ApiHelper.invoke(Group_Change_Title.class, GroupUin, UserUin, title);
    }

    public static void Group_Change_Name(String GroupUin, String UserUin, String name) {
        ApiHelper.invoke(Group_Change_Name.class, GroupUin, UserUin, name);
    }
}
