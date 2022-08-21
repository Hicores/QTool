package cc.hicore.qtool.QQTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.Utils.DebugUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQManager.QQGuildManager;
import cc.hicore.qtool.R;
import de.robv.android.xposed.XposedBridge;

/*
显示QQ好友,群聊,频道选择界面,并回调数据
 */
public class QQSelectHelper {

    public interface onSelected {
        void onGroupSelect(ArrayList<String> uin);

        void onFriendSelect(ArrayList<String> uin);

        void onGuildSelect(HashMap<String, HashSet<String>> guilds);

        void onAllSelected();
    }

    private boolean isShowTroop;
    private boolean isShowFriend;
    private boolean isShowGuild;
    private Context mContext;
    LayoutInflater inflater = null;
    LinearLayout mContainer;

    private int CurrentType = 0;

    private ArrayList<String> selectGroup = new ArrayList<>();
    private ArrayList<String> selectFriend = new ArrayList<>();
    private HashMap<String, HashSet<String>> selectGuild = new HashMap<>();

    public QQSelectHelper(Context context, boolean isTroop, boolean isFriend, boolean isGuild) {
        mContext = context;
        isShowTroop = isTroop;
        isShowFriend = isFriend;
        isShowGuild = isGuild;
    }

    public void setSelectedGroup(ArrayList<String> selectGroup) {
        this.selectGroup = selectGroup;
    }

    public void setSelectedFriend(ArrayList<String> selectFriend) {
        this.selectFriend = selectFriend;
    }

    public void setSelectedGuildChannel(HashMap<String, HashSet<String>> selectGuild) {
        this.selectGuild = selectGuild;
    }

    private List<CheckBox> checkBoxs;

    private void SwitchToGroup() {
        mContainer.removeAllViews();
        ArrayList<QQGroupUtils.GroupInfo> groupList = QQGroupUtils.Group_Get_List();
        checkBoxs = new ArrayList<>();
        for (QQGroupUtils.GroupInfo info : groupList) {
            LinearLayout item = (LinearLayout) inflater.inflate(R.layout.select_item, null);

            RoundImageView image = item.findViewById(R.id.HeadView);
            image.setImagePath(String.format("https://p.qlogo.cn/gh/%s/%s/140", info.Uin, info.Uin));
            TextView Name = item.findViewById(R.id.SetName);
            Name.setText(info.Name + "(" + info.Uin + ")");

            CheckBox itemSwitch = item.findViewById(R.id.SelectSwitch);
            itemSwitch.setChecked(selectGroup.contains(info.Uin));
            itemSwitch.setOnCheckedChangeListener((v, ischeck) -> {
                if (ischeck) selectGroup.add(info.Uin);
                else selectGroup.remove(info.Uin);
            });
            checkBoxs.add(itemSwitch);

            mContainer.addView(item);
        }

        CurrentType = 1;
    }

    private void SwitchToFriend() {
        mContainer.removeAllViews();
        ArrayList<QQEnvUtils.FriendInfo> friendList = QQEnvUtils.getFriendList();
        checkBoxs = new ArrayList<>();
        for (QQEnvUtils.FriendInfo info : friendList) {
            LinearLayout item = (LinearLayout) inflater.inflate(R.layout.select_item, null);

            RoundImageView image = item.findViewById(R.id.HeadView);
            image.setImagePath(String.format("https://q4.qlogo.cn/g?b=qq&nk=%s&s=140", info.Uin));
            TextView Name = item.findViewById(R.id.SetName);
            Name.setText(info.Name + "(" + info.Uin + ")");

            CheckBox itemSwitch = item.findViewById(R.id.SelectSwitch);
            itemSwitch.setChecked(selectFriend.contains(info.Uin));
            itemSwitch.setOnCheckedChangeListener((v, ischeck) -> {
                if (ischeck) selectFriend.add(info.Uin);
                else selectFriend.remove(info.Uin);
            });
            checkBoxs.add(itemSwitch);

            mContainer.addView(item);
        }
        CurrentType = 2;
    }

    private void SwitchToGuild() {
        mContainer.removeAllViews();
        ArrayList<QQGuildManager.Guild_Info> guildList = QQGuildManager.Guild_Get_List();
        for (QQGuildManager.Guild_Info info : guildList) {

            ArrayList<QQGuildManager.Channel_Info> channelList = QQGuildManager.Channel_Get_List(info.GuildID);
            for (QQGuildManager.Channel_Info channel : channelList) {
                LinearLayout item = (LinearLayout) inflater.inflate(R.layout.select_item, null);

                RoundImageView image = item.findViewById(R.id.HeadView);
                image.setImagePath(info.AvatarUrl);
                TextView Name = item.findViewById(R.id.SetName);
                Name.setText(info.GuildName + "&" + channel.ChannelName + "(" + info.GuildID + "->" + channel.ChannelID + ")");

                CheckBox itemSwitch = item.findViewById(R.id.SelectSwitch);
                itemSwitch.setChecked(IsChannelSelect(info.GuildID, channel.ChannelID));
                itemSwitch.setOnCheckedChangeListener((v, ischeck) -> {
                    SetChannelSelect(info.GuildID, channel.ChannelID, ischeck);
                });
                checkBoxs.add(itemSwitch);

                mContainer.addView(item);
            }
        }
        CurrentType = 3;
    }

    private boolean IsChannelSelect(String GuildID, String ChannelID) {
        HashSet<String> channelInfo = selectGuild.get(GuildID);
        if (channelInfo == null) return false;
        return channelInfo.contains(ChannelID);
    }

    private void SetChannelSelect(String GuildID, String ChannelID, boolean isSet) {
        if (isSet) {
            HashSet<String> channelInfo = selectGuild.computeIfAbsent(GuildID, k -> new HashSet<>());
            channelInfo.add(ChannelID);
        } else {
            HashSet<String> channelInfo = selectGuild.get(GuildID);
            if (channelInfo == null) return;
            channelInfo.remove(ChannelID);
            if (channelInfo.size() == 0) {
                selectGuild.remove(GuildID);
            }
        }
    }

    public void startShow(onSelected callback, int defTab) {
        try {
            if (defTab < 1 || defTab > 3) throw new RuntimeException("defTab must be 1-3");

            Context fixContext = new ContUtil.FixContext(mContext);
            inflater = ContUtil.getContextInflater(mContext);
            BottomPopupView view = new BottomPopupView(fixContext) {
                @Override
                protected int getImplLayoutId() {
                    return R.layout.select_dialog;
                }

                @Override
                protected void onCreate() {
                    super.onCreate();
                    mContainer = findViewById(R.id.selectContent);
                    if (defTab == 1)
                        ((RadioButton) findViewById(R.id.select_group)).setChecked(true);
                    if (defTab == 2)
                        ((RadioButton) findViewById(R.id.select_friend)).setChecked(true);
                    if (defTab == 3)
                        ((RadioButton) findViewById(R.id.select_guild)).setChecked(true);

                    if (!isShowTroop)
                        ((RadioButton) findViewById(R.id.select_group)).setVisibility(GONE);
                    if (!isShowFriend)
                        ((RadioButton) findViewById(R.id.select_friend)).setVisibility(GONE);
                    if (!isShowGuild)
                        ((RadioButton) findViewById(R.id.select_guild)).setVisibility(GONE);

                    if (defTab == 1) {
                        SwitchToGroup();
                    } else if (defTab == 2) {
                        SwitchToFriend();
                    } else if (defTab == 3) {
                        SwitchToGuild();
                    }
                    ((RadioButton) findViewById(R.id.select_group)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (buttonView.isPressed() && isChecked) {
                            SwitchToGroup();
                        }
                    });
                    ((RadioButton) findViewById(R.id.select_friend)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (buttonView.isPressed() && isChecked) {
                            SwitchToFriend();
                        }
                    });
                    ((RadioButton) findViewById(R.id.select_guild)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (buttonView.isPressed() && isChecked) {
                            SwitchToGuild();
                        }
                    });


                    findViewById(R.id.selectAll).setOnClickListener(v -> {
                        for (CheckBox ch : checkBoxs) {
                            ch.setChecked(true);
                        }
                    });
                    findViewById(R.id.selectBack).setOnClickListener(v -> {
                        for (CheckBox ch : checkBoxs) {
                            ch.setChecked(!ch.isChecked());
                        }
                    });
                }

                @Override
                protected void onDismiss() {
                    super.onDismiss();
                    try {
                        if (isShowFriend) callback.onFriendSelect(selectFriend);
                        if (isShowTroop) callback.onGroupSelect(selectGroup);
                        if (isShowGuild) callback.onGuildSelect(selectGuild);
                        callback.onAllSelected();
                    } catch (Exception e) {
                        LogUtils.warning("QQSelectHelper", "dismiss callback exception:\n" + e);
                    }

                }
            };
            XPopup.Builder NewPop = new XPopup.Builder(fixContext);
            BasePopupView base = NewPop.asCustom(view);
            base.show();
        } catch (Exception e) {
            LogUtils.error("QQSelectHelper", DebugUtils.getLinkStackMsg(e));
            Utils.ShowToast("选择器加载失败：\n" + e);
        }


    }

    @SuppressLint("AppCompatCustomView")
    public static class RoundImageView extends ImageView {
        private static ExecutorService dlPool = Executors.newFixedThreadPool(16);
        private Paint mPaint;
        private Matrix mMatrix;

        public RoundImageView(Context context) {
            super(context);
            init();
        }

        public RoundImageView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        //进行画笔初始化
        private void init() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mMatrix = new Matrix();
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁用硬加速
        }

        //重写onDraw()方法获取BitmapDrawable进行处理
        @Override
        protected void onDraw(Canvas canvas) {
            Drawable drawable = getDrawable();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                drawRoundByShaderMode(canvas, bitmap);
            } else {
                super.onDraw(canvas);
            }
        }

        public void setImagePath(String ImagePath) {
            dlPool.submit(() -> {
                try {
                    InputStream ins = new URL(ImagePath).openStream();
                    Drawable drawable = DrawableWrapper.createFromStream(ins, ImagePath);
                    ins.close();
                    new Handler(Looper.getMainLooper())
                            .post(() -> this.setBackground(drawable));
                } catch (Exception e) {
                    LogUtils.warning("HeadLoader", "load failed(" + ImagePath + "):\n" + e);
                }

            });
        }

        //把头像画成圆的(似乎不起作用)
        private void drawRoundByShaderMode(Canvas canvas, Bitmap bitmap) {
            //获取到Bitmap的宽高
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            //获取到ImageView的宽高
            int viewWidth = getWidth();
            int viewHeight = getHeight();

            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mMatrix.reset();

            float minScale = Math.min(viewWidth / (float) bitmapWidth, viewHeight / (float) bitmapHeight);
            mMatrix.setScale(minScale, minScale);
            bitmapShader.setLocalMatrix(mMatrix);
            mPaint.setShader(bitmapShader);

            //绘制圆形
            canvas.drawCircle(bitmapWidth / 2, bitmapHeight / 2, Math.min(bitmapWidth / 2, bitmapHeight / 2), mPaint);
        }
    }
}
