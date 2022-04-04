package com.hicore.qtool.VoiceHelper.Panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.hicore.Utils.FileUtils;
import com.hicore.Utils.HttpUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQMessage.QQMsgSender;
import com.hicore.qtool.R;
import com.hicore.qtool.VoiceHelper.OnlineHelper.OnlineBundleHelper;
import com.hicore.qtool.XposedInit.EnvHook;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;
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
                if (fileInfo.type == 1 || fileInfo.type == 6)image.setImageResource(R.drawable.voice_item);
                else image.setImageResource(R.drawable.folder);

                ImageView clickButton = mItem.findViewById(R.id.sendButton);
                clickButton.setVisibility((fileInfo.type == 1 || fileInfo.type == 6) ? VISIBLE:GONE);
                if (fileInfo.type == 1){
                    clickButton.setOnClickListener(v-> {
                        QQMsgSender.sendVoice(HookEnv.SessionInfo,fileInfo.Path);
                        dismiss();
                    });
                }else if (fileInfo.type == 6){
                    clickButton.setOnClickListener(v->{
                        EnvHook.requireCachePath();
                        String cachePath = HookEnv.ExtraDataPath + "Cache/"+fileInfo.Name.hashCode();
                        HttpUtils.ProgressDownload(fileInfo.Path,cachePath,()->{
                            QQMsgSender.sendVoice(HookEnv.SessionInfo,cachePath);
                            new Handler(Looper.getMainLooper()).post(()->dismiss());

                        },getContext());
                    });

                }else {
                    clickButton.setOnLongClickListener(null);
                }

                TextView title = mItem.findViewById(R.id.voice_name);
                title.setText(fileInfo.Name);

                //设置目录和语音的点击信息
                if (fileInfo.type == 1){
                    mItem.setOnClickListener(null);
                    mItem.setOnLongClickListener(v->{
                        new AlertDialog.Builder(getContext(),3)
                                .setTitle("选择操作")
                                .setItems(new String[]{"删除", "添加到语音包"}, (dialog, which) -> {
                                    if (which == 0){
                                        FileUtils.deleteFile(new File(fileInfo.Path));
                                        UpdateProviderDate();
                                    }else if (which == 1){
                                        AddVoiceToPacket(fileInfo.Name, fileInfo.Path);
                                    }
                                }).show();
                        return true;
                    });

                }else if (fileInfo.type == 2){
                    mItem.setOnClickListener(v->{
                        provider = provider.getChild(fileInfo.Name);
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(null);
                }else if (fileInfo.type == -1){
                    mItem.setOnClickListener(v->{
                        provider = provider.getParent();
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(null);
                }else if (fileInfo.type == 5){
                    mItem.setOnClickListener(v->{
                        provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_ONLINE + fileInfo.Path);
                        UpdateProviderDate();
                    });
                    mItem.setOnLongClickListener(null);
                }else if (fileInfo.type == 6){
                    mItem.setOnClickListener(null);
                    mItem.setOnLongClickListener(null);
                }
            }
        };
        recyclerView.setAdapter(commonAdapter);

        UpdateControlData();
    }
    private VoiceProvider cacheProvider;
    private void initSearchBox(){
        new Handler(Looper.getMainLooper())
                .postDelayed(()->{
                    EditText searchBox = new EditText(getContext());
                    searchBox.setHint("输入名字即可搜索");
                    searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                    searchBox.setSingleLine();
                    searchBox.setFocusable(true);
                    searchBox.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH){
                            if (ControllerMode == 0){
                                String search = v.getText().toString();
                                if (search.length() == 0){
                                    if (cacheProvider != null){
                                        provider = cacheProvider;
                                        cacheProvider = null;
                                        UpdateProviderDate();
                                    }
                                }else{
                                    if (cacheProvider == null){
                                        cacheProvider = provider;
                                    }
                                    provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_LOCAL_SEARCH+search);
                                    UpdateProviderDate();
                                }
                            }else if (ControllerMode == 1){
                                String search = v.getText().toString();
                                if (search.trim().isEmpty()){
                                    Utils.ShowToastL("必须输入搜索内容");
                                    return true;
                                }
                                provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_ONLINE_SEARCH+search);
                                UpdateProviderDate();
                            }else {
                                Utils.ShowToastL("这里不支持搜索");
                            }

                        }
                        return false;
                    });

                    ((LinearLayout)findViewById(R.id.currentPath).getParent()).addView(searchBox,1);
                },200);

    }
    private void initSelectBar(){
        LinearLayout topBar = findViewById(R.id.selectItem);
        ImageView imageLocalFile = new ImageView(getContext());
        imageLocalFile.setImageResource(R.drawable.voice_file);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),25),Utils.dip2px(getContext(),25));
        param.setMargins(Utils.dip2px(getContext(),12),10,Utils.dip2sp(getContext(),5),10);
        topBar.addView(imageLocalFile,param);

        imageLocalFile.setOnClickListener(v->{
            ControllerMode = 0;
            UpdateControlData();
        });

        imageLocalFile = new ImageView(getContext());
        imageLocalFile.setImageResource(R.drawable.voice_down);
        param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),25),Utils.dip2px(getContext(),25));
        param.setMargins(Utils.dip2px(getContext(),12),10,Utils.dip2sp(getContext(),5),10);
        topBar.addView(imageLocalFile,param);

        imageLocalFile.setOnClickListener(v->{
            ControllerMode = 1;
            UpdateControlData();
        });

        imageLocalFile = new ImageView(getContext());
        imageLocalFile.setImageResource(R.drawable.voice_upload);
        param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),25),Utils.dip2px(getContext(),25));
        param.setMargins(Utils.dip2px(getContext(),12),10,Utils.dip2sp(getContext(),5),10);
        topBar.addView(imageLocalFile,param);

        imageLocalFile.setOnClickListener(v->{
            ControllerMode = 2;
            UpdateControlData();
        });
    }
    public void UpdateProviderDate(){
        recyclerView.setVisibility(VISIBLE);
        mFrame.setVisibility(GONE);

        resultFile.clear();
        ProgressDialog dialog = new ProgressDialog(getContext(),3);
        dialog.setTitle("请稍后...");
        dialog.setMessage("加载中...");
        dialog.setCancelable(false);
        if (ControllerMode == 1){
            dialog.show();
        }
        new Thread(()->{
            try{
                resultFile.addAll(provider.getList());
            }finally {
                new Handler(Looper.getMainLooper()).post(()->{
                    dialog.dismiss();
                    commonAdapter.notifyDataSetChanged();
                });
            }
        }).start();

        showPath.setText(provider.getPath());
    }
    public void UpdateControlData(){
        if (ControllerMode == 0){
            provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_LOCAL_FILE);
            UpdateProviderDate();
        }else if (ControllerMode == 1){
            provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_ONLINE);
            UpdateProviderDate();
        }else if (ControllerMode == 2){
            UpdateUploadView();
        }
    }
    private void AddVoiceToPacket(String Name,String Path){
        ProgressDialog dialog = new ProgressDialog(getContext(),3);
        dialog.setTitle("加载中..");
        dialog.setMessage("正在获取可用列表...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread(()->{
            String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundle?key="+ OnlineBundleHelper.requestForRndKey());
            new Handler(Looper.getMainLooper())
                    .post(()->{
                        try{

                            JSONObject json = new JSONObject(Content);
                            JSONArray mArray = json.getJSONArray("data");
                            ArrayList<String> bundleList = new ArrayList<>();
                            ArrayList<String> idList = new ArrayList<>();

                            for (int i=0;i<mArray.length();i++){
                                JSONObject item = mArray.getJSONObject(i);
                                bundleList.add(item.getString("name"));
                                idList.add(item.getString("id"));
                            }

                            new AlertDialog.Builder(getContext(),3)
                                    .setItems(bundleList.toArray(new String[0]), (dialog1, which) -> {
                                        String Bundle = idList.get(which);
                                        ProgressDialog uploadProgress = new ProgressDialog(getContext(),3);
                                        uploadProgress.setTitle("正在处理...");
                                        uploadProgress.setMessage("正在上传....");
                                        uploadProgress.setCancelable(false);
                                        uploadProgress.show();
                                        new Thread(()->{
                                            try{
                                                OnlineBundleHelper.RequestUpload(Name,Path,Bundle);
                                            }catch (Exception e){
                                                Utils.ShowToastL("发生错误:\n"+e);

                                            }finally {
                                                new Handler(Looper.getMainLooper()).post(()->uploadProgress.dismiss());
                                            }
                                        }).start();
                                    }).setTitle("选择需要添加到的包").show();
                        }catch (Exception e) {
                            Utils.ShowToastL("发生错误:\n"+e);
                        }finally {
                            dialog.dismiss();
                        }
                    });

        }).start();
    }
    private void PreLoadBundleList(){
        ProgressDialog mDialog = new ProgressDialog(getContext(),3);
        mDialog.setTitle("正在加载..");
        mDialog.setMessage("正在加载列表...");
        mDialog.setCancelable(false);
        mDialog.show();
        new Thread(()->{
            try {
                UpdateList();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                new Handler(Looper.getMainLooper()).post(()->mDialog.dismiss());
            }
        }).start();
    }
    LinearLayout EDBar;
    private void UpdateUploadView(){
        recyclerView.setVisibility(GONE);
        mFrame.setVisibility(VISIBLE);
        mFrame.removeAllViews();
        View mRoot = LayoutInflater.from(getContext()).inflate(R.layout.voice_edit_bundle,null);
        mFrame.addView(mRoot);

        EDBar = mRoot.findViewById(R.id.EdBar);

        mRoot.findViewById(R.id.FlushList).setOnClickListener(v->{
            PreLoadBundleList();
        });
        mRoot.findViewById(R.id.CreateNewBundle).setOnClickListener(v->{
            EditText inputName = new EditText(getContext());
            inputName.setHint("输入名字");

            new AlertDialog.Builder(getContext(),3)
                    .setTitle("请输入名字")
                    .setView(inputName)
                    .setNeutralButton("确定创建", (dialog, which) -> {
                        String name = inputName.getText().toString();
                        if (name.length() < 4){
                            Utils.ShowToastL("名字不能少于4个字");
                            return;
                        }
                        if (name.length() > 20){
                            Utils.ShowToastL("名字不能多于20个字");
                            return;
                        }
                        OnlineBundleHelper.createBundle(name);
                        new Thread(()->UpdateList()).start();
                    }).show();
        });

        PreLoadBundleList();
    }
    private void UpdateList(){
        String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundle?key="+ OnlineBundleHelper.requestForRndKey());
        new Handler(Looper.getMainLooper()).post(()->{
            try{
                EDBar.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(EDBar.getContext());
                JSONObject NewObj = new JSONObject(Content);
                JSONArray mArray = NewObj.getJSONArray("data");
                for (int i=0;i<mArray.length();i++){
                    JSONObject item = mArray.getJSONObject(i);
                    RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.voice_panel_ed_bundle,null);
                    ImageView image = mLayout.findViewById(R.id.mIcon);
                    image.setImageResource(R.drawable.folder);

                    TextView name = mLayout.findViewById(R.id.voice_name);
                    name.setText(item.getString("name"));

                    name.setOnClickListener(v->{
                        ProgressDialog mDialog = new ProgressDialog(getContext(),3);
                        mDialog.setTitle("正在加载..");
                        mDialog.setMessage("正在加载列表...");
                        mDialog.setCancelable(false);
                        mDialog.show();
                        new Thread(()->{
                            try {
                                UpdateChcek(item.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }finally {
                                new Handler(Looper.getMainLooper()).post(()->mDialog.dismiss());
                            }
                        }).start();
                    });

                    ImageView delete = mLayout.findViewById(R.id.deleteButton);
                    delete.setOnClickListener(v->{
                        new AlertDialog.Builder(getContext(),3)
                                .setTitle("确认操作")
                                .setMessage("是否删除?")
                                .setNeutralButton("确定删除", (dialog, which) -> {
                                    try {
                                        String ret = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/removeBundle?BundleID="+item.getString("id")
                                                +"&key="+OnlineBundleHelper.requestForRndKey());
                                        JSONObject mJson = new JSONObject(ret);
                                        Utils.ShowToast(mJson.optString("msg"));

                                        PreLoadBundleList();
                                    } catch (Exception e) {
                                        Utils.ShowToastL("发生错误:"+e);
                                    }
                                }).setNegativeButton("关闭", (dialog, which) -> {

                        }).show();
                    });

                    EDBar.addView(mLayout);
                }
            }catch (Exception e){
                Utils.ShowToastL("发生错误:\n"+e);
            }
        });
    }
    private void UpdateChcek(String BundleID){
        String Content = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/GetBundleInfo?id="+ BundleID);
        new Handler(Looper.getMainLooper()).post(()->{
            try{
                EDBar.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(EDBar.getContext());
                JSONObject NewObj = new JSONObject(Content);
                JSONArray mArray = NewObj.getJSONArray("data");
                for (int i=0;i<mArray.length();i++){
                    JSONObject item = mArray.getJSONObject(i);
                    RelativeLayout mLayout = (RelativeLayout) inflater.inflate(R.layout.voice_panel_ed_bundle,null);
                    ImageView image = mLayout.findViewById(R.id.mIcon);
                    image.setImageResource(R.drawable.voice_item);

                    TextView name = mLayout.findViewById(R.id.voice_name);
                    name.setText(item.getString("Name"));

                    ImageView delete = mLayout.findViewById(R.id.deleteButton);
                    delete.setOnClickListener(v->{
                        new AlertDialog.Builder(getContext(),3)
                                .setTitle("确认操作")
                                .setMessage("是否删除?")
                                .setNeutralButton("确定删除", (dialog, which) -> {
                                    try {
                                        String ret = HttpUtils.getContent("https://qtool.haonb.cc/VoiceBundle/removeVoice?VoiceID="+item.getString("Id")
                                                +"&key="+OnlineBundleHelper.requestForRndKey());
                                        JSONObject mJson = new JSONObject(ret);
                                        Utils.ShowToast(mJson.optString("msg"));

                                        ProgressDialog mDialog = new ProgressDialog(getContext(),3);
                                        mDialog.setTitle("正在加载..");
                                        mDialog.setMessage("正在加载列表...");
                                        mDialog.setCancelable(false);
                                        mDialog.show();
                                        new Thread(()->{
                                            try {
                                                UpdateChcek(BundleID);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }finally {
                                                new Handler(Looper.getMainLooper()).post(()->mDialog.dismiss());
                                            }
                                        }).start();
                                    } catch (Exception e) {
                                        Utils.ShowToastL("发生错误:"+e);
                                    }
                                }).setNegativeButton("关闭", (dialog, which) -> {

                                }).show();
                    });

                    EDBar.addView(mLayout);
                }
            }catch (Exception e){
                Utils.ShowToastL("发生错误:\n"+e);
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
