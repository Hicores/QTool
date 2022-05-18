package cc.hicore.qtool.QQCleaner.StorageClean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cc.hicore.UIItem;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.BaseProxyAct;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQTools.ContUtil;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;

@UIItem(name = "存储空间清理",groupName = "空间清理",targetID = 4,type = 2,id = "StorageCleanView")
@SuppressLint("ResourceType")
public class StorageView implements BaseUiItem {
    private static final HashMap<String,String> cleanPaths = new LinkedHashMap<>();
    static {
        cleanPaths.put("默认外部cache目录","%extra%/cache");
        cleanPaths.put("默认内部cache目录","%private%/cache");
        cleanPaths.put("照片编辑器","%extra%/files/ae");
        cleanPaths.put("tbs_log","%extra%/files/commonlog");
        cleanPaths.put("tbs_cache","%extra%/files/tbs");
        cleanPaths.put("频道图片缓存","%extra%/files/guild");
        cleanPaths.put("一起听歌缓存","%extra%/files/ListenTogether_v828");
        cleanPaths.put("Process_Log","%extra%/files/onelog");
        cleanPaths.put("appsdk_cache","%extra%/files/opensdk_tmp");
        cleanPaths.put("看点缓存","%extra%/files/qcircle");
        cleanPaths.put("看点缓存2","%extra%/qcircle");
        cleanPaths.put("超级QQ秀缓存","%extra%/files/QQShowDownload");
        cleanPaths.put("QQ钱包缓存","%extra%/files/QWallet");
        cleanPaths.put("app_logs","%extra%/files/tencent/");
        cleanPaths.put("地图缓存文件","%extra%/files/tencentmapsdk");
        cleanPaths.put("视频编辑器缓存","%extra%/files/video");
        cleanPaths.put("超级QQ秀缓存2","%extra%/files/zootopia_download");
        cleanPaths.put("QQ空间缓存","%extra%/qzone");
        cleanPaths.put("msf_report_log","%extra%/Tencent/audio");
        cleanPaths.put("小程序缓存,日志","%extra%/Tencent/mini");
        cleanPaths.put("收藏表情缓存","%extra%/Tencent/QQ_Favorite");
        cleanPaths.put("图片编辑器缓存","%extra%/Tencent/QQ_Images");
        cleanPaths.put("开屏广告缓存(?)","%extra%/Tencent/QQ_Shortvideos");
        cleanPaths.put("miniAppSdk_Lib_cache","%extra%/Tencent/wxminiapp");
        cleanPaths.put("QQ秀","%extra%/Tencent/MobileQQ/.apollo");
        cleanPaths.put("铭牌标志缓存","%extra%/Tencent/MobileQQ/.card");
        cleanPaths.put("炫彩群名片缓存","%extra%/Tencent/MobileQQ/.CorlorNick");
        cleanPaths.put("原创表情缓存","%extra%/Tencent/MobileQQ/.emotionsm");
        cleanPaths.put("特效字体缓存","%extra%/Tencent/MobileQQ/.font_effect");
        cleanPaths.put("字体缓存","%extra%/Tencent/MobileQQ/.font_info");
        cleanPaths.put("头像框缓存","%extra%/Tencent/MobileQQ/.pendant");
        cleanPaths.put("资料卡缓存","%extra%/Tencent/MobileQQ/.profilecard");
        cleanPaths.put("入群特效缓存","%extra%/Tencent/MobileQQ/.troop");
        cleanPaths.put("戳一戳缓存","%extra%/Tencent/MobileQQ/.vaspoke");
        cleanPaths.put("语音缓存","%extra%/Tencent/MobileQQ/%uin%/ptt");
        cleanPaths.put("消息截图缓存","%extra%/Tencent/MobileQQ/aio_long_shot");
        cleanPaths.put("聊天图片缓存","%extra%/Tencent/MobileQQ/chatpic");
        cleanPaths.put("图片缓存","%extra%/Tencent/MobileQQ/diskcache");
        cleanPaths.put("头像缓存","%extra%/Tencent/MobileQQ/head");
        cleanPaths.put("热图缓存","%extra%/Tencent/MobileQQ/hotpic");
        cleanPaths.put("flutter_lib_and_res","%extra%/Tencent/MobileQQ/pddata");
        cleanPaths.put("相片预发送缓存","%extra%/Tencent/MobileQQ/photo");
        cleanPaths.put("涂鸦缓存","%extra%/Tencent/MobileQQ/Scribble");
        cleanPaths.put("短视频缓存","%extra%/Tencent/MobileQQ/shortvideo");
        cleanPaths.put("缩略图(?)","%extra%/Tencent/MobileQQ/thumb");
        cleanPaths.put("群聊段位标志缓存","%extra%/Tencent/MobileQQ/troopgamecard");
        cleanPaths.put("不知道哪里的图片缓存","%extra%/Tencent/MobileQQ/troopphoto");
        cleanPaths.put("不知道哪里的lottie缓存","%extra%/Tencent/MobileQQ/vas");
        cleanPaths.put("作图缓存","%extra%/Tencent/MobileQQ/zhitu");
        cleanPaths.put("WebView_Cache","%private%/app_x5webview/Cache");
        cleanPaths.put("Json卡片缓存","%private%/files/ArkApp/Cache");
        cleanPaths.put("消息气泡缓存","%private%/files/bubble_info");
        cleanPaths.put("未知内部缓存","%private%/files/files");
        cleanPaths.put("已下载的小程序","%private%/files/mini");
        cleanPaths.put("pddata","%private%/files/pddata");
        cleanPaths.put("礼物,花里胡哨的VIP图标缓存","%private%/files/vas_material_folder");
    }
    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        createDialog(context);
    }

    public void createDialog(Context context){
        BaseProxyAct.createNewView("StorageCleanView", (Activity) context, context1 -> getContentView(context1));

    }
    ExecutorService calcSize = Executors.newFixedThreadPool(16);
    private View getContentView(Context context){
        ScrollView sc = new ScrollView(context);
        LinearLayout mRoot = new LinearLayout(context);
        sc.addView(mRoot);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(context);

        for (String name : cleanPaths.keySet()){
            String path = getReplacePath(cleanPaths.get(name));
            RelativeLayout item = (RelativeLayout) inflater.inflate(R.layout.storage_clean_item,null);
            TextView nameItem = item.findViewById(R.id.Show_Name);
            nameItem.setText(name);
            mRoot.addView(item);

            Button cleanButton = item.findViewById(R.id.Clean_Now);
            cleanButton.setOnClickListener(v->{
                BasePopupView popup = new XPopup.Builder(ContUtil.getFixContext(context))
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asLoading("正在清理中...");
                popup.show();

                Thread cleanThread = new Thread(()->{
                    try {
                        FileUtils.deleteFile(new File(path));
                    }finally {
                        Utils.PostToMain(()->popup.dismiss());
                        calcSize.submit(()->{
                            long size = FileUtils.getDirSize(new File(path));
                            Utils.PostToMain(()->nameItem.setText(name+"("+Utils.bytes2kb(size)+")"));
                        });
                    }
                });
                cleanThread.setName("QTool_Storage_Cleaner");
                cleanThread.start();

            });

            calcSize.submit(()->{
                long size = FileUtils.getDirSize(new File(path));
                Utils.PostToMain(()->nameItem.setText(name+"("+Utils.bytes2kb(size)+")"));
            });
        }

        return sc;
    }
    private static String getReplacePath(String path){
        return path.replace("%extra%", HookEnv.AppContext.getExternalCacheDir().getParent())
                .replace("%private%",HookEnv.AppContext.getFilesDir().getParent())
                .replace("%uin%", QQEnvUtils.getCurrentUin());
    }
}
