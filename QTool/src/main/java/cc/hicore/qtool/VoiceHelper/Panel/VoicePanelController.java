package cc.hicore.qtool.VoiceHelper.Panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import java.io.File;
import java.util.ArrayList;

import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.R;

public final class VoicePanelController extends BottomPopupView {
    int ControllerMode = 0;
    private VoiceProvider provider;

    private VerticalRecyclerView recyclerView;
    private FrameLayout mFrame;
    private EasyAdapter<VoiceProvider.FileInfo> commonAdapter;
    private TextView showPath;

    private ArrayList<VoiceProvider.FileInfo> resultFile = new ArrayList<>();

    @Override
    protected void onCreate() {
        super.onCreate();
        initSelectBar();
        initSearchBox();
        showPath = findViewById(R.id.currentPath);
        mFrame = findViewById(R.id.ExtraView);

        recyclerView = findViewById(R.id.recyclerView);
        commonAdapter = new EasyAdapter<VoiceProvider.FileInfo>(resultFile, R.layout.voice_panel_item) {
            @Override
            protected void bind(@NonNull ViewHolder viewHolder, VoiceProvider.FileInfo fileInfo, int i) {
                RelativeLayout mItem = (RelativeLayout) viewHolder.getConvertView();
                ImageView image = mItem.findViewById(R.id.mIcon);
                if (fileInfo.type == 1)
                    image.setImageResource(R.drawable.voice_item);
                else image.setImageResource(R.drawable.folder);

                ImageView clickButton = mItem.findViewById(R.id.sendButton);
                clickButton.setVisibility((fileInfo.type == 1 || fileInfo.type == 6) ? VISIBLE : GONE);
                if (fileInfo.type == 1) {
                    clickButton.setOnClickListener(v -> {
                        QQMsgSender.sendVoice(HookEnv.SessionInfo, fileInfo.Path);
                        dismiss();
                    });
                }else {
                    clickButton.setOnLongClickListener(null);
                }

                TextView title = mItem.findViewById(R.id.voice_name);
                title.setText(fileInfo.Name);

                //设置目录和语音的点击信息
                if (fileInfo.type == 1) {
                    mItem.setOnClickListener(null);
                } else if (fileInfo.type == 2) {
                    mItem.setOnClickListener(v -> {
                        provider = provider.getChild(fileInfo.Name);
                        UpdateProviderDate();
                    });
                } else if (fileInfo.type == -1) {
                    mItem.setOnClickListener(v -> {
                        provider = provider.getParent();
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(null);
                }
            }
        };
        recyclerView.setAdapter(commonAdapter);

        UpdateControlData();
    }
    private ArrayList<String> searchVoices(String path){
        File[] fs = new File(path).listFiles();
        if (fs == null)return new ArrayList<>();
        ArrayList<String> ret = new ArrayList<>();
        for (File f : fs){
            if (f.isFile()){
                ret.add(f.getAbsolutePath());
            }else if (f.isDirectory()){
                ret.addAll(searchVoices(f.getAbsolutePath()));
            }
        }
        return ret;
    }

    private VoiceProvider cacheProvider;

    private void initSearchBox() {
        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    EditText searchBox = new EditText(getContext());
                    searchBox.setHint("输入名字即可搜索");
                    searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                    searchBox.setSingleLine();
                    searchBox.setFocusable(true);
                    searchBox.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            if (ControllerMode == 0) {
                                String search = v.getText().toString();
                                if (search.length() == 0) {
                                    if (cacheProvider != null) {
                                        provider = cacheProvider;
                                        cacheProvider = null;
                                        UpdateProviderDate();
                                    }
                                } else {
                                    if (cacheProvider == null) {
                                        cacheProvider = provider;
                                    }
                                    provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_LOCAL_SEARCH + search);
                                    UpdateProviderDate();
                                }
                            }

                        }
                        return false;
                    });

                    ((LinearLayout) findViewById(R.id.currentPath).getParent()).addView(searchBox, 1);
                }, 200);

    }

    private void initSelectBar() {
        LinearLayout topBar = findViewById(R.id.selectItem);
        ImageView imageLocalFile = new ImageView(getContext());
        imageLocalFile.setImageResource(R.drawable.voice_file);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(), 25), Utils.dip2px(getContext(), 25));
        param.setMargins(Utils.dip2px(getContext(), 12), 10, Utils.dip2sp(getContext(), 5), 10);
        topBar.addView(imageLocalFile, param);

        imageLocalFile.setOnClickListener(v -> {
            ControllerMode = 0;
            UpdateControlData();
        });

        imageLocalFile = new ImageView(getContext());
        imageLocalFile.setImageResource(R.drawable.convert);
        param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(), 25), Utils.dip2px(getContext(), 25));
        param.setMargins(Utils.dip2px(getContext(), 12), 10, Utils.dip2sp(getContext(), 5), 10);
        topBar.addView(imageLocalFile, param);

        imageLocalFile.setOnClickListener(v -> {
            recyclerView.setVisibility(GONE);
            mFrame.setVisibility(VISIBLE);

            mFrame.removeAllViews();
            mFrame.addView(TTSPanel.initView(getContext()));
        });
    }

    public void UpdateProviderDate() {
        recyclerView.setVisibility(VISIBLE);
        mFrame.setVisibility(GONE);

        resultFile.clear();
        ProgressDialog dialog = new ProgressDialog(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT);
        dialog.setTitle("请稍后...");
        dialog.setMessage("加载中...");
        dialog.setCancelable(false);
        if (ControllerMode == 1) {
            dialog.show();
        }
        new Thread(() -> {
            try {
                resultFile.addAll(provider.getList());
            } finally {
                new Handler(Looper.getMainLooper()).post(() -> {
                    dialog.dismiss();
                    commonAdapter.notifyDataSetChanged();
                });
            }
        }).start();

        showPath.setText(provider.getPath());
    }

    public void UpdateControlData() {
        if (ControllerMode == 0) {
            provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_LOCAL_FILE);
            UpdateProviderDate();
        }
    }
    public VoicePanelController(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.voice_base_panel;
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }

    @Override
    protected int getPopupHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }
}
