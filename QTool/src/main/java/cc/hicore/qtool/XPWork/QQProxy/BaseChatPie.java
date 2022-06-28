package cc.hicore.qtool.XPWork.QQProxy;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.qtool.HookEnv;

@XPItem(name = "BaseChatPie_Init",itemType = XPItem.ITEM_Hook)
public class BaseChatPie{
    public static Object cacheChatPie;
    @VerController
    @XPExecutor(methodID = "basechatpie_init",period = XPExecutor.After)
    public BaseXPExecutor worker(){
        return param -> {
            cacheChatPie = param.thisObject;
            HookEnv.AppInterface = MField.GetFirstField(cacheChatPie, MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"));
            HookEnv.SessionInfo = MField.GetFirstField(cacheChatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
        };
    }
    @MethodScanner
    @VerController
    public void getBaseChatPieInit(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("basechatpie_init","AIO_doOnCreate_initUI", m ->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie")));
    }
}
