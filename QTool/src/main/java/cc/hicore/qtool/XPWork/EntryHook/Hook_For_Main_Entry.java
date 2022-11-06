package cc.hicore.qtool.XPWork.EntryHook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.MainMenu;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;

@SuppressLint("ResourceType")
@XPItem(name = "主界面加号入口", itemType = XPItem.ITEM_Hook)
public class Hook_For_Main_Entry {
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container) {
        container.addMethod("hook_1", MMethod.FindMethod(MClass.loadClass("com.tencent.widget.PopupMenuDialog"), "createAndAttachItemsView", void.class, new Class[]{
                Activity.class, List.class, LinearLayout.class, boolean.class
        }));
        container.addMethod("hook_2", MMethod.FindMethod(MClass.loadClass("com.tencent.widget.PopupMenuDialog"), "onClick", void.class, new Class[]{View.class}));
    }

    @VerController
    @XPExecutor(methodID = "hook_1")
    public BaseXPExecutor worker_1() {
        return param -> {
            List mMenu = (List) param.args[1];
            Object newItem = MClass.NewInstance(mMenu.get(0).getClass(), new Class[]{
                    int.class, String.class, String.class, int.class
            }, 1699, "QTool", "点击打开QTool主菜单", 3);
            Drawable drawable = HookEnv.AppContext.getDrawable(R.drawable.micon);
            MField.SetField(newItem, "drawable", drawable);
            mMenu.add(0, newItem);
        };
    }

    @VerController
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2() {
        return param -> {
            View v = (View) param.args[0];
            if (v.getId() == 1699) {
                MainMenu.onCreate(Utils.getTopActivity());
            }
        };
    }

}
