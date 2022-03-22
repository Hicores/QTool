package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.util.Util;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.BitmapUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.ListForm.JavaPluginAct;
import com.hicore.qtool.R;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

//主菜单界面绘制
public class MainMenu extends Activity {
    private static final String TAG = "MainActivityProxy01";
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
    @SuppressLint("ResourceType")
    private View getCreateItem(HookLoader.UiInfo info){
        if (info.type == 1){
            Switch NewSwitch = new Switch(this);
            NewSwitch.setTextColor(Color.BLACK);
            NewSwitch.setText(info.title);
            NewSwitch.setChecked(HookEnv.Config.getBoolean("Main_Switch",info.ID,false));
            NewSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                HookEnv.Config.setBoolean("Main_Switch",info.ID,isChecked);
                info.UIInstance.SwitchChange(isChecked);
            });
            return NewSwitch;
        }else if (info.type == 2){
            RelativeLayout relativeLayout = new RelativeLayout(this);
            TextView text = new TextView(this);
            text.setTextColor(Color.BLACK);
            text.setText(info.title);
            text.setOnClickListener(v-> info.UIInstance.ListItemClick());
            text.setGravity(Gravity.TOP | Gravity.LEFT);
            text.setId(56668);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            relativeLayout.addView(text,params);

            TextView rightClick = new TextView(this);
            rightClick.setText(">");
            rightClick.setTextColor(Color.BLACK);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            relativeLayout.addView(rightClick,params);


            return relativeLayout;
        }
        return null;
    }
    //创建所有通过UIItem注解创建的菜单项目
    private void createItems(){
        HashSet<HookLoader.UiInfo> uiInfos = HookLoader.getUiInfos();
        LinearLayout QQHelper_Bar = findViewById(R.id.HideBar_QQHelper);
        for(HookLoader.UiInfo item : uiInfos){
            item.UIInstance = HookLoader.searchForUiInstance(item.ClzName);
            if (item.UIInstance != null){
                if (item.Position == 1){
                    View mAdd = getCreateItem(item);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(this,24));
                    param.setMargins(Utils.dip2px(this,20),10,Utils.dip2px(this,10),10);
                    QQHelper_Bar.addView(mAdd,param);
                }
            }

        }
        RegisterAnim(findViewById(R.id.QQHelper),QQHelper_Bar);

        LinearLayout QQCleaner_Bar = findViewById(R.id.HideBar_QQCleaner);
        for(HookLoader.UiInfo item : uiInfos){
            item.UIInstance = HookLoader.searchForUiInstance(item.ClzName);
            if (item.UIInstance != null){
                if (item.Position == 2){
                    View mAdd = getCreateItem(item);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(this,24));
                    param.setMargins(Utils.dip2px(this,20),10,Utils.dip2px(this,10),10);
                    QQCleaner_Bar.addView(mAdd,param);
                }
            }

        }
        RegisterAnim(findViewById(R.id.QQCleaner),QQCleaner_Bar);

    }
    //注册菜单展开收起动画
    private void RegisterAnim(View clickView,View animAA){
        int MaxHeight = InitMeasure(animAA);
        AtomicBoolean IsExpanded = new AtomicBoolean(false);
        AtomicBoolean IsExpanding = new AtomicBoolean(false);

        clickView.setOnClickListener(v->{
            if (IsExpanding.get())return;
            if (IsExpanded.get()){
                animAA.getLayoutParams().height = MaxHeight;
                animAA.requestLayout();
                int targetHeight = 0;
                IsExpanding.getAndSet(true);
                Animation scale = new Animation(){
                    int initialHeight;
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        animAA.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
                        animAA.requestLayout();
                    }

                    @Override
                    public void initialize(int width, int height, int parentWidth, int parentHeight) {
                        initialHeight = height;
                        super.initialize(width, height, parentWidth, parentHeight);
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };
                scale.setDuration(400);
                scale.setFillAfter(true);
                animAA.startAnimation(scale);
                animAA.setVisibility(View.INVISIBLE);

                new Handler(Looper.getMainLooper())
                        .postDelayed(()->{
                            animAA.setVisibility(View.GONE);
                            IsExpanding.getAndSet(false);
                            IsExpanded.getAndSet(false);
                            animAA.clearAnimation();
                        },500);
            }else {
                animAA.getLayoutParams().height = 0;
                animAA.requestLayout();
                int targetHeight = MaxHeight;
                IsExpanding.getAndSet(true);
                Animation scale = new Animation(){
                    int initialHeight;
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        animAA.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
                        animAA.requestLayout();
                    }

                    @Override
                    public void initialize(int width, int height, int parentWidth, int parentHeight) {
                        initialHeight = height;
                        super.initialize(width, height, parentWidth, parentHeight);
                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };
                scale.setDuration(400);
                scale.setFillAfter(true);
                animAA.startAnimation(scale);
                animAA.setVisibility(View.INVISIBLE);

                new Handler(Looper.getMainLooper())
                        .postDelayed(()->{
                            animAA.setVisibility(View.VISIBLE);
                            IsExpanding.getAndSet(false);
                            IsExpanded.getAndSet(true);
                            animAA.clearAnimation();
                        },500);
            }
        });
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);


            ResUtils.StartInject(this);
            setTheme(R.style.AnimActivity);


            setTitleFea();

            setContentView(R.layout.menu_main);

            LinearLayout mRoot = findViewById(R.id.sRoot_);

            blurRoot = findViewById(R.id.BlurContain);
            try{
                mRoot.setBackground(new BitmapDrawable(null, map));
                blurRoot.setBackground(new BitmapDrawable(null, BitmapUtils.toBlur(map, 10)));
                blurRoot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_out));
            }catch (Exception e){
                map = BitmapFactory.decodeResource(getResources(),R.drawable.cat);
                mRoot.setBackground(new BitmapDrawable(null, map));
                blurRoot.setBackground(new BitmapDrawable(null, BitmapUtils.toBlur(map, 10)));
                blurRoot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_out));
            }

            View itemJavaPlugin = findViewById(R.id.ItemPlugin);
            itemJavaPlugin.setOnClickListener(v-> JavaPluginAct.startActivity(this));

            findViewById(R.id.GetNewVer).setOnClickListener(v->{
                Uri u = Uri.parse("https://github.com/Hicores/QTool/actions");
                Intent in = new Intent(Intent.ACTION_VIEW,u);
                startActivity(in);
            });

            createItems();

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
    private int InitMeasure(View vv){
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        vv.measure(width,height);
        return vv.getMeasuredHeight();
    }

}
