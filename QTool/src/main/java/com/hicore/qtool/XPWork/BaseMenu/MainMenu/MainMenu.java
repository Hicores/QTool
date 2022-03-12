package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.BitmapUtils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.ListForm.JavaPluginAct;
import com.hicore.qtool.R;

public class MainMenu extends Activity {
    private static final String TAG = "MainActivityProxy";
    private static Bitmap map = null;
    RelativeLayout blurRoot;
    @Override
    public ClassLoader getClassLoader() {
        return HookEnv.moduleLoader;
    }

    public static void createActivity(Activity parentAct){
        try{
            Intent intent = new Intent(parentAct,MainMenu.class);
            map = BitmapUtils.onCut(parentAct);
            parentAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.error(TAG, Log.getStackTraceString(e));
        }


    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            ResUtils.StartInject(this);
            setTheme(R.style.AnimActivity);
            super.onCreate(savedInstanceState);

            setTitleFea();

            setContentView(R.layout.menu_main);

            LinearLayout mRoot = findViewById(R.id.sRoot_);
            mRoot.setBackground(new BitmapDrawable(null, map));
            blurRoot = findViewById(R.id.BlurContain);
            blurRoot.setBackground(new BitmapDrawable(null, BitmapUtils.toBlur(map, 10)));
            blurRoot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_out));


            View itemJavaPlugin = findViewById(R.id.ItemPlugin);
            itemJavaPlugin.setOnClickListener(v-> JavaPluginAct.startActivity(this));
        } catch (Exception e) {
            LogUtils.error(TAG, Log.getStackTraceString(e));
        }
    }
    private boolean IsBacking = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (IsBacking)return true;
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Animation anim = AnimationUtils.loadAnimation(this,R.anim.anim_fade_in);
            anim.setFillAfter(true);
            blurRoot.startAnimation(anim);
            IsBacking = true;

            new Handler(Looper.getMainLooper())
                    .postDelayed(()->{
                        IsBacking = false;
                        finish();
                    },600);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0,0);
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
