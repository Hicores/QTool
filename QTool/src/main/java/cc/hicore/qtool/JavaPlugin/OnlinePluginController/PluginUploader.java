package cc.hicore.qtool.JavaPlugin.OnlinePluginController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.NameUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.JavaPlugin.Controller.PluginController;
import cc.hicore.qtool.JavaPlugin.Controller.PluginInfo;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQTools.ContUtil;
import cc.hicore.qtool.XposedInit.EnvHook;
import com.lxj.xpopup.XPopup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PluginUploader {
    private static final String TIP = "请注意\n" +
            "1.脚本上传后,会进入审核器,在审核时期无法再次上传新脚本,只能上传ID相同的脚本进行替换审核\n" +
            "2.审核通过的脚本在未重置模块配置的情况下可以主动移除,如果重置后则需要联系作者\n" +
            "3.上传脚本时候会自动加载一次脚本,只有在正常加载无错误的情况下才会打包上传\n\n" +
            "以下类型的脚本将不会审核通过\n" +
            "1.脚本提供炸群,卡屏,恶意群发等影响QQ正常使用的代码\n" +
            "2.违反法律法规,低素质低道德的脚本\n" +
            "3.全加密导致无法审核是否有恶意代码的脚本(可以选择加密部分关键字符串或代码)\n" +
            "4.说明不明确,版本,名字错乱的脚本,重复上传的脚本\n" +
            "5.在相关接口有效但是仍然使用其他方式绕过接口进行操作的脚本(如果没有接口可以用其他方式实现)\n" +
            "6.破坏其他脚本运行,直接修改模块运行环境的脚本,尝试伪装自己的脚本";
    public static void RequestForUpload(String Path){
        Activity act = Utils.getTopActivity();
        Context fixContext = ContUtil.getFixContext(act);
        new XPopup.Builder(fixContext)
                .asConfirm("提示", TIP, "拒绝", "同意", () -> RequestUpload(Path), () -> { },false).show();
    }

    public static void RequestUpload(String Path){
        String PropFile = Path + "/info.prop";
        if (!new File(PropFile).exists()){
            Utils.ShowToast("你都没有info.prop文件上传个啥呢");
            return;
        }
        if (!new File(Path + "/main.java").exists()){
            Utils.ShowToast("你都没有main.java文件上传个啥呢");
            return;
        }
        if (!new File(Path + "/desc.txt").exists()){
            Utils.ShowToast("你都没有desc.txt文件上传个啥呢");
            return;
        }
        try{
            Properties props = new Properties();
            props.load(new StringReader(FileUtils.ReadFileString(PropFile)));
            String name = props.getProperty("name","未设定");
            String author = props.getProperty("author","未设定");
            String version = props.getProperty("version","未设定");
            String id = props.getProperty("id","未设定");
            String desc = FileUtils.ReadFileString(new File(Path + "/desc.txt"));
            if (name.equals("未设定") || author.equals("未设定") ||version.equals("未设定") || id.equals("未设定") || TextUtils.isEmpty(desc)){
                Utils.ShowToast("信息不完全哦,请补充完成信息再上传吧");
                return;
            }

            //尝试加载脚本
            PluginInfo info = new PluginInfo();
            info.PluginName = name;
            info.LocalPath = Path;
            info.PluginID = id;

            PluginController.endPlugin(info.PluginID);

            if (!PluginController.LoadOnce(info)){
                Utils.ShowToast("脚本存在问题无法加载哦,请重试吧");
                return;
            }

            PluginController.endPlugin(info.PluginID);
            //搜索文件并打包到zip中
            ArrayList<String> ignoreList = new ArrayList<>();
            String ignore = FileUtils.ReadFileString(Path + "/ignore.txt");
            if (!TextUtils.isEmpty(ignore)){
                ignoreList = new ArrayList<>(Arrays.asList(ignore.split("\n")));
            }
            String[] ignores = new String[ignoreList.size()];
            for(int i=0;i<ignores.length;i++)ignores[i] = Path + "/" + ignoreList.get(i);

            Activity act = Utils.getTopActivity();
            ProgressDialog progress = new ProgressDialog(act,3);
            progress.setTitle("正在执行操作...");
            progress.setMessage("正在收集需要打包的文件...");
            progress.setCancelable(false);
            progress.show();

            new Thread(()->{
                try{
                    ArrayList<String> fileList = searchForFile(Path,ignores);
                    String ZipCachePath = HookEnv.ExtraDataPath + "/Cache/"+ NameUtils.getRandomString(8)+".zip";
                    EnvHook.requireCachePath();
                    new Handler(Looper.getMainLooper()).post(()->progress.setMessage("正在打包..."));
                    ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(ZipCachePath));
                    for (String path : fileList){
                        ZipEntry zipEntry = new ZipEntry(path.substring(Path.length()+1));
                        zOut.putNextEntry(zipEntry);
                        FileInputStream fInp =new FileInputStream(path);
                        zOut.write(DataUtils.readAllBytes(fInp));
                        fInp.close();
                    }
                    zOut.close();

                    JSONObject collectInfo = new JSONObject();
                    collectInfo.put("author",author);
                    collectInfo.put("uin", QQEnvUtils.getCurrentUin());
                    collectInfo.put("version",version);
                    collectInfo.put("name",name);
                    collectInfo.put("id",id);
                    collectInfo.put("size",new File(ZipCachePath).length());
                    collectInfo.put("desc",DataUtils.ByteArrayToHex(FileUtils.ReadFileString(Path + "/desc.txt").getBytes(StandardCharsets.UTF_8)));

                    StringBuilder showInfo = new StringBuilder();
                    showInfo.append("名字:").append(name).append("\n");
                    showInfo.append("版本号:").append(version).append("\n");
                    showInfo.append("作者:").append(author).append("\n");
                    showInfo.append("ID:").append(id).append("\n");
                    showInfo.append("总大小:").append(Utils.bytes2kb(new File(ZipCachePath).length())).append("\n");
                    showInfo.append("描述:").append(FileUtils.ReadFileString(Path + "/desc.txt"));

                    new Handler(Looper.getMainLooper())
                            .post(()->{
                                new AlertDialog.Builder(act,3)
                                        .setTitle("确认上传")
                                        .setMessage("请问是否要上传该脚本,脚本信息如下:\n"+showInfo)
                                        .setNegativeButton("取消", (dialog, which) -> {

                                        }).setNeutralButton("上传", (dialog, which) -> {
                                            Upload0(act,collectInfo.toString(),ZipCachePath);
                                        }).show();
                            });
                }catch (Exception e){
                    new Handler(Looper.getMainLooper()).post(()->{
                        new AlertDialog.Builder(act,3)
                                .setTitle("发生错误啦...")
                                .setMessage(e.toString())
                                .setNeutralButton("关闭", (dialog, which) -> {

                                })
                                .show();
                    });
                }finally {
                    new Handler(Looper.getMainLooper()).post(()->progress.dismiss());
                }

            }).start();
        }catch (Exception e){
            Utils.ShowToast("发生错误啦\n"+e);
            return;
        }
    }
    private static void Upload0(Context context,String info,String zipPath){
        ProgressDialog progress = new ProgressDialog(context,3);
        progress.setTitle("正在执行操作...");
        progress.setMessage("正在获取上传ID...");
        progress.setCancelable(false);
        progress.show();
        new Thread(()->{
            try{
                String URL = "https://qtool.haonb.cc/plugin/getUploadKey?data=";
                URL = URL + DataUtils.ByteArrayToHex(info.getBytes(StandardCharsets.UTF_8));

                String reqResult = HttpUtils.getContent(URL);
                JSONObject NewResult = new JSONObject(reqResult);
                if (NewResult.getInt("code") == 1){
                    new Handler(Looper.getMainLooper())
                            .post(()->{
                                try {
                                    new AlertDialog.Builder(context,3)
                                            .setTitle("本次上传被拒绝")
                                            .setMessage(NewResult.getString("msg"))
                                            .setNeutralButton("关闭", (dialog, which) -> {

                                            })
                                            .show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });

                    return;
                }
                String key = NewResult.getString("key");
                new Handler(Looper.getMainLooper()).post(()->progress.setMessage("正在上传中..."));

                String PostURL = "https://qtool.haonb.cc/upload";
                byte[] buffer = new byte[1024 * 1024];//分块上传,每一块1MB
                FileInputStream ins = new FileInputStream(zipPath);
                long size = new File(zipPath).length();
                for (int i=0;i<size / (1024 * 1024) +1;i++){
                    int read = ins.read(buffer);
                    String result = HttpUtils.PostForResult(PostURL,key,buffer,read);
                    if (result.equals("ok"))continue;
                    if (result.contains("上传成功")){
                        ins.close();
                        new Handler(Looper.getMainLooper())
                                .post(()->{
                                    new AlertDialog.Builder(context,3)
                                            .setTitle("上传完成")
                                            .setMessage(result)
                                            .setNeutralButton("关闭", (dialog, which) -> {

                                            })
                                            .show();
                                });

                        return;
                    }
                    ins.close();
                    new Handler(Looper.getMainLooper())
                            .post(()->{
                                new AlertDialog.Builder(context,3)
                                        .setTitle("上传错误")
                                        .setMessage("服务器返回数据:\n"+result)
                                        .setNeutralButton("关闭", (dialog, which) -> {

                                        })
                                        .show();
                            });

                    return;
                }
                new Handler(Looper.getMainLooper())
                        .post(()->{
                            new AlertDialog.Builder(context,3)
                                    .setTitle("上传错误")
                                    .setMessage("上传已结束但服务器未返回成功")
                                    .setNeutralButton("关闭", (dialog, which) -> {

                                    })
                                    .show();
                        });

            }catch (Exception e){
                new Handler(Looper.getMainLooper())
                        .post(()->{
                            new AlertDialog.Builder(context,3)
                                    .setTitle("发生错误啦...")
                                    .setMessage(e.toString())
                                    .setNeutralButton("关闭", (dialog, which) -> {

                                    })
                                    .show();
                        });

            }finally {
                new Handler(Looper.getMainLooper()).post(()->progress.dismiss());
            }
        }).start();

    }
    public static ArrayList<String> searchForFile(String RootPath,String[] ignoreList){
        File[] f = new File(RootPath).listFiles();
        ArrayList<String> newResult = new ArrayList<>();
        if (f != null){
            FileLoop:
            for(File fs : f){
                if (fs.isFile() && !fs.getName().endsWith(".bak") && !fs.getName().contains("error_track") && !fs.getName().endsWith(".zip")){
                    String path = fs.getAbsolutePath();
                    for (String ignore : ignoreList){
                        if (path.startsWith(ignore)){
                            continue FileLoop;
                        }
                    }
                    newResult.add(path);
                }else if (fs.isDirectory()){
                    newResult.addAll(searchForFile(fs.getAbsolutePath(),ignoreList));
                }
            }
        }
        return newResult;
    }
}
