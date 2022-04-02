package com.hicore.qtool.VoiceHelper.Panel;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.Voice;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQMessage.QQMessageUtils;
import com.hicore.qtool.QQMessage.QQMsgSendUtils;
import com.hicore.qtool.QQMessage.QQMsgSender;
import com.hicore.qtool.R;
import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import java.util.ArrayList;

public final class VoicePanelController extends BottomPopupView {
    int ControllerMode = 0;
    private VoiceProvider provider;

    private VerticalRecyclerView recyclerView;
    private EasyAdapter<VoiceProvider.FileInfo> commonAdapter;
    private TextView showPath;

    private ArrayList<VoiceProvider.FileInfo> resultFile = new ArrayList<>();
    @Override
    protected void onCreate() {
        super.onCreate();
        initSelectBar();
        initSearchBox();
        showPath = findViewById(R.id.currentPath);

        recyclerView = findViewById(R.id.recyclerView);
        commonAdapter = new EasyAdapter<VoiceProvider.FileInfo>(resultFile, R.layout.voice_panel_item) {
            @Override
            protected void bind(@NonNull ViewHolder viewHolder, VoiceProvider.FileInfo fileInfo, int i) {
                RelativeLayout mItem = (RelativeLayout) viewHolder.getConvertView();
                ImageView image = mItem.findViewById(R.id.mIcon);
                if (fileInfo.type == 1)image.setImageResource(R.drawable.voice_item);
                else image.setImageResource(R.drawable.folder);
                ImageView clickButton = mItem.findViewById(R.id.sendButton);
                clickButton.setVisibility(fileInfo.type == 1 ? VISIBLE:GONE);
                if (fileInfo.type == 1){
                    clickButton.setOnClickListener(v-> {
                        QQMsgSender.sendVoice(HookEnv.SessionInfo,fileInfo.Path);
                        dismiss();
                    });
                }

                TextView title = mItem.findViewById(R.id.voice_name);
                title.setText(fileInfo.Name);
                //设置目录和语音的点击信息
                if (fileInfo.type == 1){
                    mItem.setOnClickListener(null);
                }else if (fileInfo.type == 2){
                    mItem.setOnClickListener(v->{
                        provider = provider.getChild(fileInfo.Name);
                        UpdateProviderDate();
                    });
                }else if (fileInfo.type == -1){
                    mItem.setOnClickListener(v->{
                        provider = provider.getParent();
                        UpdateProviderDate();
                    });
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
    }
    public void UpdateProviderDate(){
        resultFile.clear();
        resultFile.addAll(provider.getList());
        commonAdapter.notifyDataSetChanged();
        showPath.setText(provider.getPath());
    }
    public void UpdateControlData(){
        if (ControllerMode == 0){
            provider = VoiceProvider.getNewInstance(VoiceProvider.PROVIDER_LOCAL_FILE);
            UpdateProviderDate();
        }
    }
    private void UpdateList(ArrayList<VoiceProvider.FileInfo> infos){

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
