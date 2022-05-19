package cc.hicore.qtool.ChatHook.ChatCracker;

import static cc.hicore.qtool.QQGroupHelper.AvatarMenu.AvatarMenuHooker.findView;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "转发消息来源定位",desc = "在合并转发消息中可以点击头像显示资料卡,可以在标题显示来源群号",groupName = "聊天界面增强",targetID = 1,type = 1,id = "TransShowUin")
public class TransShowUin extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
            List MessageRecoreList = MField.GetField(param.thisObject,param.thisObject.getClass() ,"a", List.class);
            if(MessageRecoreList==null)return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);

            Activity context = (Activity) mLayout.getContext();
            if (context.getClass().getName().contains("MultiForwardActivity")){
                int isTroop = MField.GetField(ChatMsg,ChatMsg.getClass() ,"istroop", int.class);
                if(isTroop==1) {
                    String Troop = MField.GetField(ChatMsg,ChatMsg.getClass() ,"frienduin", String.class);
                    View mRootView = context.getWindow().getDecorView();
                    int titleid = context.getResources().getIdentifier("title", "id", context.getPackageName());
                    View mtitleView = mRootView.findViewById(titleid);
                    if(mtitleView instanceof TextView) {
                        TextView mView = (TextView) mtitleView;
                        mView.setText(""+ QQGroupUtils.GetTroopNameByContact(Troop).replace("\n","")+"("+Troop+")");
                        //mView.setWidth(8000);
                        mView.setOnClickListener(v1 -> QQEnvUtils.OpenTroopCard(Troop));
                    }
                }
                View avatar = findView("VasAvatar",mLayout);
                if (avatar != null){
                    String UserUin = MField.GetField(ChatMsg,ChatMsg.getClass() ,"senderuin", String.class);
                    avatar.setOnClickListener(v->{
                        QQEnvUtils.OpenUserCard(UserUin);
                    });
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
        if (IsCheck) HookLoader.CallHookStart(TransShowUin.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod() {
        return MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
    }
}
