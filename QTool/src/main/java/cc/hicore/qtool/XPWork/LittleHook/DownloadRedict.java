package cc.hicore.qtool.XPWork.LittleHook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "下载重定向",groupName = "功能辅助",targetID = 1,type = 2,id = "DownloadRedict")
public class DownloadRedict extends BaseHookItem implements BaseUiItem {
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            String Path = (String) param.args[0];
            String Result = (String) param.getResult();
            if (isEnable()){
                if(Result.contains("/Tencent/QQfile_recv/")) {
                    if(new File(Result).exists() && new File(Result).isFile())return;//如果下载的文件已经存在则不替换,防止与QQ文件数据库出错而导致无法下载的问题
                    String End = Path.substring(Path.lastIndexOf("/Tencent/QQfile_recv/")+"/Tencent/QQfile_recv/".length());
                    String Start = HookEnv.Config.getString("Set","DownloadRedictPath","");

                    if(TextUtils.isEmpty(Start))Start = Environment.getExternalStorageDirectory()+"/Download/MobileQQ/";
                    if(!Start.endsWith("/"))Start = Start + "/";
                    param.setResult(Start+End);
                    return;
                }
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return HookEnv.Config.getBoolean("Set","DownloadRedictOpen",false);
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        LinearLayout mRoot = new LinearLayout(context);
        mRoot.setOrientation(LinearLayout.VERTICAL);

        CheckBox box = new CheckBox(context);
        box.setText("开启重定向");
        box.setChecked(HookEnv.Config.getBoolean("Set","DownloadRedictOpen",false));
        mRoot.addView(box);

        EditText ed = new EditText(context);
        ed.setText(HookEnv.Config.getString("Set","DownloadRedictPath",HookEnv.AppContext.getExternalCacheDir().getParent()+"/Tencent/QQfile_recv/"));
        mRoot.addView(ed);

        new AlertDialog.Builder(context)
                .setTitle("下载重定向设置")
                .setView(mRoot)
                .setNegativeButton("保存", (dialog, which) -> {
                    if (box.isChecked()){
                        String path = ed.getText().toString();
                        if (!checkMkDir(path)){
                            new AlertDialog.Builder(context)
                                    .setTitle("警告")
                                    .setMessage("设置的路径没有访问权限,请输入有访问权限的路径")
                                    .setNegativeButton("确定", (dialog1, which1) -> {

                                    }).show();
                            return;
                        }
                        HookEnv.Config.setBoolean("Set","DownloadRedictOpen",true);
                        if (!path.endsWith("/"))path = path + "/";
                        HookEnv.Config.setString("Set","DownloadRedictPath",path);
                        Utils.ShowToast("重启QQ生效");
                    }else {
                        HookEnv.Config.setBoolean("Set","DownloadRedictOpen",false);
                    }

                }).show();
    }
    private static boolean checkMkDir(String Path){
        File f = new File(Path);
        f.mkdirs();
        return f.exists() && f.isDirectory();
    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.vfs.VFSAssistantUtils"),"getSDKPrivatePath",String.class,new Class[]{
                String.class
        });
    }
}
