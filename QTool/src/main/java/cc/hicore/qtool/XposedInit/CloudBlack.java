package cc.hicore.qtool.XposedInit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.ThreadUtils;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupManager;
import cc.hicore.qtool.QQManager.QQGroupUtils;

@XPItem(itemType = XPItem.ITEM_Api, name = "检测", period = XPItem.Period_InitData)
public class CloudBlack {
    public static void startCheckCloudBlack() {
        try {
            Thread.sleep(10000);
            String JSONContent = HttpUtils.getContent("https://qtool.haonb.cc/getCloudBlack");
            JSONObject json = new JSONObject(JSONContent);
            JSONArray uinArr = json.getJSONArray("data");
            for (int i = 0; i < uinArr.length(); i++) {
                JSONObject itemObj = uinArr.getJSONObject(i);
                String Uin = itemObj.getString("uin");
                checkAndKick(Uin);
            }
        } catch (Throwable t) {

        }
    }

    private static void checkAndKick(String Uin) {
        if (Uin.length() < 5 || Uin.length() > 10) return;
        ArrayList<QQGroupUtils.GroupInfo> groups = QQGroupUtils.Group_Get_List();
        for (QQGroupUtils.GroupInfo info : groups) {
            String GroupUin = info.Uin;
            if (QQGroupUtils.IsCreator(GroupUin, QQEnvUtils.getCurrentUin())) {
                if (QQGroupUtils.Is_In_Group(GroupUin, Uin)) {
                    QQGroupManager.Group_Kick(GroupUin, Uin, true);
                    reportYunBlack(Uin, GroupUin);
                }
            } else if (QQGroupUtils.IsAdmin(GroupUin, QQEnvUtils.getCurrentUin())) {
                QQGroupUtils.GroupMemberInfo memberInfo = QQGroupUtils.Group_Get_Member_Info(GroupUin, Uin);
                if (memberInfo != null && !memberInfo.isAdmin && !memberInfo.isCreator) {
                    QQGroupManager.Group_Kick(GroupUin, Uin, true);
                    reportYunBlack(Uin, GroupUin);
                }
            }
        }
    }

    private static void reportYunBlack(String TargetUin, String TargetGroupUin) {
        ThreadUtils.PostCommonTask(() -> HttpUtils.getContent("https://qtool.haonb.cc/yumBlack/reportKick?uin=" + TargetUin + "&groupuin=" + TargetGroupUin));
    }

    @VerController
    @CommonExecutor
    public void CommonWorker() {
        new Thread(CloudBlack::startCheckCloudBlack).start();
    }
}
