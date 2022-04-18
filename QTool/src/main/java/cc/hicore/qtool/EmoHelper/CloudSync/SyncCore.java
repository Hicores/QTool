package cc.hicore.qtool.EmoHelper.CloudSync;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQTools.ContUtil;
import com.lxj.xpopup.XPopup;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncCore {
    public static ExecutorService syncThread = Executors.newFixedThreadPool(16);
    public static void requestShare(Context context,String Name,String LocalName){
        String shareAlarm = "你决定上传名字为 "+Name+" 的表情包,上传期间不能进行其他操作,请耐心等待上传完成,过大的图片可能无法完成上传\n\n" +
                "在上传完成后,你无法上传新的表情包,需要等待审核后才能上传新的,审核通过后表情包即可被他人看到\n\n请确认是否上传";
        Context fixContext = ContUtil.getFixContext(context);
        new XPopup.Builder(fixContext)
                .asConfirm("确认上传", shareAlarm, () -> {
                    CollectInfoAndUpload(fixContext, Name,LocalName);
                }).show();
    }
    private static void CollectInfoAndUpload(Context context,String Name,String LocalName){
        ProgressDialog dialog = new ProgressDialog(context,3);
        dialog.setTitle("正在处理...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread(()->{
            try{
                //扫描需要上传的文件
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在收集信息.."));
                ArrayList<String> fileList = new ArrayList<>();
                File[] fs = new File(HookEnv.ExtraDataPath+"Pic/"+LocalName).listFiles();
                if (fs == null){
                    Utils.ShowToastL("表情包目录为空？？");
                    return;
                }
                for (File f : fs){
                    if (f.isFile() && !f.getName().contains(".")){
                        fileList.add(f.getAbsolutePath());
                    }
                }
                if (fileList.size() < 10){
                    Utils.ShowToastL("仅能上传大于10张的表情");
                    return;
                }
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在创建远程图集..."));
                String URL =
                        "https://qtool.haonb.cc/PicSync/CreateBundle?Uin="+ QQEnvUtils.getCurrentUin()
                        +"&name="+Name;
                String result = HttpUtils.getContent(URL);
                JSONObject json = new JSONObject(result);
                if (json.getInt("code") == 1){
                    Utils.ShowToastL("验证失败,无法创建图集");
                    return;
                }

                //执行图片上传,每一张图都是单独的post,如果有一张失败也会直接结束,需要每一张都上传完成
                String ID = json.getString("id");
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在上传图片..."));
                URL = "https://qtool.haonb.cc/PicSync/Upload";
                for (int i=0;i<fileList.size();i++){
                    int finalI = i;
                    new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在上传图片...["+(finalI +1)+"/"+fileList.size()+"]"));
                    String Path = fileList.get(i);
                    HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Uin",QQEnvUtils.getCurrentUin());
                    connection.setRequestProperty("BundleID",ID);
                    OutputStream out = connection.getOutputStream();
                    out.write(FileUtils.ReadFile(new File(Path)));
                    out.flush();
                    out.close();
                    InputStream ins = connection.getInputStream();
                    DataUtils.readAllBytes(ins);//这里是有返回值的,但是懒得判断了,直接忽略

                    ins.close();
                }
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在分享图集..."));
                URL = "https://qtool.haonb.cc/PicSync/share?uin="+QQEnvUtils.getCurrentUin()+"&id="+ID;
                String shareResult = HttpUtils.getContent(URL);
                JSONObject shareResultJson = new JSONObject(shareResult);
                if (shareResultJson.getInt("code")==1){
                    Utils.ShowToastL("分享成功,请等待审核通过");
                }
            }catch (Exception e){
                Utils.ShowToastL("发生错误:\n"+ Log.getStackTraceString(e));
            }finally {
                new Handler(Looper.getMainLooper()).post(dialog::dismiss);
            }
        }).start();

    }
}
