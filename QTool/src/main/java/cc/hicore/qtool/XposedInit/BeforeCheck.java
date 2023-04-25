package cc.hicore.qtool.XposedInit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.IBinder;

import java.lang.reflect.Method;

import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.ServerUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;

public class BeforeCheck {
    private static final StringBuilder checkResult = new StringBuilder();

    public static void StartCheckAndShow() {
        new Thread(BeforeCheck::checkNewVersion).start();
        CheckPermission();
        CheckResInject();
        CheckHiddenApi();
        if (checkResult.length() != 0) {
            Activity act = Utils.getTopActivity();
            if (act != null) {
                new AlertDialog.Builder(act, 3)
                        .setTitle("QTool提示")
                        .setMessage("在模块初始化过程中检测到一个或多个严重错误,模块可能无法正常工作:\n" + checkResult.toString())
                        .setNegativeButton("确定", (dialog, which) -> {

                        }).show();
            }
        }
    }

    private static void checkNewVersion() {
        HookEnv.New_Version = ServerUtils.getNewestCIBuildVer();
    }

    private static void CheckPermission() {
        if (HookEnv.ExtraDataPath != null) {
            if (!ExtraPathInit.CheckPermission(HookEnv.ExtraDataPath) || HookEnv.ExtraDataPath.isEmpty()) {
                checkResult.append("你当前设置的存储路径:").append(HookEnv.ExtraDataPath).append("无效或者没有权限访问,这可能导致脚本,语音无法使用,导致模块的设置无法保存到文件\n");
                checkResult.append("如果你最近做了以下操作,请恢复原状重试:\n1.关闭了QQ的存储权限\n2.修改了模块的配置存储目录权限\n3.使用管理软件拒绝了QQ访问模块目录\n\n你也可以长按设置界面QTool菜单重新进行目录设置\n------------------\n");
            }
        }
    }

    private static void CheckResInject() {
        ResUtils.StartInject(HookEnv.AppContext);
        if (!ResUtils.CheckResInject(HookEnv.AppContext)) {
            checkResult.append("当前模块资源注入不可用,这可能是由于系统更新或者框架提供的模块路径不可用导致的,这会导致模块所有界面显示异常\n1.如果你之前可用在更换或者升级框架后不再可用,请尝试恢复原状\n2.如果是升级系统后不可用,请等待更新\n------------------\n");
        }
    }

    @SuppressLint("BlockedPrivateApi")
    private static void CheckHiddenApi() {
        try {
            Class<?> clz = MClass.loadClass("android.app.ActivityThread");
            Method m = clz.getDeclaredMethod("getLaunchingActivity", IBinder.class);
            if (m == null) {
                throw new RuntimeException("getLaunchingActivity");
            }
        } catch (Throwable th) {
            try {
                Class runtimeClass = MClass.loadClass("dalvik.system.VMRuntime");
                Method m2 = runtimeClass.getDeclaredMethod("setTargetSdkVersionNative",
                        int.class);
                if (m2 == null) {
                    throw new RuntimeException("setTargetSdkVersionNative");
                }
            } catch (Throwable th2) {
                checkResult.append("当前无法访问被限制的AndroidAPI,这可能导致模块部分功能异常,比如界面显示出现异常\n1.如果你是升级框架后出现该问题,请降级后再试\n2.如果你是刚使用则出现该问题,请更换框架后再试\n------------------\n");
            }
        }

    }
    public static void showGrayQQTip(){
        Activity act = Utils.getTopActivity();
        if (act != null) {
            new AlertDialog.Builder(act, 3)
                    .setTitle("提示")
                    .setMessage("你当前使用的可能不是官方正式版QQ,要正常使用QTool,你需要下载官方正式版QQ后再进行使用" )
                    .setNegativeButton("确定", (dialog, which) -> {

                    }).show();
        }
    }


}
