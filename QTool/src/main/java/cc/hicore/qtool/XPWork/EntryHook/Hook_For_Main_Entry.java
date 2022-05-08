package cc.hicore.qtool.XPWork.EntryHook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.MainMenu;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isDelayInit = false,isRunInAllProc = false)
public class Hook_For_Main_Entry extends BaseHookItem {

    @Override
    public String getTag() {
        return "主界面加号入口";
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            List mMenu = (List) param.args[1];
            Object newItem = MClass.NewInstance(mMenu.get(0).getClass(), new Class[]{
                    int.class, String.class, String.class, int.class
            }, 1699, "QTool", "点击打开QTool主菜单", 3);
            Drawable drawable = HookEnv.AppContext.getDrawable(R.drawable.micon);
            MField.SetField(newItem,"drawable", drawable);
            mMenu.add(0,newItem);
        });

        XPBridge.HookBefore(m[1],param -> {
            View v = (View) param.args[0];
            if (v.getId() == 1699){
                MainMenu.onCreate(Utils.getTopActivity());
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return GlobalConfig.Get_Boolean("Add_Menu_Button_to_Main",false);
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    public Method[] getMethod(){
         Method[] m = new Method[2];
         m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.widget.PopupMenuDialog"),"createAndAttachItemsView",void.class,new Class[]{
                 Activity.class, List.class, LinearLayout.class, boolean.class
         });
         m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.widget.PopupMenuDialog"),"onClick" ,void.class, new Class[]{View.class});
         return m;
    }
}
