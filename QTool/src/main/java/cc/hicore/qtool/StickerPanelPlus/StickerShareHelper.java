package cc.hicore.qtool.StickerPanelPlus;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.QQManager.QQEnvUtils;

public class StickerShareHelper {
    public static boolean checkIsOnlineSticker(LocalDataHelper.LocalPath pathData){
        List<LocalDataHelper.LocalPicItems> items = LocalDataHelper.getPicItems(pathData.storePath);
        for (LocalDataHelper.LocalPicItems item : items){
            if (item.type == 2){
                return true;
            }
        }
        return false;
    }
    public static void startShareSync(List<LocalDataHelper.LocalPicItems> items, LocalDataHelper.LocalPath path, String showName, Context context){
        ProgressDialog progDialog = new ProgressDialog(context,3);
        progDialog.setCancelable(false);
        progDialog.setTitle("正在处理...");
        progDialog.setMessage("正在准备...");
        progDialog.show();
        new Thread(()->{
            try {
                if (items.size() < 10 ){
                    Utils.ShowToastL("仅能上传大于十张的表情包");
                    return;
                }
                new Handler(Looper.getMainLooper()).post(() -> progDialog.setMessage("正在创建远程图集..."));
                JSONObject requestData = new JSONObject();
                requestData.put("uin", QQEnvUtils.getCurrentUin());
                requestData.put("name", showName);
                String URL =
                        "https://qtool.haonb.cc/StickerHelper/createPack?in="+ DataUtils.ByteArrayToHex(requestData.toString().getBytes());
                String result = HttpUtils.getContent(URL);
                JSONObject json = new JSONObject(result);
                if (json.getInt("code") == 1) {
                    Utils.ShowToastL("验证失败,无法创建图集:\n"+json.getString("msg"));
                    return;
                }


                String key = json.getString("key");
                new Handler(Looper.getMainLooper()).post(() -> progDialog.setMessage("正在上传图片..."));
                URL = "https://qtool.haonb.cc/StickerHelper/upload";
                for (int i = 0; i < items.size(); i++) {
                    int finalI = i;
                    new Handler(Looper.getMainLooper()).post(() -> progDialog.setMessage("正在上传图片...[" + (finalI + 1) + "/" + items.size() + "]"));
                    LocalDataHelper.LocalPicItems picItem = items.get(i);
                    HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("key", key);
                    OutputStream out = connection.getOutputStream();
                    out.write(FileUtils.ReadFile(new File(LocalDataHelper.getLocalItemPath(path,picItem))));
                    out.flush();
                    out.close();
                    InputStream ins = connection.getInputStream();
                    DataUtils.readAllBytes(ins);//这里是有返回值的,但是懒得判断了,直接忽略

                    ins.close();
                }
                new Handler(Looper.getMainLooper()).post(() -> progDialog.setMessage("正在分享图集..."));
                URL = "https://qtool.haonb.cc/StickerHelper/uploadStop?key=" + key;
                String shareResult = HttpUtils.getContent(URL);
                JSONObject shareResultJson = new JSONObject(shareResult);
                if (shareResultJson.getInt("code") == 0) {
                    Utils.ShowToastL("分享成功,请等待审核通过");
                }

            }catch (Exception e){
                Utils.ShowToastL("发生错误:\n"+e);
            }finally {
                Utils.PostToMain(progDialog::dismiss);
            }
        }).start();
    }
}
