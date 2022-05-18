package cc.hicore.qtool.QQCleaner.StorageClean;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.BaseProxyAct;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQTools.ContUtil;
import cc.hicore.qtool.QQTools.QQSelectHelper;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import de.robv.android.xposed.XposedBridge;

@UIItem(name = "聊天数据库清理",groupName = "空间清理",targetID = 4,type = 2,id = "DBCleanerCleanView")
public class DBCleaner implements BaseUiItem {

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        String[] selectItems = new String[]{"群组消息","好友消息"};
        new AlertDialog.Builder(context)
                .setItems(selectItems, (dialog, which) -> calc_db(context,which))
                .setTitle("点击一项进行检索(不会立即清理)")
                .show();
    }
    private void calc_db(Context base,int select){
        BaseProxyAct.createNewView("DBResultShow", (Activity) base, context1 -> {
            ScrollView sc = new ScrollView(context1);
            LinearLayout mRoot = new LinearLayout(context1);
            sc.addView(mRoot);
            mRoot.setOrientation(LinearLayout.VERTICAL);

            BasePopupView basePopupView = new XPopup.Builder(ContUtil.getFixContext(context1))
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asLoading("正在计算...");

            basePopupView.show();

            new Thread(()->{
                try{
                    LocalTableInit.initTable();
                    LayoutInflater inflater = ContUtil.getContextInflater(context1);
                    DBHelper helper = new DBHelper(HookEnv.AppContext.getDatabasePath(QQEnvUtils.getCurrentUin()+".db").getAbsolutePath());
                    DBHelper slowTable = new DBHelper(HookEnv.AppContext.getDatabasePath("slowtable_"+QQEnvUtils.getCurrentUin()+".db").getAbsolutePath());
                    HashSet<String> tableName = new HashSet<>();
                    List<String> tables = helper.getTables();
                    Iterator<String> its = tables.iterator();
                    while (its.hasNext()){
                        String name = its.next();
                        if (select == 0 && name.startsWith("mr_troop")){
                            tableName.add(name);
                        } else if (select == 1 && name.startsWith("mr_friend")){
                            tableName.add(name);
                        }else if (select == 2 && name.startsWith("mr_guild")){
                            tableName.add(name);
                        }
                    }

                    tables = slowTable.getTables();
                    its = tables.iterator();
                    while (its.hasNext()){
                        String name = its.next();
                        if (select == 0 && name.startsWith("mr_troop")){
                            tableName.add(name);
                        } else if (select == 1 && name.startsWith("mr_friend")){
                            tableName.add(name);
                        }else if (select == 2 && name.startsWith("mr_guild")){
                            tableName.add(name);
                        }
                    }


                    for (String tablesName : tableName){

                        String uin;
                        if (select == 1){
                            uin = LocalTableInit.query(tablesName.substring(10,10+32));
                        }else{
                            uin = LocalTableInit.query(tablesName.substring(9,9+32));
                        }

                        if (uin.isEmpty())uin = DBHelper.decodeData(helper.getOneData(tablesName,"frienduin"));
                        if (TextUtils.isEmpty(uin)){
                            uin = DBHelper.decodeData(slowTable.getOneData(tablesName,"frienduin"));
                        }
                        int count = helper.getCount(tablesName);
                        int slowTableCount = slowTable.getCount(tablesName);

                        String finalUin = uin;
                        Utils.PostToMain(()->{
                            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dbcleaner_item,null);
                            TextView name = layout.findViewById(R.id.Show_Name);
                            if (TextUtils.isEmpty(finalUin)){
                                if (select == 0){
                                    name.setText(tablesName.substring(9));
                                }else if (select == 1){
                                    name.setText(tablesName.substring(10));
                                }

                            }else {
                                if (select == 0){
                                    name.setText(QQGroupUtils.Group_Get_Name(finalUin)+"("+ finalUin +")");
                                }else if (select == 1){
                                    name.setText(QQEnvUtils.getFriendName(finalUin)+"("+ finalUin +")");
                                }

                            }
                            name.setOnClickListener(v->{
                                if (select == 0){
                                    QQEnvUtils.OpenTroopCard(finalUin);
                                }else if (select == 1){
                                    QQEnvUtils.OpenUserCard(finalUin);
                                }
                            });
                            QQSelectHelper.RoundImageView headerView = layout.findViewById(R.id.Header);
                            if (select == 0){
                                headerView.setImagePath(String.format("https://p.qlogo.cn/gh/%s/%s/140", finalUin, finalUin));
                            }else if (select == 1){
                                headerView.setImagePath(String.format("https://q4.qlogo.cn/g?b=qq&nk=%s&s=140", finalUin));
                            }

                            TextView countCommon = layout.findViewById(R.id.DBSize_Common);
                            countCommon.setText("消息条数:"+count);
                            TextView slowCount = layout.findViewById(R.id.DBSize_SlowTable);
                            slowCount.setText("Slowtable消息条数:"+slowTableCount);
                            mRoot.addView(layout);

                            Button btnReset = layout.findViewById(R.id.Clean_Now);
                            btnReset.setOnClickListener(v->{
                                new AlertDialog.Builder(context1)
                                        .setTitle("确认操作?")
                                        .setMessage("确认清除所有 "+name.getText() + " 的消息记录(仅本地)?")
                                        .setNegativeButton("清除所有", (dialog, which) -> {
                                            BasePopupView popup = new XPopup.Builder(ContUtil.getFixContext(context1))
                                                    .dismissOnTouchOutside(false)
                                                    .dismissOnBackPressed(false)
                                                    .asLoading("正在处理,请稍后...");
                                            popup.show();
                                            new Thread(()->{
                                                try {
                                                    slowTable.Drop(tablesName);
                                                    helper.Drop(tablesName);
                                                }catch (Exception e){

                                                }finally {
                                                    Utils.ShowToast("已执行");
                                                    Utils.PostToMain(popup::dismiss);
                                                }
                                            }).start();




                                        }).setNeutralButton("仅清除slowtable数据", (dialog, which) -> {
                                            BasePopupView popup = new XPopup.Builder(ContUtil.getFixContext(context1))
                                                    .dismissOnTouchOutside(false)
                                                    .dismissOnBackPressed(false)
                                                    .asLoading("正在处理,请稍后...");
                                            popup.show();
                                            new Thread(()->{
                                                try {
                                                    slowTable.Drop(tablesName);
                                                }catch (Exception e){

                                                }finally {
                                                    Utils.ShowToast("已执行");
                                                    Utils.PostToMain(popup::dismiss);
                                                }
                                            }).start();
                                        }).show();
                            });
                        });
                    }
                }finally {
                    Utils.PostToMain(basePopupView::dismiss);
                }
            }).start();

            return sc;
        });
    }


}
