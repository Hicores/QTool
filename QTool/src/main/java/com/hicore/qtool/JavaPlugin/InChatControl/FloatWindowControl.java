package com.hicore.qtool.JavaPlugin.InChatControl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hicore.ReflectUtils.XPBridge;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.Controller.PluginController;
import com.hicore.qtool.JavaPlugin.Controller.PluginInfo;
import com.hicore.qtool.QQMessage.QQSessionUtils;
import com.hicore.qtool.QQTools.ContUtil;
import com.hicore.qtool.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class FloatWindowControl {
    static Activity cacheAct;
    static WindowManager manager;
    static ImageView mPluginButton;
    static AtomicBoolean IsAdd = new AtomicBoolean();
    static WindowManager.LayoutParams sParams;

    private static final int MIN_X = 0;
    private static int MIN_Y = -1;
    private static int MAX_X = -1;
    private static int MAX_Y = -1;
    private static Timer timeHide = new Timer();
    private static boolean IsSetTimer = false;



    public static void onShowEvent(boolean IsShow,Object session){
        Activity act = Utils.getTopActivity();
        if (MIN_Y == -1)
        {
            int resourceId= act.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                MIN_Y = act.getResources().getDimensionPixelSize(resourceId);
            }
        }
        if (MAX_X == -1){
            DisplayMetrics metrics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            MAX_X = metrics.widthPixels-  Utils.dip2px(act,32);
            MAX_Y = MIN_Y + metrics.heightPixels -  Utils.dip2px(act,32);
        }

        if (IsShow && IsAvailable(QQSessionUtils.getGroupUin(session),!(QQSessionUtils.getSessionID(session) == 0 || QQSessionUtils.getSessionID(session) == 1000))){

            if (act == cacheAct){
                if (!IsAdd.getAndSet(true))
                {
                    manager.addView(mPluginButton,getParam(act));
                }
            }else {
                if (IsAdd.getAndSet(false)){
                    manager.removeViewImmediate(mPluginButton);
                }

                manager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);

                InitImageButton(act);
                manager.addView(mPluginButton,getParam(act));
                IsAdd.getAndSet(true);

                cacheAct = act;
                requestTimer();
            }
        }else {
            if (manager != null && IsAdd.getAndSet(false))
            {
                removeTimer();
                manager.removeViewImmediate(mPluginButton);
            }

        }
    }
    static HashMap<String,PluginInfo> cachePlugin;
    private static boolean IsAvailable(String TroopUin,boolean IsTroop){
        HashMap<String, PluginInfo> AvailPlugin = PluginController.checkHasAvailMenu(TroopUin,IsTroop);
        cachePlugin = AvailPlugin;
        return !AvailPlugin.isEmpty();
    }
    private static void removeTimer(){
        if (IsSetTimer){
            timeHide.cancel();
            IsSetTimer = false;
        }
    }
    private static void requestTimer(){
        removeTimer();
        timeHide = new Timer();
        timeHide.schedule(new TimerTask() {
            @Override
            public void run() {
                IsSetTimer = false;
                new Handler(Looper.getMainLooper()).post(FloatWindowControl::ChangeColor);
            }
        },3000);
        IsSetTimer = true;
    }
    private static AtomicBoolean actionClick = new AtomicBoolean();
    @SuppressLint("ClickableViewAccessibility")
    private static void InitImageButton(Context context){
        mPluginButton = new ImageView(context);
        mPluginButton.setImageResource(R.drawable.plugin_btn);
        mPluginButton.setOnClickListener(v->{
            if (actionClick.get())
            {
                ShowButtonDialog();
            }

        });

        mPluginButton.setOnTouchListener(new View.OnTouchListener() {
            private int x;
            private int y;

            private int sumX;
            private int sumY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        sumX = 0;
                        sumY = 0;
                        removeTimer();
                        mPluginButton.getDrawable().setAlpha(255);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int movedX = nowX - x;
                        int movedY = nowY - y;
                        x = nowX;
                        y = nowY;
                        sParams.x = sParams.x + movedX;
                        sParams.y = sParams.y + movedY;

                        if (sParams.x <MIN_X)sParams.x = MIN_X;
                        if (sParams.y < MIN_Y)sParams.y = MIN_Y;
                        if (sParams.x > MAX_X)sParams.x = MAX_X;
                        if (sParams.y > MAX_Y)sParams.y = MAX_Y;

                        sumX+=Math.abs(movedX);
                        sumY+=Math.abs(movedY);

                        // 更新悬浮窗控件布局
                        manager.updateViewLayout(mPluginButton, sParams);


                        if (sumX > 4 || sumY > 4){
                            actionClick.getAndSet(false);
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        HookEnv.Config.setInt("Window_Position","Plugin_Button_y",sParams.y);
                        HookEnv.Config.setInt("Window_Position","Plugin_Button_x",sParams.x);
                        new Handler(Looper.getMainLooper()).postDelayed(()->actionClick.getAndSet(true),200);
                        new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::StickToSize,80);

                        requestTimer();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }
    public static void ChangeColor(){
        if (IsAdd.get()){
            if (mPluginButton.getDrawable().getAlpha() > 100){
                mPluginButton.getDrawable().setAlpha(mPluginButton.getDrawable().getAlpha() - 10);
                new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::ChangeColor,20);
            }

        }

    }
    public static void StickToSize(){
        if (IsAdd.get()){
            if (sParams.x > MIN_X && sParams.x < MAX_X){
                int middle = MAX_X / 2;
                if (sParams.x < middle){
                    sParams.x = sParams.x - 20;
                    manager.updateViewLayout(mPluginButton, sParams);
                    new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::StickToSize,6);
                }else {
                    sParams.x = sParams.x + 20;
                    manager.updateViewLayout(mPluginButton, sParams);
                    new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::StickToSize,6);
                }

            }
        }

    }
    public static WindowManager.LayoutParams getParam(Context context){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW+5;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = Utils.dip2px(context,32);
        layoutParams.height = Utils.dip2px(context,32);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        sParams = layoutParams;

        int x = HookEnv.Config.getInt("Window_Position","Plugin_Button_x",0);
        int y = HookEnv.Config.getInt("Window_Position","Plugin_Button_y",300);
        int middle = MAX_X / 2;
        sParams.y = y;
        if (x < middle){
            sParams.x = 0;
        }else {
            sParams.x = MAX_X;
        }


        return layoutParams;
    }
    private static void ShowButtonDialog(){
        Context fixContext = new ContUtil.FixContext(cacheAct);
        LayoutInflater inflater = ContUtil.getContextInflater(cacheAct);
        try{
            XPBridge.HookAfterOnce(LayoutInflater.class.getMethod("from", Context.class), param -> {
                LayoutInflater inflater1 = (LayoutInflater) param.getResult();
                param.setResult(inflater1.cloneInContext(fixContext));
            });
        }catch (Exception e){}
        BottomPopupView view = new BottomPopupView(fixContext){
            @Override
            protected int getImplLayoutId() {
                return R.layout.plugin_inchat_menu;
            }

            @Override
            protected int getMaxHeight() {
                return fixContext.getResources().getDisplayMetrics().heightPixels / 2 - 200;
            }

            @Override
            protected void onCreate() {
                LinearLayout mList = findViewById(R.id.plugin_menu_list);

                for (String VerifyKey : cachePlugin.keySet()){
                    PluginInfo info = cachePlugin.get(VerifyKey);

                    RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.plugin_inchat_menu_item,null);
                    TextView title = item.findViewById(R.id.plugin_title);
                    title.setText(info.PluginName);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param.setMargins(Utils.dip2px(getContext(),20),Utils.dip2px(getContext(),10),Utils.dip2px(getContext(),20),Utils.dip2px(getContext(),10));


                    LinearLayout menu_items = item.findViewById(R.id.menu_items);
                    for (String itemKey : info.ItemFunctions.keySet()){
                        PluginController.ItemInfo itemInfo = info.ItemFunctions.get(itemKey);
                        if (itemInfo.itemType == 1){
                            TextView newView = (TextView) inflater.inflate(R.layout.plugin_item_menu_item,null);
                            newView.setText(" - "+itemInfo.ItemName);

                            LinearLayout.LayoutParams parambb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            parambb.setMargins(Utils.dip2px(getContext(),5),Utils.dip2px(getContext(),5),Utils.dip2px(getContext(),5),Utils.dip2px(getContext(),5));

                            menu_items.addView(newView,parambb);
                        }
                    }

                    mList.addView(item,param);
                }
                super.onCreate();
            }
        };
        XPopup.Builder NewPop = new XPopup.Builder(fixContext);
        BasePopupView base = NewPop.asCustom(view);
        base.show();



    }
}
