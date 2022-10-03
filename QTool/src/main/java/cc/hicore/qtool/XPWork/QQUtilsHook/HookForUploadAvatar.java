package cc.hicore.qtool.XPWork.QQUtilsHook;

import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.OutputStream;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.DebugUtils;
@XPItem(name = "半透明头像上传",itemType = XPItem.ITEM_Hook)
public class HookForUploadAvatar{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "半透明头像上传";
        ui.desc = "包括个人头像和群头像,需要自行抠图";
        ui.groupName = "功能辅助";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container) throws NoSuchMethodException {
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.pic.compress.Utils", null, boolean.class, new Class[]{
                String.class,
                Bitmap.class,
                int.class,
                String.class,
                MClass.loadClass("com.tencent.mobileqq.pic.CompressInfo")
        }));
        container.addMethod("hook_2",Bitmap.class.getMethod("compress", Bitmap.CompressFormat.class, int.class, OutputStream.class));
        container.addMethod("hook_3",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.avatar.UploadItem"),"a",new Class[]{
                int.class
        }));
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container) throws NoSuchMethodException {
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","options == null || TextUtils.isEmpty(filepath)",m->
                MMethod.FindMethod(m.getDeclaringClass(), null, boolean.class, new Class[]{
                String.class,
                Bitmap.class,
                int.class,
                String.class,
                MClass.loadClass("com.tencent.mobileqq.pic.CompressInfo")
        })));
        container.addMethod("hook_2",Bitmap.class.getMethod("compress", Bitmap.CompressFormat.class, int.class, OutputStream.class));
        container.addMethod(MethodFinderBuilder.newFinderByMethodInvoking("hook_3",MMethod.FindMethodByName(MClass.loadClass("com.tencent.mobileqq.troop.avatar.TroopUploadingThread"),"h"),m-> m.getDeclaringClass().getName().startsWith("com.tencent.mobileqq.troop.avatar") && m.getDeclaringClass().getSimpleName().length() == 1));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker_1(){
        return param -> {
            FileOutputStream fos = new FileOutputStream((String) param.args[0]);
            Bitmap bitmap = (Bitmap) param.args[1];
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            param.setResult(true);
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> {
            String CurrentCallStacks = DebugUtils.getCurrentCallStacks();
            if (CurrentCallStacks.contains("NearbyPeoplePhotoUploadProcessor") || CurrentCallStacks.contains("doInBackground") ||
                    CurrentCallStacks.contains("TroopUploadingThread")) {
                param.args[0] = Bitmap.CompressFormat.PNG;
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_3",period = XPExecutor.After)
    public BaseXPExecutor worker_3(){
        return param -> param.setResult(String.valueOf(param.getResult()).replace("imagetype=5","imagetype=2").replace("filetype=3","filetype=2"));
    }
}
