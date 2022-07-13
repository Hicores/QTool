package cc.hicore.qtool.QQGroupHelper.GroupHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
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
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
@XPItem(name = "群聊快捷菜单",itemType = XPItem.ITEM_Hook)
public class GroupHelperHooker{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "群聊快捷菜单";
        ui.groupName = "群聊助手";
        ui.targetID = 1;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.troop.troopsetting.activity.TroopSettingActivity","doOnCreate",boolean.class,new Class[]{Bundle.class}));
    }
    @VerController
    @XPExecutor(methodID = "hook",period = XPExecutor.After)
    public BaseXPExecutor worker(){
        return param -> {
            View mGetItem = MField.GetFirstField(param.thisObject,MClass.loadClass("com.tencent.mobileqq.widget.QFormSimpleItem"));
            if (mGetItem != null){
                LinearLayout mRootView = (LinearLayout) mGetItem.getParent();
                if (mRootView != null){
                    Context context = mRootView.getContext();
                    String GroupUin = QQSessionUtils.getGroupUin();
                    mRootView.addView(FormItemUtils.createSingleItem(context,"群聊快捷菜单",v->{
                        String[] items = new String[]{"查看本群禁言数据","查看本群活跃数据","查看本群信用星级"};
                        new AlertDialog.Builder(mRootView.getContext(),3)
                                .setItems(items, (dialog, which) -> {
                                    if (which == 0){
                                        Intent intent = new Intent();
                                        intent.putExtra("troopuin",GroupUin);
                                        intent.setClassName(context,"com.tencent.mobileqq.activity.TroopGagActivity");
                                        context.startActivity(intent);
                                    }else if (which == 1){
                                        Intent intent = new Intent();
                                        intent.putExtra("url", "https://qun.qq.com/m/qun/activedata/active.html?_wv=3&_bid=128&gc=" + GroupUin + "&src=2");
                                        intent.putExtra("PARAM_PLUGIN_INTERNAL_ACTIVITIES_ONLY", false);
                                        intent.putExtra("leftViewText", "返回");
                                        intent.setClassName(context, "com.tencent.mobileqq.activity.QQBrowserActivity");
                                        context.startActivity(intent);
                                    }else if (which == 2){
                                        Intent intent = new Intent();
                                        intent.putExtra("url", "https://qqweb.qq.com/m/business/qunlevel/index.html?gc=" + GroupUin);
                                        intent.putExtra("PARAM_PLUGIN_INTERNAL_ACTIVITIES_ONLY", false);
                                        intent.putExtra("leftViewText", "返回");
                                        intent.setClassName(context, "com.tencent.mobileqq.activity.QQBrowserActivity");
                                        context.startActivity(intent);
                                    }else if (which == 3){
                                        QQGroupUtils.waitForGetGroupInfo(GroupUin);
                                    }

                                }).show();
                    }),14);

                }
            }
        };
    }
}
