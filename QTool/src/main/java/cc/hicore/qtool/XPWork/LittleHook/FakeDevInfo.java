package cc.hicore.qtool.XPWork.LittleHook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "修改设备型号",groupName = "修改",id = "FakeDevInfo",targetID = 3,type = 2)
public class FakeDevInfo extends BaseHookItem implements BaseUiItem {
    @Override
    public boolean startHook() throws Throwable {
        MField.SetField(null, Build.class,"MODEL",String.class,HookEnv.Config.getString("Set","FakeDevInfoSet",""));
        Method m = MMethod.FindMethod(MClass.loadClass("NS_MOBILE_EXTRA.GetDeviceInfoRsp"),"readFrom",void.class,new Class[]{MClass.loadClass("com.qq.taf.jce.JceInputStream")});
        XPBridge.HookAfter(m,param -> {
            if (HookEnv.Config.getBoolean("Set","FakeDevInfoOpen",false)){
                List devinfo_List = MField.GetField(param.thisObject,"vecDeviceInfo");
                for (Object devInfo : devinfo_List){
                    String name = MField.GetField(devInfo,"strDeviceTail");
                    if (name.toLowerCase(Locale.ROOT).startsWith("android"))break;
                    String sub = name.indexOf("(") != -1 ? name.substring(name.indexOf("(")) : "";
                    name = HookEnv.Config.getString("Set","FakeDevInfoSet","")+sub;
                    MField.SetField(devInfo,"strDeviceTail",name);
                }
            }
        });
        try{
            m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.Pandora.deviceInfo.DeviceInfoManager"), "getModel",String.class,new Class[]{
                    Context.class
            });
            XPBridge.HookAfter(m,param -> {
                if (HookEnv.Config.getBoolean("Set","FakeDevInfoOpen",false)){
                    param.setResult(HookEnv.Config.getString("Set","FakeDevInfoSet",""));
                }
            });
        }catch (Throwable th){
            if (HostInfo.getVerCode() < 7830){
                m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pandora.deviceinfo.DeviceInfoManager"), "h",String.class,new Class[]{
                        Context.class
                });
            }else {
                m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pandora.deviceinfo.DeviceInfoManager"), "g",String.class,new Class[]{
                        Context.class
                });
            }

            XPBridge.HookAfter(m,param -> {
                if (HookEnv.Config.getBoolean("Set","FakeDevInfoOpen",false)){
                    param.setResult(HookEnv.Config.getString("Set","FakeDevInfoSet",""));
                }
            });
        }

        return true;
    }

    @Override
    public boolean isEnable() {
        return HookEnv.Config.getBoolean("Set","FakeDevInfoOpen",false);
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        LinearLayout mRoot = new LinearLayout(context);
        mRoot.setOrientation(LinearLayout.VERTICAL);

        CheckBox box = new CheckBox(context);
        box.setText("开启修改设备型号");
        box.setChecked(HookEnv.Config.getBoolean("Set","FakeDevInfoOpen",false));
        mRoot.addView(box);

        EditText ed = new EditText(context);
        ed.setText(HookEnv.Config.getString("Set","FakeDevInfoSet",""));
        mRoot.addView(ed);

        new AlertDialog.Builder(context)
                .setTitle("请设置选项")
                .setView(mRoot)
                .setNegativeButton("保存", (dialog, which) -> {
                    if (ed.getText().toString().isEmpty()){
                        Utils.ShowToast("输入不能为空");
                        return;
                    }
                    HookEnv.Config.setBoolean("Set","FakeDevInfoOpen",box.isChecked());
                    HookEnv.Config.setString("Set","FakeDevInfoSet",ed.getText().toString());
                    HookLoader.CallHookStart(FakeDevInfo.class.getName());
                    new File("/data/data/com.tencent.mobileqq/app_x5webview/Default/Local Storage/leveldb/MANIFEST-000001").delete();
                    new File(HookEnv.Application.getCacheDir().getParent()+"/app_x5webview/Default/Local Storage/leveldb/MANIFEST-000001").delete();
                    new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage("是否立即停止QQ?")
                            .setNegativeButton("立即重启", (dialog1, which1) -> {
                                QQEnvUtils.ExitQQAnyWays();
                            }).show();
                }).show();
    }
}
