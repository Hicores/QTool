package cc.hicore.qtool.GroupHelper.GroupHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "群聊快捷菜单",groupName = "群聊助手",targetID = 1,type = 1,id = "GroupHelper")
public class GroupHelperHooker extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            if (IsEnable){
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
                                        }

                                    }).show();
                        }),14);

                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(GroupHelperHooker.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.troop.troopsetting.activity.TroopSettingActivity","doOnCreate",boolean.class,new Class[]{Bundle.class});
    }
}
