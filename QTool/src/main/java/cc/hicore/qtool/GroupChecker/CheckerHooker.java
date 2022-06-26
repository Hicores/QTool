package cc.hicore.qtool.GroupChecker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
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
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.GroupChecker.CheckAlive.CheckCommon;
import cc.hicore.qtool.GroupChecker.CheckAlive.CheckExtra;
import cc.hicore.qtool.GroupChecker.CheckJoinIn.JoinSame;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@XPItem(name = "群聊检测助手",itemType = XPItem.ITEM_Hook)
public class CheckerHooker{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "群聊检测助手";
        ui.groupName = "群聊助手";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.troop.troopsetting.activity.TroopSettingActivity","doOnCreate",boolean.class,new Class[]{Bundle.class}));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor hook_worker(){
        return param -> {
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
                                    }else if (which == 1){
                                        CheckCommon.CollectAndCheck(QQSessionUtils.getGroupUin());
                                    }else if (which == 2){
                                        CheckExtra.CollectAndCheck(QQSessionUtils.getGroupUin());
                                    }

                                }).show();
                    }),14);

                }
            }
        };
    }
}
