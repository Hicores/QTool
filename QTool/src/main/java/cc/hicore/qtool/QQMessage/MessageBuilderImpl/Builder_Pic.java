package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.UUID;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.DataUtils;
import cc.hicore.qtool.HookEnv;

@XPItem(name = "Builder_Pic",itemType = XPItem.ITEM_Api)
public class Builder_Pic {
    @VerController
    @ApiExecutor
    public Object onBuild(Object _Session, String PicPath) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade", null, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"), new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                String.class
        });
        Object PICMsg = CallMethod.invoke(null,
                HookEnv.AppInterface, _Session, PicPath
        );
        MField.SetField(PICMsg, "md5", DataUtils.getFileMD5(new File(PicPath)));
        MField.SetField(PICMsg, "uuid", DataUtils.getFileMD5(new File(PicPath)) + ".jpg");
        MField.SetField(PICMsg, "localUUID", UUID.randomUUID().toString());
        MMethod.CallMethodNoParam(PICMsg, "prewrite", void.class);
        return PICMsg;
    }
}
