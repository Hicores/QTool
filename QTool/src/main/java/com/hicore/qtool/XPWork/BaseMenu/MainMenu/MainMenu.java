package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.BitmapUtils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.R;

public class MainMenu extends Activity {
    private static final String TAG = "MainActivityProxy";
    @Override
    public ClassLoader getClassLoader() {
        return HookEnv.fixLoader;
    }

    public static void createActivity(Activity parentAct){
        try{
            Intent intent = new Intent(parentAct,MainMenu.class);
            parentAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.error(TAG, Log.getStackTraceString(e));
        }


    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try{
            ResUtils.StartInject(this);
            setTheme(R.style.Theme_QTool);
            super.onCreate(savedInstanceState);
            setTitleFea();
            setContentView(R.layout.main_menu);


            LinearLayout mRoot = findViewById(R.id.sRoot_);

            mRoot.setBackground(new BitmapDrawable(null,BitmapUtils.toBlur(BitmapFactory.decodeResource(getResources(),R.drawable.def_bg),24)));
            //mRoot.setBackground(new BitmapDrawable(null,BitmapFactory.decodeResource(getResources(),R.drawable.def_bg)));

        }catch (Exception e){
            LogUtils.error(TAG, Log.getStackTraceString(e));
        }
    }
    private void setTitleFea(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }
}
