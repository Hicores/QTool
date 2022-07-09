package cc.hicore.qtool.XPWork.LittleHook;

import android.view.ViewGroup;
import android.widget.TextView;

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
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
@XPItem(name = "显示完整消息数量",itemType = XPItem.ITEM_Hook)
public class ShowFullMessageCount {
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "显示完整消息数量";
        ui.groupName = "功能辅助";
        ui.targetID = 1;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.widget.CustomWidgetUtil"),null,void.class,new Class[]{
                TextView.class, int.class, int.class, int.class, int.class, String.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor before(){
        return param -> param.args[4]=Integer.MAX_VALUE;
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor after(){
        return param -> {
            int Type = (int) param.args[1];
            if(Type == 4 || Type == 7 || Type == 9|| Type == 3) {
                TextView t = (TextView) param.args[0];
                int Count = (int) param.args[2];
                String str = ""+Count;
                ViewGroup.LayoutParams params = t.getLayoutParams();
                params.width = Utils.dip2px(HookEnv.AppContext,9+7*str.length());
                t.setLayoutParams(params);
            }
        };
    }
}
