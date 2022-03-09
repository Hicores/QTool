package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hicore.ReflectUtils.ResUtils;
import com.hicore.qtool.R;

public class MainMenu extends Activity {
    public static void createActivity(Activity parentAct){

        Intent intent = new Intent(parentAct,MainMenu.class);
        parentAct.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ResUtils.StartInject(this);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
