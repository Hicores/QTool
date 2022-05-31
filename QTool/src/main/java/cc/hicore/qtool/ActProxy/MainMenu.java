package cc.hicore.qtool.ActProxy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

import cc.hicore.ConfigUtils.BeforeConfig;
import cc.hicore.Utils.ActionUtils;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import de.robv.android.xposed.XposedBridge;

public class MainMenu {
    private static class Item_data{
        private String Name;
        private int type;
        private String actionData;
        private int IconRes;
    }
    private static HashMap<Integer,Item_data> main_items = new HashMap<>(64);
    static {
        new MainMenuActionRegister();
    }
    public static void addItemData(int id,String Name,int type,String ActionData,int ResIcon){
        Item_data data = new Item_data();
        data.Name = Name;
        data.actionData = ActionData;
        data.type = type;
        data.IconRes = ResIcon;
        main_items.put(id,data);
    }
    public static void onCreate(Context context){

        BaseProxyAct.createNewView("MainMenu", (Activity) context, hostContext -> {
            LayoutInflater inflater = LayoutInflater.from(hostContext);
            RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.base_main_menu,null);
            TextView now_version = v.findViewById(R.id.Version_Now);
            now_version.setText("当前版本:"+ BuildConfig.VERSION_NAME);

            TextView new_version = v.findViewById(R.id.Version_New);
            new_version.setText("最新CI版本:QTool-CI-"+ HookEnv.New_Version);

            try{
                if (BeforeConfig.getBoolean("Enable_SubMode") && (Integer.parseInt(HookEnv.New_Version) > BuildConfig.VERSION_CODE)){
                    new_version.setOnClickListener(av->{
                        String truePath = HookEnv.AppPath + "/files/"+ DataUtils.getStrMD5(Settings.Secure.ANDROID_ID);
                        String cachePath = HookEnv.AppPath + "/cache/"+ DataUtils.getStrMD5(Settings.Secure.ANDROID_ID);
                        new AlertDialog.Builder(hostContext)
                                .setTitle("是否更新?")
                                .setMessage("是否更新到CI-"+HookEnv.New_Version)
                                .setNegativeButton("确定更新", (dialog, which) -> {
                                    HttpUtils.ProgressDownload("https://down.haonb.cc/CIDL/QTool-CI-"+HookEnv.New_Version+"/QTool-release.apk",cachePath,()->{
                                        if (new File(cachePath).length() > 1024){
                                            FileUtils.copy(cachePath,truePath);
                                        }
                                        Utils.ShowToastL("更新完成,请重启QQ");
                                    },hostContext);
                                }).setNeutralButton("取消", (dialog, which) -> {

                                }).show();
                    });
                }
            }catch (Exception e){

            }

            createView(v);
            return v;
        });
    }
    private static void createView(View v){
        LinearLayout l = v.findViewById(R.id.ContainItems);
        for (int id : main_items.keySet()){
            Item_data data = main_items.get(id);
            View item = LayoutInflater.from(v.getContext()).inflate(R.layout.base_menu_item,null);
            TextView name = item.findViewById(R.id.Base_Menu_Item_Name);
            name.setText(data.Name);
            ImageView icon = item.findViewById(R.id.Base_Menu_Item_Icon);
            icon.setBackgroundResource(data.IconRes);

            item.setOnClickListener(vx-> ActionUtils.startAction(data.actionData, (Activity) v.getContext()));

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(v.getContext(),60));
            param.topMargin = Utils.dip2px(v.getContext(),5);
            l.addView(item,param);
        }

    }
}
