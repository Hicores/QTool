package cc.hicore.qtool.GroupChecker.CheckJoinIn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQTools.ContextFixUtil;
import cc.hicore.qtool.QQTools.QQSelectHelper;
import cc.hicore.qtool.R;

public class JoinSame {
    public static void start(Context context){
        ArrayList<String> groupList = new ArrayList<>();
        ArrayList<QQGroupUtils.GroupInfo> tempGroupList = QQGroupUtils.Group_Get_List();
        boolean[] bool = new boolean[tempGroupList.size()];
        for (QQGroupUtils.GroupInfo info : tempGroupList){
            groupList.add(info.Name+"("+info.Uin+")");
        }
        new AlertDialog.Builder(context,3)
                .setMultiChoiceItems(groupList.toArray(new String[0]), bool, (dialog, which, isChecked) -> {

                }).setTitle("选择需要检测的群聊")
                .setNegativeButton("开始检测", (dialog, which) -> {
                    ArrayList<String> check = new ArrayList<>();
                    for (int i=0;i<bool.length;i++){
                        if (bool[i])check.add(tempGroupList.get(i).Uin);
                    }
                    if (check.size() < 2){
                        Utils.ShowToast("必须选择两个或以上的群聊");
                        return;
                    }
                    InnChecker(check.toArray(new String[0]),context);
                }).show();
    }
    private static void InnChecker(String[] checkGroupUin,Context context){
        ProgressDialog prog = new ProgressDialog(context,3);
        prog.setTitle("请稍等...");
        prog.setMessage("正在刷新群成员状态...");
        prog.setCancelable(false);
        prog.show();
        new Thread(()->{
            try {
                HashMap<String,ArrayList<QQGroupUtils.GroupMemberInfo>> updateMemberInfo = new HashMap<>();
                for (String GroupUin : checkGroupUin){
                    updateMemberInfo.put(GroupUin,QQGroupUtils.waitForGetGroupInfo(GroupUin));
                }
                Utils.PostToMain(()-> InnChecker0(updateMemberInfo,context));
            }catch (Exception e){

            }finally {
                Utils.PostToMain(prog::dismiss);
            }
        }).start();
    }
    private static void InnChecker0(HashMap<String,ArrayList<QQGroupUtils.GroupMemberInfo>> checkInfos,Context context){
        HashSet<String> uins = new HashSet<>();
        for (String groupUin : checkInfos.keySet()){
            for (QQGroupUtils.GroupMemberInfo info : checkInfos.get(groupUin)){
                uins.add(info.Uin);
            }
        }
        HashMap<String,ArrayList<String>> JoinInSaveGroupUserInfo = new HashMap<>();
        for (String Uin : uins){
            ArrayList<String> joinTroop = new ArrayList<>();
            for (String groupUin : checkInfos.keySet()){
                for (QQGroupUtils.GroupMemberInfo info : checkInfos.get(groupUin)){
                    if (info.Uin.equals(Uin))joinTroop.add(groupUin);
                }
            }
            if (joinTroop.size() > 1)JoinInSaveGroupUserInfo.put(Uin,joinTroop);
        }

        ShowResult(context,JoinInSaveGroupUserInfo);


    }
    private static void ShowResult(Context context,HashMap<String,ArrayList<String>> result){
        ScrollView sc = new ScrollView(context);
        sc.setBackgroundColor(Color.WHITE);
        LinearLayout mRoot = new LinearLayout(context);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        sc.addView(mRoot);

        ResUtils.StartInject(context);
        LayoutInflater inflater = ContextFixUtil.getContextInflater(context);
        for (String uin : result.keySet()){
            LinearLayout mItem = (LinearLayout) inflater.inflate(R.layout.join_save_item,null);
            QQSelectHelper.RoundImageView header = mItem.findViewById(R.id.Header);
            header.setImagePath(String.format("https://q4.qlogo.cn/g?b=qq&nk=%s&s=140", uin));
            TextView uinView = mItem.findViewById(R.id.UserUin);
            uinView.setText(QQGroupUtils.Group_Get_Member_Name(result.get(uin).get(0),uin)+"("+uin+")");

            LinearLayout container = mItem.findViewById(R.id.GroupContainer);

            for (String GroupUin : result.get(uin)){
                String GroupName = QQGroupUtils.Group_Get_Name(GroupUin);
                String InGroupName = QQGroupUtils.Group_Get_Member_Name(GroupUin,uin);

                TextView troopInfo = new TextView(context);
                troopInfo.setTextColor(Color.BLACK);
                troopInfo.setTextSize(16);
                troopInfo.setText(InGroupName + "->" + GroupName+"("+GroupUin+")");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = Utils.dip2px(context,8);
                container.addView(troopInfo,params);
            }
            mRoot.addView(mItem);
        }

        @SuppressLint("ResourceType") Dialog fullScreen = new Dialog(context,3);
        fullScreen.setContentView(sc);
        fullScreen.show();
    }
}
