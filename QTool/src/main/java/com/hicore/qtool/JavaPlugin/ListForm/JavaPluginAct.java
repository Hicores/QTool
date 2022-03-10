package com.hicore.qtool.JavaPlugin.ListForm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hicore.qtool.HookEnv;
import com.hicore.qtool.R;

import java.io.File;

public class JavaPluginAct extends Activity {
    private LinearLayout itemLayout;
    public static void startActivity(Activity host){
        Intent intent = new Intent(host, JavaPluginAct.class);
        host.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_QTool);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_javaplugin);
        itemLayout = findViewById(R.id.ContainLayout);
        searchForLocal();

    }
    private void searchForLocal(){
        itemLayout.removeAllViews();
        File searchPath = new File(HookEnv.ExtraDataPath+"/Plugin");
        File[] searchResult = searchPath.listFiles();
        if (searchResult != null){
            for (File f :searchResult){
                if (f.exists() && f.isDirectory()){
                    LocalPluginItemController controller = LocalPluginItemController.create(this);
                    boolean loadResult = controller.checkAndLoadPluginInfo(f.getAbsolutePath());
                    if (loadResult){
                        itemLayout.addView(controller.getRoot());
                    }
                }
            }
        }
    }
    private void searchForOnline(){

    }
}
