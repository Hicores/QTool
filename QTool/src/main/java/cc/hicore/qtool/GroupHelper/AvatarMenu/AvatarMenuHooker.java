package cc.hicore.qtool.GroupHelper.AvatarMenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XPWork.QQProxy.BaseChatPie;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "群聊便捷菜单",desc = "长按头像显示",targetID = 1,type = 1,id = "GroupAvatarMenu",groupName = "群聊助手")
public class AvatarMenuHooker extends BaseHookItem implements BaseUiItem, View.OnLongClickListener {
    static Object chatPie;
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable){
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

            }
        });
        XPBridge.HookBefore(m[1],param -> {
            if (IsEnable){
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
            }
        });

        XPBridge.HookBefore(m[2],param -> chatPie = param.thisObject);
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null && m[2] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(AvatarMenuHooker.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[3];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.vas.avatar.VasAvatar"),"setOnLongClickListener",void.class,new Class[]{
                MClass.loadClass("android.view.View$OnLongClickListener")
        });
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        m[2] = BaseChatPie.getMethod();
        return m;
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
