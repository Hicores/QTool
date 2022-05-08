package cc.hicore.qtool.GroupChecker;

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
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.GroupChecker.CheckJoinIn.JoinSame;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "群聊检测助手",groupName = "群聊助手",targetID = 1,type = 1,id = "GroupCheckerHooker")
public class CheckerHooker extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(), param -> {
            if (IsEnable){
                View mGetItem = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.mobileqq.widget.QFormSimpleItem"));
                if (mGetItem != null){
                    LinearLayout mRootView = (LinearLayout) mGetItem.getParent();
                    if (mRootView != null){
                        Context context = mRootView.getContext();
                        mRootView.addView(FormItemUtils.createSingleItem(context,"群聊检测助手", v->{
                            String[] items = new String[]{"检测重复加群","检测不活跃用户(最后发言时间)","检测不活跃用户(规则2)"};
                            new AlertDialog.Builder(mRootView.getContext(),3)
                                    .setItems(items, (dialog, which) -> {
                                        if (which == 0){
                                            JoinSame.start(mRootView.getContext());
                                        }else {
                                            Utils.ShowToast("未完成............................");
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
        if (IsCheck) HookLoader.CallHookStart(CheckerHooker.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.troop.troopsetting.activity.TroopSettingActivity","doOnCreate",boolean.class,new Class[]{Bundle.class});
    }

}
