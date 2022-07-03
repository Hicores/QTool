package cc.hicore.qtool.QQMessage.MessageBuilderImpl;

import java.io.File;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;

@XPItem(name = "Copy_Tuya",itemType = XPItem.ITEM_Api)
public class Copy_Tuya {
    @ApiExecutor
    @VerController
    public Object onCopy(Object SourceObj) throws Exception {
        Object mMessageRecord = QQMsgBuilder.build_common_message_record(-7001);
        MMethod.CallMethod(mMessageRecord, mMessageRecord.getClass().getSuperclass().getSuperclass(), "initInner", void.class,
                new Class[]{String.class, String.class, String.class, String.class, long.class, int.class, int.class, long.class},
                QQEnvUtils.getCurrentUin(), MField.GetField(SourceObj, "frienduin"), QQEnvUtils.getCurrentUin(), "[涂鸦]", System.currentTimeMillis() / 1000, -7001,
                MField.GetField(SourceObj, "istroop"), System.currentTimeMillis() / 1000
        );

        MField.SetField(mMessageRecord, "combineFileUrl", MField.GetField(SourceObj, SourceObj.getClass(), "combineFileUrl", String.class));
        MField.SetField(mMessageRecord, "combineFileMd5", MField.GetField(SourceObj, SourceObj.getClass(), "combineFileMd5", String.class));

        MField.SetField(mMessageRecord, "gifId", MField.GetField(SourceObj, SourceObj.getClass(), "gifId", int.class));

        MField.SetField(mMessageRecord, "offSet", MField.GetField(SourceObj, SourceObj.getClass(), "offSet", int.class));
        MField.SetField(mMessageRecord, "fileUploadStatus", MField.GetField(SourceObj, SourceObj.getClass(), "fileUploadStatus", int.class));
        MField.SetField(mMessageRecord, "fileDownloadStatus", MField.GetField(SourceObj, SourceObj.getClass(), "fileDownloadStatus", int.class));
        String mPath = MField.GetField(SourceObj, "localFildPath");
        if (!new File(mPath).exists()){
            mPath = HookEnv.AppContext.getExternalCacheDir()+ "/"+String.valueOf(Math.random()).substring(2);
            HttpUtils.DownloadToFile(MField.GetField(SourceObj,"combineFileUrl"),mPath);
        }
        MField.SetField(mMessageRecord, "localFildPath", mPath);
        MField.SetField(mMessageRecord, "extStr", MField.GetField(SourceObj, SourceObj.getClass(), "extStr", String.class));
        MField.SetField(mMessageRecord, "msg", "[涂鸦]");
        MMethod.CallMethodNoParam(mMessageRecord, "prewrite", void.class);
        MMethod.CallMethodNoParam(mMessageRecord, "parse", void.class);
        return QQMsgBuilder.rebuild_message(mMessageRecord);
    }
}
