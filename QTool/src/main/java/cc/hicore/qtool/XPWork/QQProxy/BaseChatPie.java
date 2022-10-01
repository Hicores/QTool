package cc.hicore.qtool.XPWork.QQProxy;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.Finders;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XposedBridge;

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
            HookEnv.isCurrentGuild = false;
        };
    }
    public static Object getNewSessionInfo(){
        try {
            return MField.GetFirstField(cacheChatPie, MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @MethodScanner
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieInit(MethodContainer container){
        Finders.BaseChatPieInit_8893(container);
    }
    @MethodScanner
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    public void getBaseChatPieOld(MethodContainer container){
        Finders.BaseChatPieInit(container);
    }
}
