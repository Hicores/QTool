package cc.hicore.qtool.QQManager.UtilsImpl;

import java.util.ArrayList;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQGroupUtils;

@XPItem(name = "Group_Get_Mute_List",itemType = XPItem.ITEM_Api)
public class Group_Get_Mute_List {
    @ApiExecutor
    @VerController
    public ArrayList<QQGroupUtils.MuteList> get(String GroupUin) throws Exception {
        Object Manager = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"), new Class[]{HookEnv.AppInterface.getClass()}, HookEnv.AppInterface);
        ArrayList TheList;
        try {
            TheList = MMethod.CallMethod(Manager, "a", ArrayList.class, new Class[]{String.class, boolean.class}, GroupUin, true);
        } catch (Exception e) {
            TheList = MMethod.CallMethod(Manager, null, ArrayList.class, new Class[]{String.class, boolean.class}, GroupUin, true);
        }

        if (TheList != null) {
            ArrayList<QQGroupUtils.MuteList> newList = new ArrayList<>();
            for (Object item : TheList) {
                QQGroupUtils.MuteList newItem = new QQGroupUtils.MuteList();
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
    }
}
