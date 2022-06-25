package cc.hicore.qtool.ChatHook.ShowForbiddenHook;

import android.content.Context;

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
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.Utils.XPUtils;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQTools.QQHighTipHelper;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XCallback;

@XPItem(name = "显示精确禁言信息",itemType = XPItem.ITEM_Hook)
public class ShowForbiddenInfo {
    @VerController
    @UIItem
    public UIInfo getUIInfo(){
        UIInfo ui = new UIInfo();
        ui.name = "显示精确禁言信息";
        ui.desc = "即使你不是管理员";
        ui.type = 1;
        ui.targetID = 1;
        ui.groupName = "聊天界面增强";
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"),null,void.class,new Class[]{
                String.class,
                long.class,
                long.class,
                int.class,
                String.class,
                String.class,
                boolean.class
        }));
        container.addMethod("hook_2",MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr",null,void.class,new Class[]{
                String.class,
                String.class,
                long.class,
                long.class,
                int.class,
                boolean.class,
                boolean.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1",hook_period = XC_MethodHook.PRIORITY_LOWEST)
    public BaseXPExecutor worker_1(){
        return param -> {
            String GroupUin = (String) param.args[0];
            long TimeRest = (long) param.args[2];
            String AdminUin = (String) param.args[4];
            String Target = (String) param.args[5];
            if(!AdminUin.equals(QQEnvUtils.getCurrentUin())) {
                QQHighTipHelper.HighLightItem[] mItems = new QQHighTipHelper.HighLightItem[2];
                mItems[0] = new QQHighTipHelper.HighLightItem();
                String Name1 = QQGroupUtils.Group_Get_Member_Name(GroupUin,Target);
                mItems[0].Uin = Target;
                mItems[0].Start = 0;
                mItems[0].End = Name1.length();
                String ShowText = Name1+"被";

                mItems[1] = new QQHighTipHelper.HighLightItem();
                mItems[1].Uin = AdminUin;
                mItems[1].Start = ShowText.length();
                Name1 = QQGroupUtils.Group_Get_Member_Name(GroupUin,AdminUin);

                mItems[1].End = ShowText.length()+Name1.length();
                ShowText = ShowText + Name1;
                if(TimeRest==0) ShowText = ShowText + "解除禁言";
                else ShowText = ShowText + "禁言" + Utils.secondToTime(TimeRest);
                QQHighTipHelper.AddHighLightTip(GroupUin,ShowText,mItems);
                param.setResult(null);
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2",hook_period = XC_MethodHook.PRIORITY_LOWEST)
    public BaseXPExecutor worker_2(){
        return param -> {
            String GroupUin = (String) param.args[0];
            String AdminUin = (String) param.args[1];
            long TimeRest = (long) param.args[3];
            boolean b = (boolean) param.args[5];
            param.setResult(null);
            if(b) {
                QQHighTipHelper.HighLightItem[] mItems = new QQHighTipHelper.HighLightItem[1];
                String Name1 = QQGroupUtils.Group_Get_Member_Name(GroupUin,AdminUin);
                String ShowText = "";

                mItems[0] = new QQHighTipHelper.HighLightItem();
                mItems[0].Uin = AdminUin;
                mItems[0].Start = ShowText.length();
                mItems[0].End = ShowText.length()+Name1.length();


                ShowText = ShowText + Name1;
                if(TimeRest==0) ShowText = ShowText + "关闭了全员禁言";
                else            ShowText = ShowText + "开启了全员禁言";


                QQHighTipHelper.AddHighLightTip(GroupUin,ShowText,mItems);
                return;
            }
            QQHighTipHelper.HighLightItem[] mItems = new QQHighTipHelper.HighLightItem[1];
            String Name1 = QQGroupUtils.Group_Get_Member_Name(GroupUin,AdminUin);
            String ShowText = "你被";

            mItems[0] = new QQHighTipHelper.HighLightItem();
            mItems[0].Uin = AdminUin;
            mItems[0].Start = ShowText.length();
            mItems[0].End = ShowText.length()+Name1.length();


            ShowText = ShowText + Name1;
            if(TimeRest==0) ShowText = ShowText + "解除禁言";
            else            ShowText = ShowText + "禁言" + Utils.secondToTime(TimeRest);
            QQHighTipHelper.AddHighLightTip(GroupUin,ShowText,mItems);
        };
    }
}
