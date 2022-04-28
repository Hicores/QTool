package cc.hicore.qtool.QQManager;

import android.text.TextUtils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;

public class QQGuildManager {
    public static void Guild_Mute(String GuildID, String UserTinyID, long Time) {
        try {
            if (TextUtils.isEmpty(UserTinyID)) {
                Object IGpsManager = QQEnvUtils.GetIGpsManager();
                Object ResultProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, (proxy, method, args) -> null);
                MMethod.CallMethodParams(IGpsManager, "setMemberShutUp", void.class, GuildID, UserTinyID, QQEnvUtils.GetServerTime() + Time, ResultProxy);
            } else {
                Object IGpsManager = QQEnvUtils.GetIGpsManager();
                Object ResultProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, (proxy, method, args) -> null);
                MMethod.CallMethodParams(IGpsManager, "setGuildShutUp", void.class, GuildID, Time, ResultProxy);
            }
        } catch (Exception e) {
            LogUtils.error("Guild_Mute", e);
        }
    }

    public static void Guild_Kick(String GuildID, String UserTinyID, boolean isBlack) {
        try {
            Object IGpsManager = QQEnvUtils.GetIGpsManager();
            Object ResultProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.callback.IResultWithSecurityCallback")}, (proxy, method, args) -> null);
            ArrayList<String> list = new ArrayList<>();
            list.add(UserTinyID);
            MMethod.CallMethodParams(IGpsManager, "kickGuildUsers", void.class, GuildID, list, isBlack, ResultProxy);
        } catch (Exception e) {
            LogUtils.error("Guild_Kick", e);
        }
    }

    public static void Guild_Revoke(Object msg) {
        try {
            Object RevokeHelper = QQEnvUtils.getBusinessHandler("com.tencent.mobileqq.guild.message.api.impl.GuildRevokeMessageHandler");
            MMethod.CallMethod(RevokeHelper, "a", void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")}, msg);
        } catch (Exception e) {
            LogUtils.error("Guild_Revoke", e);
        }
    }

    public static String Guild_Get_Avatar(String GuildID) {
        try {
            Object IGpsManager = QQEnvUtils.GetIGpsManager();
            Object GuildInfo = MMethod.CallMethodSingle(IGpsManager, "getGuildInfo", MClass.loadClass("com.tencent.mobileqq.qqguildsdk.data.IGProGuildInfo"), GuildID);
            return MMethod.CallMethodSingle(GuildInfo, "getAvatarUrl", String.class, 0);
        } catch (Exception e) {
            return "";
        }

    }

    public static ArrayList<Guild_Info> Guild_Get_List() {
        try {
            Object IGpsManager = QQEnvUtils.GetIGpsManager();
            List mList = MMethod.CallMethodNoParam(IGpsManager, "getGuildList", List.class);
            ArrayList<Guild_Info> NewInfos = new ArrayList<>();
            for (Object item : mList) {
                Guild_Info NewItem = new Guild_Info();
                NewItem.AvatarUrl = MMethod.CallMethodSingle(item, "getAvatarUrl", String.class, 0);
                NewItem.CreateTime = MMethod.CallMethodNoParam(item, "getCreateTime", long.class);
                NewItem.GuildID = MMethod.CallMethodNoParam(item, "getGuildID", String.class);
                NewItem.GuildName = MMethod.CallMethodNoParam(item, "getGuildName", String.class);
                NewInfos.add(NewItem);
            }
            return NewInfos;
        } catch (Exception e) {
            LogUtils.error("Guild_Get_List", e);
            return null;
        }
    }

    public static ArrayList<Channel_Info> Channel_Get_List(String GuildID) {
        try {
            Object IGpsManager = QQEnvUtils.GetIGpsManager();
            List mList = MMethod.CallMethodSingle(IGpsManager, "getChannelList", List.class, GuildID);
            ArrayList<Channel_Info> NewInfos = new ArrayList<>();
            for (Object item : mList) {
                Channel_Info NewItem = new Channel_Info();
                NewItem.ChannelID = MMethod.CallMethodNoParam(item, "getChannelUin", String.class);
                NewItem.ChannelName = MMethod.CallMethodNoParam(item, "getChannelName", String.class);
                NewItem.CreateTime = MMethod.CallMethodNoParam(item, "getCreateTime", long.class);
                NewItem.type = MMethod.CallMethodNoParam(item, "getType", int.class);
                NewInfos.add(NewItem);
            }
            return NewInfos;
        } catch (Exception e) {
            LogUtils.error("getChannelList", e);
            return null;
        }
    }

    public static String GetGuildName(String GuildID) {
        ArrayList<Guild_Info> list = Guild_Get_List();
        for (Guild_Info info : list) {
            if (info.GuildID.equals(GuildID)) {
                return info.GuildName;
            }
        }
        return GuildID;
    }

    public static String GetChannelName(String GuildID, String ChannelID) {
        ArrayList<Channel_Info> list = Channel_Get_List(GuildID);
        for (Channel_Info info : list) {
            if (info.ChannelID.equals(ChannelID)) {
                return info.ChannelName;
            }
        }
        return ChannelID;
    }

    public static class Guild_Info {
        public String GuildID;
        public String GuildName;
        public long CreateTime;
        public String AvatarUrl;
    }

    public static class Channel_Info {
        public String ChannelID;
        public String ChannelName;
        public long CreateTime;
        public int type;
    }
}
