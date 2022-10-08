package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.EmoHelper.Hooker.RepeatWithPic;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.ICreator;
import cc.hicore.qtool.StickerPanelPlus.LocalDataHelper;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;
import cc.hicore.qtool.StickerPanelPlus.RecentStickerHelper;
import cc.hicore.qtool.StickerPanelPlus.StickerShareHelper;
import de.robv.android.xposed.XposedBridge;

public class LocalStickerImpl implements MainPanelAdapter.IMainPanelItem {
    ViewGroup cacheView;
    Context mContext;
    LinearLayout panelContainer;
    HashSet<ViewInfo> cacheImageView = new HashSet<>();
    TextView tv_title;
    LocalDataHelper.LocalPath mPathInfo;
    List<LocalDataHelper.LocalPicItems> mPicItems;
    public static class ViewInfo{
        ImageView view;
        volatile int status = 0;
    }
    public LocalStickerImpl(LocalDataHelper.LocalPath pathInfo, List<LocalDataHelper.LocalPicItems> picItems,Context mContext){
        mPathInfo = pathInfo;
        mPicItems = picItems;
        this.mContext = mContext;


        cacheView = (ViewGroup) View.inflate(mContext, R.layout.sticker_panel_plus_pack_item, null);
        tv_title = cacheView.findViewById(R.id.Sticker_Panel_Item_Name);
        panelContainer = cacheView.findViewById(R.id.Sticker_Item_Container);
        tv_title.setText(mPathInfo.Name);

        View setButton = cacheView.findViewById(R.id.Sticker_Panel_Set_Item);
        setButton.setOnClickListener(v-> onSetButtonClick());

        try {
            LinearLayout itemLine = null;
            for (int i = 0; i < mPicItems.size(); i++){
                LocalDataHelper.LocalPicItems item = mPicItems.get(i);
                if (i % 5 == 0){
                    itemLine = new LinearLayout(mContext);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = Utils.dip2px(mContext,16);
                    panelContainer.addView(itemLine,params);
                }
                if (item.type == 2){
                    itemLine.addView(getItemContainer(mContext,item.url, i % 5,item));
                }else if (item.type == 1){
                    if (!TextUtils.isEmpty(item.thumbName)){
                        itemLine.addView(getItemContainer(mContext,LocalDataHelper.getLocalThumbPath(mPathInfo,item), i % 5,item));
                    }else {
                        itemLine.addView(getItemContainer(mContext,LocalDataHelper.getLocalItemPath(mPathInfo,item), i % 5,item));
                    }

                }

            }
        }catch (Exception e){XposedBridge.log(Log.getStackTraceString(e));
        }
    }
    private void onSetButtonClick(){
        new AlertDialog.Builder(mContext,3)
                .setTitle("选择你的操作").setItems(new String[]{
                        "删除该表情包", "表情包本地化", "分享该表情包"
                }, (dialog, which) -> {
                    if (which == 0){
                        new AlertDialog.Builder(mContext,3)
                                .setTitle("是否删除该表情包("+tv_title.getText()+"),该表情包内的本地表情将被删除并不可恢复")
                                .setNeutralButton("确定删除", (dialog1, which1) -> {
                                    LocalDataHelper.deletePath(mPathInfo);
                                    ICreator.dismissAll();
                                })
                                .setNegativeButton("取消", (dialog12, which12) -> {

                                }).show();
                    }else if (which == 1){
                        updateAllResToLocal();
                    }else if (which == 2){
                        if (StickerShareHelper.checkIsOnlineSticker(mPathInfo)){
                            new AlertDialog.Builder(mContext,3)
                                    .setTitle("该表情包无法分享,其中包含有在线表情成分")
                                    .setPositiveButton("确定", (dialog1, which1) -> {

                                    }).show();
                        }else{
                            EditText ed = new EditText(mContext);
                            ed.setText(mPathInfo.Name);
                            new AlertDialog.Builder(mContext,3)
                                    .setTitle("请输入分享显示的标题")
                                    .setView(ed)
                                    .setNeutralButton("确定", (dialog13, which13) -> {
                                        String name = ed.getText().toString();
                                        if (TextUtils.isEmpty(name)){
                                            Utils.ShowToastL("标题不能为空");
                                            return;
                                        }
                                        StickerShareHelper.startShareSync(mPicItems,mPathInfo,name,mContext);

                                    }).show();
                        }
                    }
                }).show();
    }
    private void updateAllResToLocal(){
        ProgressDialog progressDialog = new ProgressDialog(mContext,3);
        progressDialog.setTitle("正在更新表情包");
        progressDialog.setMessage("正在更新表情包,请稍等...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> {
            try {
                ExecutorService threadPool = Executors.newFixedThreadPool(8);
                AtomicInteger finishCount = new AtomicInteger();
                int taskCount = mPicItems.size();
                for (LocalDataHelper.LocalPicItems item : mPicItems){
                        threadPool.execute(() -> {
                            try {
                                if (item.url.startsWith("http")){
                                    String localStorePath = LocalDataHelper.getLocalItemPath(mPathInfo, item);
                                    if (!TextUtils.isEmpty(localStorePath)){
                                        HttpUtils.DownloadToFile(item.url, localStorePath);

                                        item.type = 1;
                                        item.fileName = item.MD5;


                                        if (!TextUtils.isEmpty(item.thumbUrl)){
                                            String localThumbPath = LocalDataHelper.getLocalThumbPath(mPathInfo,item);
                                            HttpUtils.DownloadToFile(item.thumbUrl,localThumbPath);
                                            item.thumbName = item.MD5 + "_thumb";
                                        }
                                        LocalDataHelper.updatePicItemInfo(mPathInfo, item);
                                    }
                                }
                            }catch (Exception e){
                                XposedBridge.log(Log.getStackTraceString(e));
                            }finally {
                                Utils.PostToMain(() -> progressDialog.setMessage("正在更新表情包,请稍等...("+ finishCount.getAndIncrement() +"/"+mPicItems.size()+")"));
                            }
                        });
                }
                while (true){
                    if (finishCount.get() == taskCount){
                        break;
                    }
                    Thread.sleep(100);
                }
                Utils.PostToMain(progressDialog::dismiss);
                Utils.ShowToast("已更新完成");
                Utils.PostToMain(ICreator::dismissAll);

            }catch (Exception e){ }
        }).start();

    }
    @Override
    public View getView(ViewGroup parent) {
        onViewDestroy(null);
        return cacheView;
    }
    private View getItemContainer(Context context, String coverView, int count, LocalDataHelper.LocalPicItems item){
        int width_item = Utils.getScreenWidth(context) / 6;
        int item_distance = (Utils.getScreenWidth(context) - width_item * 5) / 4;

        ImageView img = new ImageView(context);
        ViewInfo info = new ViewInfo();
        info.view = img;
        info.status = 0;

        cacheImageView.add(info);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width_item , width_item);
        if (count > 0)params.leftMargin = item_distance;
        img.setLayoutParams(params);

        img.setTag(coverView);
        img.setOnClickListener(v->{
            if (coverView.startsWith("http://") || coverView.startsWith("https://")){
                HttpUtils.ProgressDownload(coverView,HookEnv.ExtraDataPath + "Cache/" + coverView.substring(coverView.lastIndexOf("/")),()->{
                    if (RepeatWithPic.IsAvailable()){
                        RepeatWithPic.AddToPreSendList(HookEnv.ExtraDataPath + "Cache/" + coverView.substring(coverView.lastIndexOf("/")));
                    }else {
                        QQMsgSender.sendPic(QQSessionUtils.getCurrentSession(), QQMsgBuilder.buildPic(QQSessionUtils.getCurrentSession(),HookEnv.ExtraDataPath + "Cache/" + coverView.substring(coverView.lastIndexOf("/"))));
                    }

                    RecentStickerHelper.addPicItemToRecentRecord(mPathInfo,item);
                },mContext);
                ICreator.dismissAll();

            }else {
                if (RepeatWithPic.IsAvailable()){
                    RepeatWithPic.AddToPreSendList(LocalDataHelper.getLocalItemPath(mPathInfo,item));
                }else {
                    QQMsgSender.sendPic(QQSessionUtils.getCurrentSession(), QQMsgBuilder.buildPic(QQSessionUtils.getCurrentSession(),LocalDataHelper.getLocalItemPath(mPathInfo,item)));
                }

                RecentStickerHelper.addPicItemToRecentRecord(mPathInfo,item);
                ICreator.dismissAll();
            }
        });

        img.setOnLongClickListener(v->{
            new AlertDialog.Builder(mContext,3)
                    .setTitle("选择你对该表情的操作")
                    .setItems(new String[]{
                            "删除该表情", "设置为标题预览"
                    }, (dialog, which) -> {
                        if (which == 0){
                            LocalDataHelper.deletePicItem(mPathInfo,item);
                            ICreator.dismissAll();
                        }else if (which == 1){
                            LocalDataHelper.setPathCover(mPathInfo,item);
                            ICreator.dismissAll();
                        }
                    }).show();
            return true;
        });

        return img;

    }

    @Override
    public void onViewDestroy(ViewGroup parent) {
        for (ViewInfo img:cacheImageView){
            img.view.setImageBitmap(null);
            Glide.with(HookEnv.AppContext).clear(img.view);
        }
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public void notifyViewUpdate0() {
        for (ViewInfo v : cacheImageView){
            if (Utils.isSmallWindowNeedPlay(v.view)) {
                if (v.status != 1){
                    v.status = 1;
                    int width_item = Utils.getScreenWidth(mContext) / 6;
                    String coverView = (String) v.view.getTag();
                    try {
                        if (coverView.startsWith("http://") || coverView.startsWith("https://")){
                            Glide.with(HookEnv.AppContext).load(new URL(coverView)).override(width_item,width_item).into(v.view);
                        }else {
                            Glide.with(HookEnv.AppContext).load(coverView).fitCenter().diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(width_item,width_item).into(v.view);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

            }else {
                if (v.status != 0){
                    Glide.with(HookEnv.AppContext).clear(v.view);
                    v.status = 0;
                }


            }
        }
    }


}
