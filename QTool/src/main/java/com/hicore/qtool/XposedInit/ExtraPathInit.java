package com.hicore.qtool.XposedInit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.hicore.ConfigUtils.GlobalConfig;
import com.hicore.Utils.Utils;

import java.io.File;
import java.io.IOException;

public class ExtraPathInit {
    public static void InitPath(){
        String Path = GlobalConfig.Get_String("StorePath");
        File pathFile = new File(Path);
        if (!pathFile.exists())pathFile.mkdirs();
        if (pathFile.exists()){
            HookEnv.ExtraDataPath = pathFile.getAbsolutePath() + File.separatorChar;
        }
    }
    @SuppressLint("ResourceType")
    public static void ShowPathSetDialog(){
        Activity act = Utils.getTopActivity();
        Dialog fullScreenDialog = new Dialog(act, 3);
        LinearLayout mRoot = new LinearLayout(act);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setBackgroundColor(Color.WHITE);

        TextView topTitle = new TextView(act);
        topTitle.setText("QTool");
        topTitle.setTextSize(48);
        topTitle.setTextColor(Color.BLACK);
        topTitle.setGravity(Gravity.CENTER);
        mRoot.addView(topTitle);

        TextView desc = new TextView(act);
        desc.setText("你需要确定一个存储路径用来存储模块配置/缓存/资源等数据,请在下面确认你需要使用的存储路径");
        desc.setTextColor(Color.BLUE);
        desc.setTextSize(16);
        mRoot.addView(desc);

        TextView aboveInput = new TextView(act);
        aboveInput.setText("在下面输入框输入需要使用的路径:");
        aboveInput.setTextColor(Color.BLUE);
        mRoot.addView(aboveInput);

        EditText inputPath = new EditText(act);
        inputPath.setText(GlobalConfig.Get_String("StorePath"));
        inputPath.setHint("\n\n");//占位让输入框至少显示3行
        inputPath.setGravity(Gravity.TOP);


        LinearLayout toolBar = new LinearLayout(act);
        toolBar.setPadding(0,0,20,0);
        TextView selectExtra = new TextView(act);
        selectExtra.setTextColor(Color.BLUE);
        selectExtra.setText("外置存储根目录");
        selectExtra.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        selectExtra.setOnClickListener(v->inputPath.setText(Environment.getExternalStorageDirectory()+"/QTool"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(20);
        toolBar.addView(selectExtra,lp);


        TextView selectMedia = new TextView(act);
        selectMedia.setTextColor(Color.BLUE);
        selectMedia.setText("半私有Media目录");
        selectMedia.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        selectMedia.setOnClickListener(v->inputPath.setText(HookEnv.AppContext.getExternalMediaDirs()[0]+"/.tool"));
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(20);
        toolBar.addView(selectMedia,lp);

        TextView selectData = new TextView(act);
        selectData.setTextColor(Color.BLUE);
        selectData.setText("私有data目录");
        selectData.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        selectData.setOnClickListener(v->inputPath.setText(HookEnv.AppContext.getExternalFilesDir(null)+"/.tool"));
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(20);
        toolBar.addView(selectData,lp);
        mRoot.addView(toolBar);
        mRoot.addView(inputPath,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button saveBtn = new Button(act);
        saveBtn.setText("保存设置");
        saveBtn.setOnClickListener(v-> CheckAndSave(act,fullScreenDialog,inputPath.getText().toString()));
        mRoot.addView(saveBtn,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        fullScreenDialog.setContentView(mRoot);
        fullScreenDialog.setCancelable(false);
        fullScreenDialog.show();
    }
    public static void CheckAndSave(Activity mAct,Dialog dismissDialog,String Path){
        if (Path.endsWith("/"))Path = Path.substring(0,Path.length()-1);
        new File(Path).mkdirs();
        if (!CheckPermission(Path)){
            if (ContextCompat.checkSelfPermission(mAct,"android.permission.WRITE_EXTERNAL_STORAGE")== PackageManager.PERMISSION_DENIED){
                mAct.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},0);
            }
            Utils.ShowToast("目录没有权限,请确定是否给予存储权限或者目录是否有效");
            return;
        }
        GlobalConfig.Put_String("StorePath",Path);
        dismissDialog.dismiss();
    }
    public static boolean CheckPermission(String Path){
        File check = new File(Path,".PermissionCheck");
        if (check.exists())check.delete();
        try {
            check.createNewFile();
            if (check.exists()){
                check.delete();
                if (!check.exists()){
                    return true;
                }
            }
        } catch (IOException e) { }
        return false;
    }
}
