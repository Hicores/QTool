package cc.hicore.qtool.XPWork.LittleHook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIClick;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
@XPItem(name = "修改设备型号",itemType = XPItem.ITEM_Hook,proc = XPItem.PROC_ALL)
public class FakeDevInfo{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "修改设备型号";
        ui.groupName = "修改";
        ui.targetID = 3;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",getMethod());
        container.addMethod("qzone_hook",MMethod.FindMethod(MClass.loadClass("NS_MOBILE_EXTRA.GetDeviceInfoRsp"),"readFrom",void.class,new Class[]{MClass.loadClass("com.qq.taf.jce.JceInputStream")}));
    }
    @VerController
    @XPExecutor(methodID = "qzone_hook",period = XPExecutor.After)
    public BaseXPExecutor fix_qzone(){
        return param -> {
            List devinfo_List = MField.GetField(param.thisObject,"vecDeviceInfo");
            for (Object devInfo : devinfo_List){
                String name = MField.GetField(devInfo,"strDeviceTail");
                if (name.toLowerCase(Locale.ROOT).startsWith("android"))break;
                String sub = name.indexOf("(") != -1 ? name.substring(name.indexOf("(")) : "";
                name = HookEnv.Config.getString("Set","FakeDevInfoSet","")+sub;
                MField.SetField(devInfo,"strDeviceTail",name);
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor fix_base(){
        return param -> param.setResult(HookEnv.Config.getString("Set","FakeDevInfoSet",""));
    }
    @VerController
    @CommonExecutor
    public void fix_build() throws Exception {
        MField.SetField(null, Build.class,"MODEL",String.class,HookEnv.Config.getString("Set","FakeDevInfoSet",""));
    }
    @VerController
    @UIClick
    public void uiClick(Context context){
        LinearLayout mRoot = new LinearLayout(context);
        mRoot.setOrientation(LinearLayout.VERTICAL);

        CheckBox box = new CheckBox(context);
        box.setText("开启修改设备型号");
        box.setChecked(HookEnv.Config.getBoolean("Set","FakeDevInfoOpen",false));
        //mRoot.addView(box);

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
    public Method getMethod(){
        Method m =  MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.Pandora.deviceInfo.DeviceInfoManager"), "getModel",String.class,new Class[]{
                Context.class
        });
        if (m == null){
            if (HostInfo.getVerCode() < 7830){
                m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pandora.deviceinfo.DeviceInfoManager"), "h",String.class,new Class[]{
                        Context.class
                });
            }else {
                m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pandora.deviceinfo.DeviceInfoManager"), "g",String.class,new Class[]{
                        Context.class
                });
            }
        }
        if (m == null){
            m = MMethod.FindMethod(MClass.loadClass("com.tencent.qmethod.pandoraex.monitor.DeviceInfoMonitor"), "getModel",String.class,new Class[0]);
        }
        return m;
    }
}
