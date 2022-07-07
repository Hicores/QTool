package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;

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
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieInit(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("basechatpie_init","AIO_doOnCreate_initUI", m ->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.core.BaseChatPie")));
    }
    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieOld(MethodContainer container){
        Method m;
        if (HostInfo.getVerCode() > 6440) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "s", void.class, new Class[0]);
        } else if (HostInfo.getVerCode() > 5870) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "r", void.class, new Class[0]);
        } else if (HostInfo.getVerCode() > 5570) {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "q", void.class, new Class[0]);
        } else {
            m = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.core.BaseChatPie",
                    "f", void.class, new Class[0]);
        }
        container.addMethod("basechatpie_init",m);
    }
}
