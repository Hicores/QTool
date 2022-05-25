package cc.hicore.qtool.VoiceHelper.Panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.R;
import cc.hicore.qtool.VoiceHelper.OnlineHelper.OnlineBundleHelper;
import cc.hicore.qtool.XposedInit.EnvHook;

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
                if (fileInfo.type == 1 || fileInfo.type == 6)
                    image.setImageResource(R.drawable.voice_item);
                else image.setImageResource(R.drawable.folder);

                ImageView clickButton = mItem.findViewById(R.id.sendButton);
                clickButton.setVisibility((fileInfo.type == 1 || fileInfo.type == 6) ? VISIBLE : GONE);
                if (fileInfo.type == 1) {
                    clickButton.setOnClickListener(v -> {
                        QQMsgSender.sendVoice(HookEnv.SessionInfo, fileInfo.Path);
                        dismiss();
                    });
                } else if (fileInfo.type == 6) {
                    clickButton.setOnClickListener(v -> {
                        EnvHook.requireCachePath();
                        String cachePath = HookEnv.ExtraDataPath + "Cache/" + fileInfo.Name.hashCode();
                        HttpUtils.ProgressDownload(fileInfo.Path, cachePath, () -> {
                            QQMsgSender.sendVoice(HookEnv.SessionInfo, cachePath);
                            new Handler(Looper.getMainLooper()).post(() -> dismiss());

                        }, getContext());
                    });

                } else {
                    clickButton.setOnLongClickListener(null);
                }

                TextView title = mItem.findViewById(R.id.voice_name);
                title.setText(fileInfo.Name);

                //设置目录和语音的点击信息
                if (fileInfo.type == 1) {
                    mItem.setOnClickListener(null);
                    mItem.setOnLongClickListener(v -> {
                        new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("选择操作")
                                .setItems(new String[]{"删除", "上传"}, (dialog, which) -> {
                                    if (which == 0) {
                                        FileUtils.deleteFile(new File(fileInfo.Path));
                                        UpdateProviderDate();
                                    } else if (which == 1) {
                                        ArrayList<String> paths = new ArrayList<>();
                                        paths.add(fileInfo.Path);
                                        AddVoiceToPacket(paths);
                                    }
                                }).show();
                        return true;
                    });

                } else if (fileInfo.type == 2) {
                    mItem.setOnClickListener(v -> {
                        provider = provider.getChild(fileInfo.Name);
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(v->{
                        new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("选择操作")
                                .setItems(new String[]{"上传"}, (dialog, which) -> {
                                    if (which == 0) {
                                        AddVoiceToPacket(searchVoices(fileInfo.Path));
                                    }
                                }).show();
                        return true;
                    });
                } else if (fileInfo.type == -1) {
                    mItem.setOnClickListener(v -> {
                        provider = provider.getParent();
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(null);
                } else if (fileInfo.type == 5) {
                    mItem.setOnClickListener(v -> {
                        provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_ONLINE + fileInfo.Path);
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(null);
                } else if (fileInfo.type == 6) {
                    mItem.setOnClickListener(null);
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
                            } else if (ControllerMode == 1) {
                                String search = v.getText().toString();
                                if (search.trim().isEmpty()) {
                                    Utils.ShowToastL("必须输入搜索内容");
                                    return true;
                                }
                                provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_ONLINE_SEARCH + search);
                                UpdateProviderDate();
                            } else {
                                Utils.ShowToastL("这里不支持搜索");
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
        imageLocalFile.setImageResource(R.drawable.voice_down);
        param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(), 25), Utils.dip2px(getContext(), 25));
        param.setMargins(Utils.dip2px(getContext(), 12), 10, Utils.dip2sp(getContext(), 5), 10);
        topBar.addView(imageLocalFile, param);

        imageLocalFile.setOnClickListener(v -> {
            ControllerMode = 1;
            UpdateControlData();
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
        } else if (ControllerMode == 1) {
            provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_ONLINE);
            UpdateProviderDate();
        }
    }

    private void AddVoiceToPacket(ArrayList<String> Paths) {
        new Handler(Looper.getMainLooper())
                .post(() -> {
                    ProgressDialog uploadProgress = new ProgressDialog(getContext(), 3);
                    uploadProgress.setTitle("正在处理...");
                    uploadProgress.setMessage("正在上传....");
                    uploadProgress.setCancelable(false);
                    uploadProgress.show();
                    new Thread(() -> {
                        try {
                            int sumAll = Paths.size();
                            int curr = 0;
                            for (String Path : Paths){
                                OnlineBundleHelper.RequestUpload(new File(Path).getName(), Path);
                                curr++;
                                int finalCurr = curr;
                                new Handler(Looper.getMainLooper()).post(()->uploadProgress.setMessage("正在上传("+ finalCurr +"/"+sumAll+")..."));
                            }
                            Utils.ShowToast("上传结束");
                        } catch (Exception e) {
                            Utils.ShowToastL("发生错误:\n" + e);
                        } finally {
                            new Handler(Looper.getMainLooper()).post(uploadProgress::dismiss);
                        }
                    }).start();
                });
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
