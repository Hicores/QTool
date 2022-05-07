package cc.hicore.qtool.ChatHook.Repeater;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = true, isRunInAllProc = false)
@UIItem(targetID = 1,groupName = "聊天辅助",name = "消息复读+1",id = "RepeaterHooker",type = 2)
public class Hooker extends BaseHookItem implements BaseUiItem {

    public static Drawable cacheDrawable;
    @Override
    public String getTag() {
        return "消息复读+1";
    }

    @Override
    public boolean startHook() throws Throwable {
        requestRepeatIcon();
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0], param -> {
            if (!HookEnv.Config.getBoolean("Repeater","Open",false)) return;
            Object mGetView = param.getResult();
            RelativeLayout baseChatItem = null;
            if (mGetView instanceof RelativeLayout) baseChatItem = (RelativeLayout) mGetView;
            else return;
            Context context= baseChatItem.getContext();
            if (context.getClass().getName().contains("MultiForwardActivity"))return;
            List MessageRecoreList = MField.GetField(param.thisObject, "a", List.class);
            if (MessageRecoreList == null) return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
            if (ChatMsg == null) return;
            String ActivityName = baseChatItem.getContext().getClass().getName();
            if (ActivityName.contains("MultiForwardActivity")) return;
            RepeaterHelper.createRepeatIcon(baseChatItem, ChatMsg);
        });
        return true;
    }

    private static void requestRepeatIcon(){
        String iconPath = HookEnv.ExtraDataPath + "res/repeat.png";
        try{
            if (!new File(iconPath).exists()){
                File path = new File(iconPath).getParentFile();
                path.mkdirs();
                throw new RuntimeException("Can't load Repeat Icon");
            }else {
                cacheDrawable = Drawable.createFromPath(iconPath);
                if (cacheDrawable == null){
                    throw new RuntimeException("Can't load Repeat Icon");
                }
            }

        }catch (Exception e){
            LogUtils.warning("Repeater","Not Found res File,use default icon");
            cacheDrawable = HookEnv.AppContext.getDrawable(R.drawable.repeat);
        }

    }

    @Override
    public boolean isEnable() {
        return HookEnv.Config.getBoolean("Repeater","Open",false);
    }

    @Override
    public boolean check() {
        return getMethod()[0] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        RepeaterSet.startShow(context);
    }

    private Method[] getMethod() {
        Method[] m = new Method[1];
        m[0] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        return m;
    }
}
