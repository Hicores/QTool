package cc.hicore.qtool.QQTools;

import android.os.Bundle;

import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;

public class QQHighTipHelper {
    public static class HighLightItem{
        public int Start;
        public int End;
        public String Uin;
    }
    public static void AddHighLightTip(String GroupUin,String Items,HighLightItem[] ItemsClick) {
        try {
            Object ItemObject = QQMsgBuilder.Build_RawMessageRecord_Troop(GroupUin,-2030);
            MField.SetField(ItemObject,"msg",Items);
            for(HighLightItem item : ItemsClick)
            {
                Bundle b = new Bundle();
                b.putInt("key_action",5);
                b.putString("troop_mem_uin",item.Uin);
                b.putBoolean("need_update_nick", true);
                MMethod.CallMethod(ItemObject,"addHightlightItem",void.class,new Class[]{
                        int.class,int.class,Bundle.class
                },item.Start,item.End,b);
            }
            MField.SetField(ItemObject,"isread",true);
            QQMessageUtils.AddMsg(ItemObject);
        } catch (Exception e) {
        }
    }
}
