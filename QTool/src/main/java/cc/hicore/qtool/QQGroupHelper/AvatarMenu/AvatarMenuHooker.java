package cc.hicore.qtool.QQGroupHelper.AvatarMenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import de.robv.android.xposed.XposedBridge;
@XPItem(name = "群聊便捷菜单",itemType = XPItem.ITEM_Hook)
public class AvatarMenuHooker implements View.OnLongClickListener {
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "群聊便捷菜单";
        ui.desc = "长按群聊内头像显示";
        ui.groupName = "群聊助手";
        ui.targetID = 1;
        ui.type = 1;
        return ui;
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.vas.avatar.VasAvatar"),"setOnLongClickListener",void.class,new Class[]{
                MClass.loadClass("android.view.View$OnLongClickListener")
        }));
        Finders.AIOMessageListAdapter_getView(container);
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod_890(MethodContainer container){
        Finders.AIOMessageListAdapter_getView_890(container);
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.vas.avatar.VasAvatar"),"setOnLongClickListener",void.class,new Class[]{
                MClass.loadClass("android.view.View$OnLongClickListener")
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1(){
        return param -> {
            View v = (View) param.thisObject;
            Context context = v.getContext();
            if (context.getClass().getName().contains("MultiForwardActivity"))return;

            Object chatMsg = v.getTag();
            if (chatMsg != null){
                int istroop = MField.GetField(chatMsg,"istroop",int.class);
                if (istroop == 1){
                    XposedBridge.invokeOriginalMethod(param.method,param.thisObject,new Object[]{this});
                    param.setResult(null);
                }
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "onAIOGetView",period = XPExecutor.After)
    public BaseXPExecutor worker_2(){
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
            Context context= mLayout.getContext();
            if (context.getClass().getName().contains("MultiForwardActivity"))return;

            View avatar = findView("VasAvatar",mLayout);
            if (avatar != null){
                Object chatMsg = avatar.getTag();
                if (chatMsg != null){
                    int istroop = MField.GetField(chatMsg,"istroop",int.class);
                    if (istroop == 1){
                        avatar.setOnLongClickListener(null);
                    }
                }

            }
        };
    }
    @Override
    public boolean onLongClick(View v) {
        Object chatMsg = v.getTag();
        if (chatMsg != null){
            AvatarMenuBuilder.showAvatarMenu(v.getContext(),chatMsg);
        }
        return true;
    }
    public static View findView(String Name, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            if (vg.getChildAt(i).getClass().getSimpleName().contains(Name)) {
                return vg.getChildAt(i);
            }
        }
        return null;
    }
}
