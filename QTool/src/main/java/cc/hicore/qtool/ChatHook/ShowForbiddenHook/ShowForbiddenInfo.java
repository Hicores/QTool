package cc.hicore.qtool.ChatHook.ShowForbiddenHook;

import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.QQTools.QQHighTipHelper;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.callbacks.XCallback;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "显示精确禁言信息",desc = "即使你不是管理员",groupName = "聊天界面增强",targetID = 1,type = 1,id = "ShowForbiddenInfo")
public class ShowForbiddenInfo extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable){
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
            }
        }, XCallback.PRIORITY_LOWEST);
        XPBridge.HookBefore(m[1],param -> {
            if (IsEnable){
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
            }
        }, XCallback.PRIORITY_LOWEST);
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(ShowForbiddenInfo.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"),"a",void.class,new Class[]{
                String.class,
                long.class,
                long.class,
                int.class,
                String.class,
                String.class,
                boolean.class
        });
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr","a",void.class,new Class[]{
                String.class,
                String.class,
                long.class,
                long.class,
                int.class,
                boolean.class,
                boolean.class
        });
        return m;

    }
}
