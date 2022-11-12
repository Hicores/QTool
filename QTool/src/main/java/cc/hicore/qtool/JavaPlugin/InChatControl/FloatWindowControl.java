package cc.hicore.qtool.JavaPlugin.InChatControl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.JavaPlugin.Controller.PluginController;
import cc.hicore.qtool.JavaPlugin.Controller.PluginInfo;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQManager.QQGuildManager;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.QQTools.ContextFixUtil;
import cc.hicore.qtool.R;

/*
显示脚本菜单项目
 */
public class FloatWindowControl {
    private static final int MIN_X = 0;
    static Activity cacheAct;
    static WindowManager manager;
    static ImageView mPluginButton;
    static AtomicBoolean IsAdd = new AtomicBoolean();
    static WindowManager.LayoutParams sParams;
    static HashMap<String, PluginInfo> cachePlugin;
    private static int MIN_Y = -1;
    private static int MAX_X = -1;
    private static int MAX_Y = -1;
    private static Timer timeHide = new Timer();
    private static boolean IsSetTimer = false;
    private static final AtomicBoolean actionClick = new AtomicBoolean();
    private static Object CacheSession;

    public static void onShowEvent(boolean IsShow, Object session) {
        CacheSession = session;
        Activity act = Utils.getTopActivity();
        ResUtils.StartInject(act);
        if (MIN_Y == -1) {
            int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                MIN_Y = act.getResources().getDimensionPixelSize(resourceId);
            }
        }
        if (MAX_X == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            MAX_X = metrics.widthPixels - Utils.dip2px(act, 32);
            MAX_Y = MIN_Y + metrics.heightPixels - Utils.dip2px(act, 32);
        }

        if (IsShow && IsAvailable(QQSessionUtils.getGroupUin(session), !(QQSessionUtils.getSessionID(session) == 0 || QQSessionUtils.getSessionID(session) == 1000),1)) {

            if (act == cacheAct) {
                if (!IsAdd.getAndSet(true)) {
                    manager.addView(mPluginButton, getParam(act));
                }
            } else {
                if (IsAdd.getAndSet(false)) {
                    manager.removeViewImmediate(mPluginButton);
                }

                manager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);

                InitImageButton(act);
                manager.addView(mPluginButton, getParam(act));

                IsAdd.getAndSet(true);

                cacheAct = act;
                requestTimer();
            }
        } else {
            if (manager != null && IsAdd.getAndSet(false)) {
                removeTimer();
                manager.removeViewImmediate(mPluginButton);
            }

        }
    }

    public static boolean IsAvailable(String TroopUin, boolean IsTroop,int type) {
        HashMap<String, PluginInfo> AvailPlugin = PluginController.checkHasAvailMenu(TroopUin, IsTroop,type);
        cachePlugin = AvailPlugin;
        return !AvailPlugin.isEmpty();
    }

    private static void removeTimer() {
        if (IsSetTimer) {
            timeHide.cancel();
            IsSetTimer = false;
        }
    }

    private static void requestTimer() {
        removeTimer();
        timeHide = new Timer();
        timeHide.schedule(new TimerTask() {
            @Override
            public void run() {
                IsSetTimer = false;
                new Handler(Looper.getMainLooper()).post(FloatWindowControl::ChangeColor);
            }
        }, 3000);
        IsSetTimer = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private static void InitImageButton(Context context) {
        mPluginButton = new ImageView(context);
        mPluginButton.setImageResource(R.drawable.plugin_btn);
        mPluginButton.setAdjustViewBounds(true);
        mPluginButton.setMaxHeight(Utils.dip2px(context, 32));
        mPluginButton.setMaxWidth(Utils.dip2px(context, 32));

        mPluginButton.setOnClickListener(v -> {
            if (actionClick.get()) {
                ShowButtonDialog(CacheSession,1,null);
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

                        if (sParams.x < MIN_X) sParams.x = MIN_X;
                        if (sParams.y < MIN_Y) sParams.y = MIN_Y;
                        if (sParams.x > MAX_X) sParams.x = MAX_X;
                        if (sParams.y > MAX_Y) sParams.y = MAX_Y;

                        sumX += Math.abs(movedX);
                        sumY += Math.abs(movedY);

                        // 更新悬浮窗控件布局
                        manager.updateViewLayout(mPluginButton, sParams);


                        if (sumX > 4 || sumY > 4) {
                            actionClick.getAndSet(false);
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        HookEnv.Config.setInt("Window_Position", "Plugin_Button_y", sParams.y);
                        HookEnv.Config.setInt("Window_Position", "Plugin_Button_x", sParams.x);
                        new Handler(Looper.getMainLooper()).postDelayed(() -> actionClick.getAndSet(true), 200);
                        //new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::StickToSize,80);

                        requestTimer();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public static void ChangeColor() {
        if (IsAdd.get()) {
            if (mPluginButton.getDrawable().getAlpha() > 100) {
                mPluginButton.getDrawable().setAlpha(mPluginButton.getDrawable().getAlpha() - 10);
                new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::ChangeColor, 20);
            }

        }

    }

    public static void StickToSize() {
        if (IsAdd.get()) {
            if (sParams.x > MIN_X && sParams.x < MAX_X) {
                int middle = MAX_X / 2;
                if (sParams.x < middle) {
                    sParams.x = sParams.x - 20;
                    manager.updateViewLayout(mPluginButton, sParams);
                    new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::StickToSize, 6);
                } else {
                    sParams.x = sParams.x + 20;
                    manager.updateViewLayout(mPluginButton, sParams);
                    new Handler(Looper.getMainLooper()).postDelayed(FloatWindowControl::StickToSize, 6);
                }

            }
        }

    }

    public static WindowManager.LayoutParams getParam(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = Utils.dip2px(context, 32);
        layoutParams.height = Utils.dip2px(context, 32);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        sParams = layoutParams;

        int x = HookEnv.Config.getInt("Window_Position", "Plugin_Button_x", 0);
        int y = HookEnv.Config.getInt("Window_Position", "Plugin_Button_y", 300);
        int middle = MAX_X / 2;
        sParams.y = y;
        if (x < middle) {
            sParams.x = 0;
        } else {
            sParams.x = MAX_X;
        }


        return layoutParams;
    }

    public static void ShowButtonDialog(Object Session,int typeAA,Object chatMsg) {
        Context fixContext = new ContextFixUtil.FixContext(Utils.getTopActivity());
        LayoutInflater inflater = ContextFixUtil.getContextInflater(Utils.getTopActivity());
        BottomPopupView view = new BottomPopupView(fixContext) {
            @Override
            protected int getImplLayoutId() {
                return R.layout.plugin_inchat_menu;
            }

            @Override
            protected int getMaxHeight() {
                return fixContext.getResources().getDisplayMetrics().heightPixels / 2;
            }

            @Override
            protected void onCreate() {
                LinearLayout mList = findViewById(R.id.plugin_menu_list);
                TextView sub_groupuin = findViewById(R.id.sub_groupuin);
                String sub_title = "当前";

                //从SessionInfo中获取当前的聊天窗口信息并显示,防止有时混群发送的问题
                int type = QQSessionUtils.getSessionID(Session);
                if (type == 1) {

                    sub_title = "群聊:" + QQGroupUtils.Group_Get_Name(QQSessionUtils.getGroupUin(Session)) + "(" + QQSessionUtils.getGroupUin(Session) + ")";
                } else if (type == 0) {

                    sub_title = "好友:" + QQEnvUtils.getFriendName(QQSessionUtils.getFriendUin(Session)) + "(" + QQSessionUtils.getFriendUin(Session) + ")";
                } else if (type == 1000) {
                    sub_title = "私聊:" + QQGroupUtils.Group_Get_Member_Name(QQSessionUtils.getGroupUin(Session), QQSessionUtils.getFriendUin(Session)) + "(" + QQSessionUtils.getFriendUin(Session) + ")";
                    sub_title += "->" + QQGroupUtils.Group_Get_Name(QQSessionUtils.getGroupUin(Session)) + "(" + QQSessionUtils.getGroupUin(Session) + ")";
                } else if (type == 10014) {
                    sub_title = "[频道]:" + QQGuildManager.GetGuildName(QQSessionUtils.getGuildID(Session)) + "(" + QQSessionUtils.getGuildID(Session) + ")";
                    sub_title += "->" + QQGuildManager.GetChannelName(QQSessionUtils.getGuildID(Session), QQSessionUtils.getChannelID(Session))
                            + "(" + QQSessionUtils.getChannelID(Session) + ")";
                }
                sub_groupuin.setText(sub_title);

                for (String VerifyKey : cachePlugin.keySet()) {
                    PluginInfo info = cachePlugin.get(VerifyKey);


                    RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.plugin_inchat_menu_item, null);
                    TextView title = item.findViewById(R.id.plugin_title);
                    title.setText(info.PluginName);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    param.setMargins(Utils.dip2px(getContext(), 20), Utils.dip2px(getContext(), 10), Utils.dip2px(getContext(), 20), Utils.dip2px(getContext(), 10));

                    if (!TextUtils.isEmpty(info.ItemClickFunctionName))
                        PluginController.InvokeToPreCheckItem(Session, info.ItemClickFunctionName, info);

                    if (info.ItemFunctions.size() == 0) continue;
                    LinearLayout menu_items = item.findViewById(R.id.menu_items);
                    for (String itemKey : info.ItemFunctions.keySet()) {
                        PluginController.ItemInfo itemInfo = info.ItemFunctions.get(itemKey);
                        if (itemInfo.itemType == typeAA) {
                            TextView newView = (TextView) inflater.inflate(R.layout.plugin_item_menu_item, null);
                            newView.setText(" - " + itemInfo.ItemName);

                            LinearLayout.LayoutParams parambb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            parambb.setMargins(Utils.dip2px(getContext(), 5), Utils.dip2px(getContext(), 5), Utils.dip2px(getContext(), 5), Utils.dip2px(getContext(), 5));

                            newView.setOnClickListener(v -> {
                                PluginController.InvokeToPluginItem(Session, itemInfo.CallbackName, info,typeAA,chatMsg);
                                dismiss();
                            });
                            menu_items.addView(newView, parambb);
                        }
                    }
                    mList.addView(item, param);
                }
                super.onCreate();
            }
        };
        XPopup.Builder NewPop = new XPopup.Builder(fixContext);
        BasePopupView base = NewPop.asCustom(view);
        base.show();
    }
}
