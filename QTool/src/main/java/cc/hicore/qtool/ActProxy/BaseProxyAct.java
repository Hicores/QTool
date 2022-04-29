package cc.hicore.qtool.ActProxy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.Utils.BitmapUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XPWork.BaseMenu.MainMenu.MainMenu;

public class BaseProxyAct extends Activity{
    private static class TmpCacheParams{
        View createView;
        Bitmap cacheShop;
    }
    private static HashMap<String,TmpCacheParams> cacheParam = new HashMap<>();
    private View createView;
    private Bitmap cacheBitmap;
    public static void createNewView(String Tag,Activity baseAct,View childView){
        try{
            TmpCacheParams param = new TmpCacheParams();
            param.cacheShop = BitmapUtils.onCut(baseAct);
            param.createView = childView;
            cacheParam.put(Tag,param);

            Intent intent = new Intent(baseAct, BaseProxyAct.class);
            intent.putExtra("Tag",Tag);
            baseAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.error("BaseActProxy", Log.getStackTraceString(e));
            Utils.ShowToast("无法创建界面:"+e);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AnimActivity);
        setTitleFea();
        setContentView(R.layout.base_activity_container);

        String tag = getIntent().getStringExtra("Tag");
        TmpCacheParams mParam= cacheParam.get(tag);
        if (mParam == null){
            finish();
            return;
        }
        cacheParam.remove(tag);


        createView = mParam.createView;
        cacheBitmap = mParam.cacheShop;
        LinearLayout base_root = findViewById(R.id.Base_Container_First);
        RelativeLayout ani_layout = findViewById(R.id.Base_Container_Ani);
        if (cacheBitmap.isRecycled()){
            cacheBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        }
        base_root.setBackground(new BitmapDrawable(cacheBitmap));
        ani_layout.setBackground(new BitmapDrawable(null, BitmapUtils.toBlur(cacheBitmap, 10)));
        ani_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fade_out));

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ani_layout.addView(createView,param);
    }
    private void setTitleFea() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }
}
