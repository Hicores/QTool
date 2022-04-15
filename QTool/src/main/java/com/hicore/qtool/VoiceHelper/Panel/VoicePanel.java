package com.hicore.qtool.VoiceHelper.Panel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.FileUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.EmoHelper.Panel.EmoPanelView;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQTools.ContUtil;
import com.hicore.qtool.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoicePanel {
    public static void createVoicePanel(){
        Context context = Utils.getTopActivity();
        ResUtils.StartInject(context);
        Context fixContext = ContUtil.getFixContext(context);
        VoicePanelController controller = new VoicePanelController(fixContext);

        XPopup.Builder NewPop = new XPopup.Builder(fixContext)
                .autoOpenSoftInput(false)
                .autoFocusEditText(false)
                .isDestroyOnDismiss(true)
                .moveUpToKeyboard(false);

        BasePopupView base = NewPop.asCustom(controller);
        base.show();
    }
    private static String preSavePath;
    public static void preSaveVoice(Context context,String voicePath){
        String tmpName = "语音" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        ResUtils.StartInject(context);
        LinearLayout mRoot = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.voice_pre_save,null);
        EditText ed = mRoot.findViewById(R.id.Ed_SaveName);
        ed.setText(tmpName);
        preSavePath = "/Voice";

        TextView showPath = mRoot.findViewById(R.id.Tip_EdSavaName);

        new AlertDialog.Builder(context,Utils.getDarkModeStatus(context) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("保存语音")
                .setView(mRoot)
                .setNeutralButton("确定保存", (dialog, which) -> {
                    FileUtils.copy(voicePath,HookEnv.ExtraDataPath + preSavePath + "/" + ed.getText().toString());
                    Utils.ShowToast("已保存到:"+HookEnv.ExtraDataPath + preSavePath + "/" + ed.getText().toString());
                }).show();
        LinearLayout dirList = mRoot.findViewById(R.id.FolderSelect);
        UpdateList(dirList,showPath);

    }
    public static void UpdateList(LinearLayout root,TextView show){
        show.setText("保存到:"+preSavePath);
        root.removeAllViews();
        if (!preSavePath.equals("/Voice") && preSavePath.length() > 6){
            TextView itemSelect = new TextView(root.getContext());
            itemSelect.setTextColor(Color.BLACK);
            itemSelect.setTextSize(16);
            itemSelect.setText("...");
            root.addView(itemSelect);
            itemSelect.setOnClickListener(v->{
                preSavePath = preSavePath.substring(0,preSavePath.lastIndexOf("/"));
                UpdateList(root,show);
            });
        }
        File[] fs = new File(HookEnv.ExtraDataPath+preSavePath).listFiles();
        if (fs != null){
            for (File f : fs){
                if (f.isDirectory()){
                    TextView itemSelect = new TextView(root.getContext());
                    itemSelect.setTextColor(Color.BLACK);
                    itemSelect.setTextSize(16);
                    itemSelect.setText(f.getName());
                    root.addView(itemSelect);
                    itemSelect.setOnClickListener(v->{
                        preSavePath += "/" + f.getName();
                        UpdateList(root,show);
                    });
                }
            }

        }
    }
}
