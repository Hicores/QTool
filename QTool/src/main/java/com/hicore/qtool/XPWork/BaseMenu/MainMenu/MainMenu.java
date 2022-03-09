package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hicore.qtool.HookEnv;
import com.hicore.qtool.R;

public class MainMenu extends AppCompatActivity {
    private ClassLoader cacheLoader = null;
    @Override
    public ClassLoader getClassLoader() {
        if (cacheLoader == null) {
            cacheLoader = HookEnv.fixLoader;
        }
        return cacheLoader;
    }

    public static void createActivity(Activity parentAct){

        Intent intent = new Intent(parentAct,MainMenu.class);
        parentAct.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setTheme(R.style.Theme_QTool);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
