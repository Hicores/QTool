package com.hicore.qtool.JavaPlugin.ListForm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hicore.qtool.R;

public class JavaPluginAct extends Activity {
    public static void startActivity(Activity host){
        Intent intent = new Intent(host, JavaPluginAct.class);
        host.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_QTool);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_javaplugin);

    }
}
