package com.hicore.qtool.QQTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.QQManager.QQGroupUtils;
import com.hicore.qtool.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.XposedBridge;

public class QQSelectHelper {

    public interface onSelected{
        void onGroupSelect(ArrayList<String> uin);
        void onFriendSelect(ArrayList<String> uin);
        void onGuildSelect(HashMap<String,ArrayList<String>> guilds);
    }
    private static class FixResClassLoader extends ClassLoader{
        protected FixResClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try{
                Class<?> clz = super.loadClass(name);
                if (clz != null)return clz;
            }catch (Exception e){

            }
            return findClass(name);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try{
                Class<?> clz = super.loadClass(name, resolve);
                if (clz != null)return clz;
            }catch (Exception e){

            }
            return findClass(name);

        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try{
                Class<?> clz = super.findClass(name);
                if (clz != null)return clz;
            }catch (Exception e){

            }
            return HookEnv.moduleLoader.loadClass(name);
        }
    }
    private static class FixContext extends ContextWrapper {
        private ClassLoader mFixLoader;

        @Override
        public ClassLoader getClassLoader() {
            if (mFixLoader != null)return mFixLoader;
            return super.getClassLoader();
        }

        public FixContext(Context base) {
            super(base);
            mFixLoader = new FixResClassLoader(base.getClassLoader());
        }
    }
    private boolean isShowTroop;
    private boolean isShowFriend;
    private boolean isShowGuild;
    private Context mContext;

    private ArrayList<String> selectGroup = new ArrayList<>();
    private ArrayList<String> selectFriend = new ArrayList<>();
    private HashMap<String,ArrayList<String>> selectGuild = new HashMap<>();
    public QQSelectHelper(Context context,boolean isTroop,boolean isFriend,boolean isGuild){
        mContext = context;
        isShowTroop = isTroop;
        isShowFriend = isFriend;
        isShowGuild = isGuild;
    }
    public void setSelectedGroup(ArrayList<String> selectGroup){
        this.selectGroup = selectGroup;
    }
    public void setSelectedFriend(ArrayList<String> selectFriend){
        this.selectFriend = selectFriend;
    }
    public void setSelectedGuildChannel(HashMap<String,ArrayList<String>> selectGuild){
        this.selectGuild = selectGuild;
    }
    public void startShow(onSelected callback,int defTab){
        try{
            if (defTab < 1 || defTab > 3)throw new RuntimeException("defTab must be 1-3");

            Context fixContext = new FixContext(mContext);

            LayoutInflater inflater = null;
            try {
                XPBridge.HookAfterOnce(LayoutInflater.class.getMethod("from", Context.class),param -> {
                    LayoutInflater inflater1 = (LayoutInflater) param.getResult();
                    param.setResult(inflater1.cloneInContext(fixContext));
                });
                inflater = LayoutInflater.from(fixContext);
                XPBridge.HookAfterOnce(LayoutInflater.class.getMethod("from", Context.class),param -> {
                    LayoutInflater inflater1 = (LayoutInflater) param.getResult();
                    param.setResult(inflater1.cloneInContext(fixContext));
                });
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            LayoutInflater finalInflater = inflater;
            BottomPopupView view =new BottomPopupView(fixContext){
                @Override
                protected int getImplLayoutId() {
                    return R.layout.select_dialog;
                }

                @Override
                protected void onCreate() {
                    super.onCreate();
                    LinearLayout mContainer = findViewById(R.id.selectContent);
                    if (defTab == 1) ((RadioButton)findViewById(R.id.select_group)).setChecked(true);
                    if (defTab == 2) ((RadioButton)findViewById(R.id.select_friend)).setChecked(true);
                    if (defTab == 3) ((RadioButton)findViewById(R.id.select_guild)).setChecked(true);

                    if (defTab == 1){
                        ArrayList<QQGroupUtils.GroupInfo> groupList = QQGroupUtils.Group_Get_List();
                        for (QQGroupUtils.GroupInfo info : groupList){
                            LinearLayout item = (LinearLayout) finalInflater.inflate(R.layout.select_item,null);

                            RoundImageView image = item.findViewById(R.id.HeadView);
                            image.setImagePath(String.format("https://p.qlogo.cn/gh/%s/%s/140",info.Uin,info.Uin));
                            TextView Name = item.findViewById(R.id.SetName);
                            Name.setText(info.Name+"("+info.Uin+")");

                            mContainer.addView(item);
                        }
                    }else if (defTab == 2){
                        ArrayList<QQEnvUtils.FriendInfo> friendList = QQEnvUtils.getFriendList();
                        for (QQEnvUtils.FriendInfo info : friendList){
                            LinearLayout item = (LinearLayout) finalInflater.inflate(R.layout.select_item,null);

                            RoundImageView image = item.findViewById(R.id.HeadView);
                            image.setImagePath(String.format("https://q4.qlogo.cn/g?b=qq&nk=%s&s=140",info.Uin));
                            TextView Name = item.findViewById(R.id.SetName);
                            Name.setText(info.Name+"("+info.Uin+")");

                            mContainer.addView(item);
                        }
                    }else if (defTab == 3){

                    }

                }
            };
            new XPopup.Builder(fixContext)
                    .asCustom(view)
                    .show();
        }catch (Exception e){
            Utils.ShowToast("选择器加载失败：\n"+e);
        }



    }
    @SuppressLint("AppCompatCustomView")
    public static class RoundImageView extends ImageView{
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
        public void setImagePath(String ImagePath){
            dlPool.submit(()->{
                try{
                    InputStream ins = new URL(ImagePath).openStream();
                    Drawable drawable = DrawableWrapper.createFromStream(ins,ImagePath);
                    ins.close();
                    new Handler(Looper.getMainLooper())
                            .post(()->this.setBackground(drawable));
                }catch (Exception e){
                    LogUtils.warning("HeadLoader","load failed("+ImagePath+"):\n"+e);
                }

            });
        }

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
