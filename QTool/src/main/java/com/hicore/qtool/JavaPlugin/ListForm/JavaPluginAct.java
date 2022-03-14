package com.hicore.qtool.JavaPlugin.ListForm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.hicore.Utils.HttpUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.Controller.PluginController;
import com.hicore.qtool.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class JavaPluginAct extends Activity {
    private LinearLayout itemLayout;
    public static void startActivity(Activity host){
        Intent intent = new Intent(host, JavaPluginAct.class);
        host.startActivity(intent);
    }
    static AtomicReference<onNotify> notifyInstance = new AtomicReference<>();
    interface onNotify{
        void onNotifyLoadSuccess(String PluginID);
    }
    public static void NotifyLoadSuccess(String PluginID){
        onNotify notify = notifyInstance.get();
        if (notify != null){
            notify.onNotifyLoadSuccess(PluginID);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        notifyInstance.getAndSet(null);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_QTool);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_javaplugin);
        itemLayout = findViewById(R.id.ContainLayout);
        searchForLocal();
        findViewById(R.id.selectLocal).setOnClickListener(v-> searchForLocal());
        findViewById(R.id.selectOnline).setOnClickListener(v-> searchForOnline());

        findViewById(R.id.openApiDesc)
                .setOnClickListener(v->{
                    Uri u = Uri.parse("https://shimo.im/docs/913JVOpxNdiavN3E/");
                    Intent in = new Intent(Intent.ACTION_VIEW,u);
                    startActivity(in);
                });

    }
    private void searchForLocal(){
        HashMap<String, LocalPluginItemController> controllers = new HashMap<>();
        itemLayout.removeAllViews();
        File searchPath = new File(HookEnv.ExtraDataPath+"/Plugin");
        File[] searchResult = searchPath.listFiles();
        if (searchResult != null){
            for (File f :searchResult){
                if (f.exists() && f.isDirectory()){
                    LocalPluginItemController controller = LocalPluginItemController.create(this);
                    boolean loadResult = controller.checkAndLoadPluginInfo(f.getAbsolutePath());
                    if (loadResult){
                        itemLayout.addView(controller.getRoot(),controller.getParams());
                        controllers.put(controller.getPluginID(),controller);
                    }
                }
            }
        }
        notifyInstance.set(PluginID -> {
            LocalPluginItemController controller = controllers.get(PluginID);
            if (controller != null){
                new Handler(Looper.getMainLooper())
                        .post(controller::notifyLoadSuccessOrDestroy);
            }
        });
    }
    private void searchForOnline(){
        ProgressBar bar = new ProgressBar(this);
        itemLayout.removeAllViews();
        itemLayout.addView(bar);

        new Thread(()->{
            try{
                String OnlineData = HttpUtils.getContent("https://qtool.haonb.cc/getList");
                JSONArray newArray = new JSONArray(OnlineData);
                for(int i=0;i<newArray.length();i++){
                    JSONObject item = newArray.getJSONObject(i);
                    OnlinePluginItemController.PluginInfo decInfo = new OnlinePluginItemController.PluginInfo(item.toString());
                    new Handler(Looper.getMainLooper())
                            .post(()->{
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                param.setMargins(20,10,20,10);
                                View v = OnlinePluginItemController.getViewInstance(this,decInfo);
                                itemLayout.addView(v,param);
                            });
                }
            }catch (Exception e){
                Utils.ShowToastL("无法获取列表:\n"+e);
            }finally {
                new Handler(Looper.getMainLooper()).post(()->itemLayout.removeView(bar));
            }
        }).start();
    }
}
