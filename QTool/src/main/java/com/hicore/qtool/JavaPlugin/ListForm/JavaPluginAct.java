package com.hicore.qtool.JavaPlugin.ListForm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.Controller.PluginController;
import com.hicore.qtool.R;

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

    }
}
