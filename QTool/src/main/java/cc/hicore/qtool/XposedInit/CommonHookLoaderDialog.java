package cc.hicore.qtool.XposedInit;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQTools.ContextFixUtil;
import cc.hicore.qtool.R;

public class CommonHookLoaderDialog {
    private final Context mContext;
    private TextView progressTitle;
    private ProgressBar progressBar;
    private final CenterPopupView popupView;
    private BasePopupView popupDialog;

    public CommonHookLoaderDialog(Context context) {
        mContext = context;
        if (mContext == null){
            popupView = null;
            return;
        }
        releaseJsonFile();
        popupView = new CenterPopupView(ContextFixUtil.getFixContext(context)) {
            @Override
            protected int getImplLayoutId() {
                return R.layout.loading_progress;
            }

            @Override
            protected void onCreate() {
                super.onCreate();

                LinearLayout root = findViewById(R.id.All_Root);
                LottieAnimationView animationView = new LottieAnimationView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getContext(), 150), Utils.dip2px(getContext(), 150));
                params.topMargin = Utils.dip2px(getContext(), 16);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                root.addView(animationView, 1, params);
                animationView.setAnimationFromJson(FileUtils.ReadFileString(mContext.getCacheDir() + "/loading.json"), null);
                animationView.setRepeatCount(-1);
                animationView.playAnimation();

                progressTitle = findViewById(R.id.ShowProgress);
                progressBar = findViewById(R.id.mBar);
            }

            @Override
            protected int getMaxWidth() {
                return super.getMaxWidth();
            }

            @Override
            protected int getMaxHeight() {
                return Utils.getScreenHeight(getContext());
            }

            @Override
            protected int getPopupHeight() {
                return Utils.getScreenHeight(getContext());
            }
        };
    }

    public void showDialog() {
        if (mContext == null)return;
        popupDialog = new XPopup.Builder(ContextFixUtil.getFixContext(mContext))
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(popupView);
        popupDialog.show();
    }

    public void destroyDialog() {
        if (mContext == null)return;
        if (popupDialog != null) {
            popupDialog.dismiss();
        }
    }

    public void updateProgress(int current, int max) {
        if (mContext == null)return;
        Utils.PostToMain(() -> {
            progressTitle.setText(current + "/" + max);
            progressBar.setMax(max);
            progressBar.setProgress(current);
        });
    }

    private void releaseJsonFile() {
        try {
            ZipInputStream zIns = new ZipInputStream(new FileInputStream(HookEnv.ToolApkPath));
            ZipEntry entry;
            while ((entry = zIns.getNextEntry()) != null) {
                if (entry.getName().equals("assets/loading.json")) {
                    FileOutputStream out = new FileOutputStream(mContext.getCacheDir() + "/loading.json");
                    DataUtils.CopyStream(zIns, out);
                    out.close();
                    break;
                }
            }
            zIns.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
