package cc.hicore.qtool.JavaPlugin.ListForm;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.EnvHook;

public class OnlinePluginItemController {
    private static final String TAG = "OnlinePluginItemController";
    private PluginInfo cacheInfo;
    private View cacheView;
    private int MeasureHeight;

    public static View getViewInstance(Context context, PluginInfo NewInfo) {
        OnlinePluginItemController NewController = new OnlinePluginItemController(context, NewInfo);
        return NewController.cacheView;
    }

    private OnlinePluginItemController(Context context, PluginInfo NewInfo) {
        cacheInfo = NewInfo;

        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.plugin_item_online, null);
        cacheView = layout;
        ((TextView) layout.findViewById(R.id.plugin_title_online)).setText(NewInfo.Name + "(" + NewInfo.Version + ")");
        ((TextView) layout.findViewById(R.id.plugin_uploader)).setText("作者:" + NewInfo.Author);
        ((TextView) layout.findViewById(R.id.plugin_download_count)).setText("下载次数:" + NewInfo.DLCount);
        ((TextView) layout.findViewById(R.id.plugin_upload_time)).setText("上传时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(NewInfo.Time)));
        ((TextView) layout.findViewById(R.id.plugin_size)).setText("脚本包大小:" + Utils.bytes2kb(NewInfo.size));
        ((TextView) layout.findViewById(R.id.plugin_desc_online)).setText(NewInfo.Desc);

        Button btnDown = layout.findViewById(R.id.plugin_download);
        btnDown.setOnClickListener(v -> {
            String reqForDL = HttpUtils.getContent("https://qtool.haonb.cc/reqDL?key=" + cacheInfo.ID);
            try {
                JSONObject NewJson = new JSONObject(reqForDL);
                if (NewJson.getInt("code") == 3) {
                    Utils.ShowToastL(NewJson.getString("msg"));
                    return;
                }
                String url = NewJson.getString("url");
                EnvHook.requireCachePath();
                String ZipPath = HookEnv.ExtraDataPath + "/Cache/" + cacheInfo.ID.hashCode() + ".zip";
                HttpUtils.ProgressDownload(url, ZipPath, () -> {
                    try {
                        String rootPath = HookEnv.ExtraDataPath + "/Plugin/" + FileUtils.requireForAvailPath(cacheInfo.Name) + "/";
                        if (!new File(rootPath).exists()) new File(rootPath).mkdirs();
                        ZipInputStream zInp = new ZipInputStream(new FileInputStream(ZipPath));
                        ZipEntry entry;
                        while ((entry = zInp.getNextEntry()) != null) {
                            String Name = entry.getName();
                            String WritePath = rootPath + Name;
                            File parent = new File(WritePath).getParentFile();
                            if (!parent.exists()) parent.mkdirs();

                            FileUtils.WriteToFile(WritePath, DataUtils.readAllBytes(zInp));
                        }
                        zInp.close();
                        Utils.ShowToastL("下载完成");
                    } catch (Exception e) {
                        Utils.ShowToastL("无法释放文件\n" + e);
                    }

                }, cacheView.getContext());
            } catch (Exception e) {
                Utils.ShowToastL("无法下载:\n" + e);
            }
        });

        //初始化动画
        LinearLayout ll = layout.findViewById(R.id.HideBar_Online);
        ll.setVisibility(View.VISIBLE);
        ll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                MeasureHeight = (int) (ll.getMeasuredHeight());
                ll.setVisibility(View.GONE);
            }
        });

        initAnim();


    }

    private void initAnim() {

        AtomicBoolean IsExpanding = new AtomicBoolean();
        AtomicBoolean IsExpanded = new AtomicBoolean();
        cacheView.setOnClickListener(v -> {
            if (IsExpanding.get()) return;
            if (IsExpanded.get()) {
                LinearLayout lExpand = cacheView.findViewById(R.id.HideBar_Online);
                lExpand.getLayoutParams().height = MeasureHeight;
                lExpand.requestLayout();
                int targetHeight = 0;
                IsExpanding.getAndSet(true);
                Animation scale = new Animation() {
                    int initialHeight;

                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        lExpand.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
                        lExpand.requestLayout();
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
                lExpand.startAnimation(scale);
                lExpand.setVisibility(View.INVISIBLE);

                new Handler(Looper.getMainLooper())
                        .postDelayed(() -> {
                            lExpand.setVisibility(View.GONE);
                            IsExpanding.getAndSet(false);
                            IsExpanded.getAndSet(false);
                            lExpand.clearAnimation();
                        }, 500);
            } else {
                LinearLayout lExpand = cacheView.findViewById(R.id.HideBar_Online);
                lExpand.getLayoutParams().height = 0;
                lExpand.requestLayout();
                int targetHeight = MeasureHeight;
                IsExpanding.getAndSet(true);
                Animation scale = new Animation() {
                    int initialHeight;

                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        lExpand.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
                        lExpand.requestLayout();
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
                lExpand.startAnimation(scale);
                lExpand.setVisibility(View.INVISIBLE);

                new Handler(Looper.getMainLooper())
                        .postDelayed(() -> {
                            lExpand.setVisibility(View.VISIBLE);
                            IsExpanding.getAndSet(false);
                            IsExpanded.getAndSet(true);
                            lExpand.clearAnimation();
                        }, 500);
            }
        });
    }

    static class PluginInfo {
        String Name;
        String ID;
        String Author;
        String Version;
        String Desc;
        long Time;
        int DLCount;
        long size;


        public PluginInfo(String JSON) {
            try {
                JSONObject NewJson = new JSONObject(JSON);
                ID = NewJson.optString("PluginID");
                Name = NewJson.optString("PluginName");
                Author = NewJson.optString("PluginAuthor");
                Version = NewJson.optString("PluginVersion");
                Desc = NewJson.optString("Desc");
                Time = NewJson.optLong("UploadTime");
                DLCount = NewJson.optInt("DownloadCount");
                size = NewJson.optInt("Size");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
