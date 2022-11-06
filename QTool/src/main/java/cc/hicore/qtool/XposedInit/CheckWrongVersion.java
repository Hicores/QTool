package cc.hicore.qtool.XposedInit;

import android.app.AlertDialog;
import android.content.Context;

public class CheckWrongVersion {
    public static void ShowToast1(Context context) {
        new AlertDialog.Builder(context, 3)
                .setTitle("提示")
                .setMessage("由于作者精力有限,QTool将在QQ8.8.35以下版本中禁用,请升级QQ后使用,如果私自修改了QQ版本号请修改回正确版本,如果有需要请使用旧版本模块")
                .setNegativeButton("确定", (dialog, which) -> {

                }).show();
    }

    public static void ShowWrongVersionDialog(Context context) {
        new AlertDialog.Builder(context, 3)
                .setTitle("提示")
                .setMessage("当前QQ版本号存在异常,请不要私自修改QQ版本号,以避免适配错误")
                .setNegativeButton("确定", (dialog, which) -> {

                }).show();
    }
}
