package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kyuubiran.ezxhelper.init.EzXHelperInit;
import com.hicore.Utils.Utils;

public class MainMenu extends AppCompatActivity {
    public static void createActivity(Activity parentAct){

        Intent intent = new Intent(parentAct,MainMenu.class);
        parentAct.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.ShowToast("Activity Proxy Success");
    }
}
