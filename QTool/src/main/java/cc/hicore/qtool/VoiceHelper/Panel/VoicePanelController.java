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
import java.nio.file.Path;
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
                                .setItems(new String[]{"删除", "添加到语音包"}, (dialog, which) -> {
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
                                .setItems(new String[]{"添加到语音包"}, (dialog, which) -> {
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

        imageLocalFile = new ImageView(getContext());
        imageLocalFile.setImageResource(R.drawable.voice_upload);
        param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(), 25), Utils.dip2px(getContext(), 25));
        param.setMargins(Utils.dip2px(getContext(), 12), 10, Utils.dip2sp(getContext(), 5), 10);
        topBar.addView(imageLocalFile, param);

        imageLocalFile.setOnClickListener(v -> {
            ControllerMode = 2;
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
        } else if (ControllerMode == 2) {
            UpdateUploadView();
        }
    }

    private void AddVoiceToPacket(ArrayList<String> Paths) {
        ProgressDialog dialog = new ProgressDialog(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT);
        dialog.setTitle("加载中..");
        dialog.setMessage("正在获取可用列表...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread(() -> {
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundle?key=" + OnlineBundleHelper.requestForRndKey());
            new Handler(Looper.getMainLooper())
                    .post(() -> {
                        try {

                            JSONObject json = new JSONObject(Content);
                            JSONArray mArray = json.getJSONArray("data");
                            ArrayList<String> bundleList = new ArrayList<>();
                            ArrayList<String> idList = new ArrayList<>();

                            for (int i = 0; i < mArray.length(); i++) {
                                JSONObject item = mArray.getJSONObject(i);
                                bundleList.add(item.getString("name"));
                                idList.add(item.getString("id"));
                            }

                            new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                    .setItems(bundleList.toArray(new String[0]), (dialog1, which) -> {
                                        String Bundle = idList.get(which);
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
                                                    OnlineBundleHelper.RequestUpload(new File(Path).getName(), Path, Bundle);
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
                                    }).setTitle("选择添加"+ Paths.size()+"个语音").show();
                        } catch (Exception e) {
                            Utils.ShowToastL("发生错误:\n" + e);
                        } finally {
                            dialog.dismiss();
                        }
                    });

        }).start();
    }

    private void PreLoadBundleList() {
        ProgressDialog mDialog = new ProgressDialog(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT);
        mDialog.setTitle("正在加载..");
        mDialog.setMessage("正在加载列表...");
        mDialog.setCancelable(false);
        mDialog.show();
        new Thread(() -> {
            try {
                UpdateList();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                new Handler(Looper.getMainLooper()).post(mDialog::dismiss);
            }
        }).start();
    }

    LinearLayout EDBar;

    private void UpdateUploadView() {
        recyclerView.setVisibility(GONE);
        mFrame.setVisibility(VISIBLE);
        mFrame.removeAllViews();
        View mRoot = LayoutInflater.from(getContext()).inflate(R.layout.voice_edit_bundle, null);
        mFrame.addView(mRoot);

        EDBar = mRoot.findViewById(R.id.EdBar);

        mRoot.findViewById(R.id.FlushList).setOnClickListener(v -> {
            PreLoadBundleList();
        });
        mRoot.findViewById(R.id.CreateNewBundle).setOnClickListener(v -> {
            EditText inputName = new EditText(getContext());
            inputName.setHint("输入名字");

            new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("请输入名字")
                    .setView(inputName)
                    .setNeutralButton("确定创建", (dialog, which) -> {
                        String name = inputName.getText().toString();
                        if (name.length() < 4) {
                            Utils.ShowToastL("名字不能少于4个字");
                            return;
                        }
                        if (name.length() > 20) {
                            Utils.ShowToastL("名字不能多于20个字");
                            return;
                        }
                        OnlineBundleHelper.createBundle(name);
                        new Thread(this::UpdateList).start();
                    }).show();
        });

        PreLoadBundleList();
    }

    private void UpdateList() {
        String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundle?key=" + OnlineBundleHelper.requestForRndKey());
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                EDBar.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(EDBar.getContext());
                JSONObject NewObj = new JSONObject(Content);
                JSONArray mArray = NewObj.getJSONArray("data");
                for (int i = 0; i < mArray.length(); i++) {
                    JSONObject item = mArray.getJSONObject(i);
                    RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.voice_panel_ed_bundle, null);
                    ImageView image = mLayout.findViewById(R.id.mIcon);
                    image.setImageResource(R.drawable.folder);

                    TextView name = mLayout.findViewById(R.id.voice_name);
                    if (item.getBoolean("IsRequestShow")) {
                        if (item.getBoolean("IsVerify")) {
                            name.setText("(通过)" + item.getString("name"));
                        } else {
                            name.setText("(审核中)" + item.getString("name"));
                        }
                    } else {
                        name.setText(item.getString("name"));
                    }


                    name.setOnClickListener(v -> {
                        ProgressDialog mDialog = new ProgressDialog(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT);
                        mDialog.setTitle("正在加载..");
                        mDialog.setMessage("正在加载列表...");
                        mDialog.setCancelable(false);
                        mDialog.show();
                        new Thread(() -> {
                            try {
                                UpdateChcek(item.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                new Handler(Looper.getMainLooper()).post(() -> mDialog.dismiss());
                            }
                        }).start();
                    });

                    ImageView shareButton = mLayout.findViewById(R.id.ShareButton);
                    shareButton.setOnClickListener(v -> {
                        new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("确认分享?")
                                .setMessage("分享后将进行审核,审核通过后其他人即可在在线语音列表显示,通过后也可以继续上传语音到该包中")
                                .setNeutralButton("确定分享", (dialog, which) -> {
                                    try {
                                        String ret = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/RequestShare?id=" + item.getString("id")
                                                + "&key=" + OnlineBundleHelper.requestForRndKey());
                                        JSONObject mJson = new JSONObject(ret);
                                        Utils.ShowToastL("分享结果:" + mJson.optString("msg"));
                                        new Thread(this::UpdateList).start();
                                    } catch (Exception e) {
                                        Utils.ShowToastL("发生错误:" + e);
                                    }

                                }).setNegativeButton("关闭", (dialog, which) -> {

                        }).show();
                    });

                    ImageView delete = mLayout.findViewById(R.id.deleteButton);
                    delete.setOnClickListener(v -> {
                        new AlertDialog.Builder(getContext(), 3)
                                .setTitle("确认操作")
                                .setMessage("是否删除?")
                                .setNeutralButton("确定删除", (dialog, which) -> {
                                    try {
                                        String ret = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/removeBundle?BundleID=" + item.getString("id")
                                                + "&key=" + OnlineBundleHelper.requestForRndKey());
                                        JSONObject mJson = new JSONObject(ret);
                                        Utils.ShowToast(mJson.optString("msg"));

                                        PreLoadBundleList();
                                    } catch (Exception e) {
                                        Utils.ShowToastL("发生错误:" + e);
                                    }
                                }).setNegativeButton("关闭", (dialog, which) -> {

                        }).show();
                    });

                    EDBar.addView(mLayout);
                }
            } catch (Exception e) {
                Utils.ShowToastL("发生错误:\n" + e);
            }
        });
    }

    private void UpdateChcek(String BundleID) {
        String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundleInfo?id=" + BundleID);
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                EDBar.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(EDBar.getContext());
                JSONObject NewObj = new JSONObject(Content);
                JSONArray mArray = NewObj.getJSONArray("data");
                for (int i = 0; i < mArray.length(); i++) {
                    JSONObject item = mArray.getJSONObject(i);
                    RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.voice_panel_ed_bundle, null);
                    ImageView image = mLayout.findViewById(R.id.mIcon);
                    image.setImageResource(R.drawable.voice_item);

                    TextView name = mLayout.findViewById(R.id.voice_name);
                    name.setText(item.getString("Name"));
                    ImageView shareButton = mLayout.findViewById(R.id.ShareButton);
                    shareButton.setVisibility(GONE);

                    ImageView delete = mLayout.findViewById(R.id.deleteButton);
                    delete.setOnClickListener(v -> {
                        new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("确认操作")
                                .setMessage("是否删除?")
                                .setNeutralButton("确定删除", (dialog, which) -> {
                                    try {
                                        String ret = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/removeVoice?VoiceID=" + item.getString("Id")
                                                + "&key=" + OnlineBundleHelper.requestForRndKey());
                                        JSONObject mJson = new JSONObject(ret);
                                        Utils.ShowToast(mJson.optString("msg"));

                                        ProgressDialog mDialog = new ProgressDialog(getContext(), 3);
                                        mDialog.setTitle("正在加载..");
                                        mDialog.setMessage("正在加载列表...");
                                        mDialog.setCancelable(false);
                                        mDialog.show();
                                        new Thread(() -> {
                                            try {
                                                UpdateChcek(BundleID);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                new Handler(Looper.getMainLooper()).post(() -> mDialog.dismiss());
                                            }
                                        }).start();
                                    } catch (Exception e) {
                                        Utils.ShowToastL("发生错误:" + e);
                                    }
                                }).setNegativeButton("关闭", (dialog, which) -> {

                        }).show();
                    });

                    EDBar.addView(mLayout);
                }
            } catch (Exception e) {
                Utils.ShowToastL("发生错误:\n" + e);
            }
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
