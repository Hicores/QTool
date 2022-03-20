package com.hicore.qtool.QQTools;

import android.os.Bundle;

import com.hicore.ReflectUtils.Classes;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.HookEnv;

import java.util.UUID;

import de.robv.android.xposed.XposedBridge;


//发送QQServlet请求包并截获返回数据
public class QQServletHelper {
    public interface FileGetCallback{
        void OnGetUrl(String URL);
    }
    public static void GetFileDownUrl(Object MessageForTroopFile,FileGetCallback callback){
        try{
            int BusID = 102;
            String Name = MField.GetField(MessageForTroopFile,"fileName");
            String dspName = MField.GetField(MessageForTroopFile,"url");
            String UUIDStr = MField.GetField(MessageForTroopFile,"uuid");
            UUID uuid = UUID.fromString(UUIDStr);
            long groupcode = Long.parseLong(MField.GetField(MessageForTroopFile,"frienduin",String.class));
            Object JobForGroup = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.teamwork.TeamWorkFileImportJobForGroup"),
                    new Class[]{MClass.loadClass("com.tencent.mobileqq.teamwork.bean.TeamWorkFileImportInfo"),Classes.QQAppinterFace()},null,null);

            Class clz = MClass.loadClass("com.tencent.biz.troop.file.protocol.TroopFileReqDownloadFileObserver");
            Object obj = MField.GetFirstField(JobForGroup,clz);


            XPBridge.HookBeforeOnce(MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.teamwork.TeamWorkFileImportJobForGroup$1"),"a",
                    new Class[]{boolean.class,int.class,MClass.loadClass("tencent.im.oidb.cmd0x6d6.oidb_0x6d6$DownloadFileRspBody"), Bundle.class}),param -> {
                if (param.args.length == 4){
                    Object rsp = param.args[2];
                    Object URL = MField.GetField(rsp,"bytes_download_url");
                    Object value = MField.GetField(URL,"value");
                    byte[] b = MField.GetField(value,"bytes");
                    String dlKey = bytes2HexStr(b);

                    Object IP = MField.GetField(rsp,"str_download_ip");
                    String dlIP = MField.GetField(IP,"value" );

                    String FullUrl = "http://"+dlIP+"/ftn_handler/"+dlKey;
                    callback.OnGetUrl(FullUrl);
                }
                param.setResult(null);
            });



            Object item = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item"));

            MField.SetField(item,"FileName",Name);
            MField.SetField(item,"BusId",BusID);
            MField.SetField(item,"FilePath",dspName);
            MField.SetField(item,"Id",uuid);
            MMethod.CallMethod(null,MClass.loadClass("com.tencent.biz.troop.file.TroopFileProtocol"),
                    "a",MClass.loadClass("com.tencent.mobileqq.filemanager.api.ITroopFileProtoReq"),
                    new Class[]{Classes.QQAppinterFace(),long.class,MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item"),
                    int.class,boolean.class,boolean.class,MClass.loadClass("com.tencent.biz.troop.file.protocol.TroopFileReqDownloadFileObserver")},
                    HookEnv.AppInterface,groupcode,item,0,false,false,obj);

        }catch (Exception e){
            XposedBridge.log(e);
            callback.OnGetUrl("");
        }

    }
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String bytes2HexStr(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            try {
                byte b = bArr[i];
                int i2 = i * 2;
                char[] cArr2 = DIGITS;
                cArr[i2 + 1] = cArr2[b & 15];
                cArr[i2 + 0] = cArr2[((byte) (b >>> 4)) & 15];
            } catch (Exception e) {

                return "";
            }
        }
        return new String(cArr);
    }
}
