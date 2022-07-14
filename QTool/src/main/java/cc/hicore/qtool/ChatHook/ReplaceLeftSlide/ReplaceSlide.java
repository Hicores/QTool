package cc.hicore.qtool.ChatHook.ReplaceLeftSlide;

import java.lang.reflect.Member;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.ReflectUtils.MClass;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = " 替换群侧滑",itemType = XPItem.ITEM_Hook,max_targetVer = QQVersion.QQ_8_9_0)
public class ReplaceSlide{
    CoreLoader.XPItemInfo info;
    @VerController
    @UIItem
    public UIInfo getUIInfo(){
        UIInfo ui = new UIInfo();
        ui.groupName = "聊天界面增强";
        ui.targetID = 1;
        ui.type = 1;
        ui.desc = "替换群聊侧滑为成员探测器";
        ui.name = "替换群侧滑";
        return ui;
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MClass.findCons(MClass.loadClass("com.tencent.mobileqq.activity.aio.drawer.TroopAppShortcutDrawer"),new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")
        }));
    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> {
            Object obj = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.activity.aio.drawer.TroopMultiCardDrawer"),param.args);
            param.setResult(obj);
        };
    }
}
