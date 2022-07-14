package cc.hicore.qtool.ChatHook.ChatCracker;


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
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "去除回复自动艾特",itemType = XPItem.ITEM_Hook)
public class DisableReplayAt{
    @UIItem
    @VerController
    public UIInfo getUIItem(){
        UIInfo info = new UIInfo();
        info.groupName = "聊天界面增强";
        info.type = 1;
        info.targetID = 1;
        info.name = "去除回复自动艾特";
        return info;
    }

    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    public void findHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.rebuild.input.InputUIUtils"),"a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.activity.aio.core.AIOContext"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                boolean.class
        }));
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    public void findHookMethod_890(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","@",m-> {
            if (m.getDeclaringClass().getName().startsWith("com.tencent.mobileqq.activity.aio.rebuild.input")){
                return MMethod.FindMethodByName(m.getDeclaringClass(),"a");
            }
            return false;
        }));
    }

    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor hookWorker(){
        return param -> param.setResult(null);
    }
}
