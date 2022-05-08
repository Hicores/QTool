package cc.hicore.qtool.XposedInit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@SuppressLint("ResourceType")
public class DebugDialog {
    public static void startShow(Context context){
        Dialog fullScreen = new Dialog(context, 3);
        ScrollView view = new ScrollView(context);
        LinearLayout mRoot = new LinearLayout(context);
        view.addView(mRoot);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setBackgroundColor(Color.WHITE);

        TextView titleBar = new TextView(context);
        titleBar.setTextColor(Color.parseColor("#6666ff"));
        titleBar.setText("状态信息");
        titleBar.setTextSize(24);
        mRoot.addView(titleBar, getMarginParam());

        TextView statusResInject = new TextView(context);
        statusResInject.setText("资源注入状态:" + (ResUtils.CheckResInject(context) ? "注入成功" : "注入失败"));
        statusResInject.setTextSize(16);
        mRoot.addView(statusResInject, getMarginParam());

        TextView statusStoragePath = new TextView(context);
        statusStoragePath.setText("当前设置的存储路径:" + GlobalConfig.Get_String("StorePath"));
        statusStoragePath.setTextSize(16);
        mRoot.addView(statusStoragePath, getMarginParam());

        LinearLayout ll = new LinearLayout(context);
        mRoot.addView(ll);
        Button btnChangeDir = new Button(context);
        btnChangeDir.setText("更改存储目录");
        btnChangeDir.setOnClickListener(vx -> ExtraPathInit.ShowPathSetDialog(true));
        ll.addView(btnChangeDir);

        Button startLogcatDumper = new Button(context);
        startLogcatDumper.setText("开启Logcat存储");
        startLogcatDumper.setOnClickListener(vx->{
            new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setMessage("在下两次QQ启动的时候,Logcat将会被导出到 "+ HookEnv.ExtraDataPath+"Log目录")
                    .setNegativeButton("确定导出", (dialog, which) -> GlobalConfig.Put_Boolean("Confirm_Output_Logcat",true))
                    .show();
        });
        //ll.addView(startLogcatDumper);

        CheckBox check = new CheckBox(context);
        check.setChecked(GlobalConfig.Get_Boolean("Add_Menu_Button_to_Main",false));
        check.setTextColor(Color.BLACK);
        check.setText("在主界面加号添加入口");
        check.setOnCheckedChangeListener((v,isCheck)->GlobalConfig.Put_Boolean("Add_Menu_Button_to_Main",isCheck));
        mRoot.addView(check);

        check = new CheckBox(context);
        check.setChecked(GlobalConfig.Get_Boolean("Prevent_Crash_In_Java",false));
        check.setTextColor(Color.BLACK);
        check.setText("阻止Java层的闪退(不一定总是有效)");
        check.setOnCheckedChangeListener((v,isCheck)->GlobalConfig.Put_Boolean("Prevent_Crash_In_Java",isCheck));
        mRoot.addView(check);

        LinearLayout crashBar = new LinearLayout(context);
        Button btnCrashMain = new Button(context);
        btnCrashMain.setText("主线程闪退测试");
        btnCrashMain.setOnClickListener(v->{throw new RuntimeException("Java闪退测试");});
        crashBar.addView(btnCrashMain);

        Button btnCrashThread = new Button(context);
        btnCrashThread.setText("子线程闪退测试");
        btnCrashThread.setOnClickListener(v->{
            Thread thread = new Thread(()->{
                throw new RuntimeException("Java子线程闪退测试");
            });
            thread.setName("QTool_闪退测试");
            thread.start();
        });
        crashBar.addView(btnCrashThread);
        mRoot.addView(crashBar);

        TextView titleClzInfo = new TextView(context);
        titleClzInfo.setTextColor(Color.parseColor("#6666ff"));
        titleClzInfo.setText("模块加载状态");
        titleClzInfo.setTextSize(24);
        mRoot.addView(titleClzInfo, getMarginParam());

        TextView hookInfo = new TextView(context);
        hookInfo.setText(getClassReport());
        hookInfo.setTextSize(12);
        hookInfo.setTextColor(Color.BLACK);
        mRoot.addView(hookInfo, getMarginParam());

        fullScreen.setContentView(view);
        fullScreen.show();

    }
    public static ViewGroup.LayoutParams getMarginParam() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 0, 0, 0);
        params.setMarginEnd(30);
        return params;
    }

    public static String getClassReport() {
        StringBuilder builder = new StringBuilder();
        for (HookLoader.CheckResult result : HookLoader.CheckForItemsStatus()) {
            builder.append(result.Name).append("\n").append("开启:").append(result.IsEnable).append("  ,可用:").append(result.IsAvailable).append(" ,已加载:").append(result.IsLoaded).append("\n");
            if (!TextUtils.isEmpty(result.ErrorInfo)) {
                builder.append("报告异常信息:\n").append(result.ErrorInfo).append("\n-------------------------\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
