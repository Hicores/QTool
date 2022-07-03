package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

@XPItem(name = "Build_Common_Msg",itemType = XPItem.ITEM_Api)
public class Build_Common_Msg {
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    @ApiExecutor
    public Object build(int type) throws InvocationTargetException, IllegalAccessException {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        return CallMethod.invoke(null,type);
    }
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    @ApiExecutor
    public Object build_8_8_93(int type) throws InvocationTargetException, IllegalAccessException {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","d", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        return CallMethod.invoke(null,type);
    }
}
