package com.hicore.qtool.XPWork.BaseMenu.MainMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hicore.LogUtils.LogUtils;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

public class MainMenu extends Activity {
    private static final String TAG = "MainActivityProxy";
    @Override
    public ClassLoader getClassLoader() {
        return HookEnv.fixLoader;
    }

    public static void createActivity(Activity parentAct){
        try{
            Intent intent = new Intent(parentAct,MainMenu.class);
            parentAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.error(TAG, Log.getStackTraceString(e));
        }


    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try{
            ResUtils.StartInject(this);
            setTheme(R.style.Theme_QTool);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            new XPopup.Builder(this).asConfirm("测试标题", "测试",
                    new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            Utils.ShowToast("click confirm");
                        }
                    })
                    .show();
        }catch (Exception e){
            LogUtils.error(TAG, Log.getStackTraceString(e));
        }

    }
}
