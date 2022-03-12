package com.hicore.qtool.QQTools;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

public class QQSelectHelper {

    interface onSelected{
        void onGroupSelect(ArrayList<String> uin);
        void onFriendSelect(ArrayList<String> uin);
        void onGuildSelect(HashMap<String,ArrayList<String>> guilds);
    }
    public QQSelectHelper(Context context){

    }
    public void setSelectedGroup(ArrayList<String> selectGroup){

    }
    public void setSelectedFriend(ArrayList<String> selectFriend){

    }
    public void setSelectedGuildChannel(HashMap<String,ArrayList<String>> selectGuild){

    }
}
