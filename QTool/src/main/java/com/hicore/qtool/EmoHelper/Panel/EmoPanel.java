package com.hicore.qtool.EmoHelper.Panel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.FileUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.EmoHelper.Hooker.HookInjectEmoTabView;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQTools.ContUtil;
import com.hicore.qtool.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class EmoPanel {
    public static void createShow(Context context){
        Context fixContext = ContUtil.getFixContext(context);
        EmoPanelView NewView = new EmoPanelView(fixContext);

        XPopup.Builder NewPop = new XPopup.Builder(fixContext);
        BasePopupView base = NewPop.asCustom(NewView);
        base.show();
    }
    public static class EmoInfo{
        public String Path;
        public String Name;
        public int type;
        public String MD5;
        public String URL;
    }
    static String choiceName = "";
    public static void PreSavePicToList(String URL,String MD5,Context context){
        choiceName = "";
        ResUtils.StartInject(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout mRoot = (LinearLayout) inflater.inflate(R.layout.emo_pre_save,null);
        ImageView preView = mRoot.findViewById(R.id.emo_pre_container);
        preView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        EmoInfo NewInfo = new EmoInfo();
        NewInfo.URL = URL;
        NewInfo.type = 2;
        NewInfo.MD5 = MD5.toUpperCase(Locale.ROOT);
        EmoOnlineLoader.submit(NewInfo,()->{
            Glide.with(HookEnv.AppContext)
                    .load(new File(NewInfo.Path))
                    .fitCenter()
                    .into(preView);
        });

        ArrayList<String> NameList = EmoSearchAndCache.searchForPathList();
        RadioGroup group = mRoot.findViewById(R.id.emo_pre_list_choser);
        for(String ItemName :NameList){
            RadioButton button = new RadioButton(context);
            button.setText(ItemName);
            button.setTextSize(16);
            button.setTextColor(context.getResources().getColor(R.color.font_plugin,null));
            button.setOnCheckedChangeListener((v,ischeck)->{
                if (v.isPressed() && ischeck){
                    choiceName = ItemName;
                }
            });
            group.addView(button);
        }

        new AlertDialog.Builder(context,3)
                .setTitle("是否保存")
                .setView(mRoot)
                .setNeutralButton("保存", (dialog, which) -> {
                    if (TextUtils.isEmpty(choiceName)){
                        Utils.ShowToastL("没有选择任何的保存列表");
                    }else if (TextUtils.isEmpty(NewInfo.Path)){
                        Utils.ShowToastL("图片尚未加载完毕,保存失败");
                    }else {
                        FileUtils.copy(NewInfo.Path,HookEnv.ExtraDataPath+"Pic/"+choiceName+"/"+MD5);
                        Utils.ShowToastL("已保存到:"+HookEnv.ExtraDataPath+"Pic/"+choiceName+"/"+MD5);
                    }
                }).show();

    }
    public static void PreSaveMultiPicList(ArrayList<String> url,ArrayList<String> MD5,Context context){
        new AlertDialog.Builder(context,3)
                .setTitle("选择需要保存的图片")
                .setItems(MD5.toArray(new String[0]), (dialog, which) -> {
                    PreSavePicToList(url.get(which),MD5.get(which),context);
                }).show();
    }

}
