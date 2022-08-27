package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.NameUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSendUtils;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.ICreator;
import cc.hicore.qtool.StickerPanelPlus.LocalDataHelper;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;
import cc.hicore.qtool.StickerPanelPlus.RecentStickerHelper;
import de.robv.android.xposed.XposedBridge;

public class LocalStickerImpl implements MainPanelAdapter.IMainPanelItem {
    ViewGroup cacheView;
    Context mContext;
    LinearLayout panelContainer;
    HashSet<ImageView> cacheImageView = new HashSet<>();
    TextView tv_title;
    LocalDataHelper.LocalPath mPathInfo;
    List<LocalDataHelper.LocalPicItems> mPicItems;
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
                    itemLine.addView(getItemContainer(mContext,LocalDataHelper.getLocalItemPath(mPathInfo,item), i % 5,item));
                }

            }
        }catch (Exception e){XposedBridge.log(Log.getStackTraceString(e));
        }
    }
    private void onSetButtonClick(){
        new AlertDialog.Builder(mContext,3)
                .setTitle("选择你的操作").setItems(new String[]{
                        "删除改表情包", "表情包本地化"
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
                for (int i = 0; i < mPicItems.size(); i++){
                    int finalI = i;
                    Utils.PostToMain(() -> progressDialog.setMessage("正在更新表情包,请稍等...("+ finalI +"/"+mPicItems.size()+")"));
                    LocalDataHelper.LocalPicItems item = mPicItems.get(i);
                    if (item.type == 2 && item.url.startsWith("http")){
                        String localStorePath = LocalDataHelper.getLocalItemPath(mPathInfo, item);
                        if (!TextUtils.isEmpty(localStorePath)){
                            HttpUtils.DownloadToFile(item.url, localStorePath);
                            item.type = 1;
                            item.fileName = item.MD5;
                            LocalDataHelper.updatePicItemInfo(mPathInfo, item);
                        }
                    }
                }
            }catch (Exception e){

            }finally {
                Utils.PostToMain(progressDialog::dismiss);
                Utils.ShowToast("已更新完成");
                Utils.PostToMain(ICreator::dismissAll);
            }
        }).start();

    }
    @Override
    public View getView(ViewGroup parent) {
        onViewDestroy(null);
        notifyDataSetChanged();

        return cacheView;
    }
    private void notifyDataSetChanged(){
        for (ImageView img : cacheImageView){
            String coverView = (String) img.getTag();
            try {
                if (coverView.startsWith("http://") || coverView.startsWith("https://")){
                    Glide.with(HookEnv.AppContext).load(new URL(coverView)).into(img);
                }else {
                    Glide.with(HookEnv.AppContext).load(coverView).into(img);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    private View getItemContainer(Context context, String coverView, int count, LocalDataHelper.LocalPicItems item){
        int width_item = Utils.getScreenWidth(context) / 6;
        int item_distance = (Utils.getScreenWidth(context) - width_item * 5) / 4;

        ImageView img = new ImageView(context);
        cacheImageView.add(img);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width_item , width_item);
        if (count > 0)params.leftMargin = item_distance;
        img.setLayoutParams(params);

        img.setTag(coverView);
        img.setOnClickListener(v->{
            if (coverView.startsWith("http://") || coverView.startsWith("https://")){
                HttpUtils.ProgressDownload(coverView,HookEnv.ExtraDataPath + "Cache/" + coverView.substring(coverView.lastIndexOf("/")),()->{
                    QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo,HookEnv.ExtraDataPath + "Cache/" + coverView.substring(coverView.lastIndexOf("/"))));
                    RecentStickerHelper.addPicItemToRecentRecord(mPathInfo,item);
                },mContext);
                ICreator.dismissAll();

            }else {
                QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo,coverView));
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
        for (ImageView img:cacheImageView){
            img.setImageBitmap(null);
            Glide.with(HookEnv.AppContext).clear(img);
        }
    }

    @Override
    public long getID() {
        return 0;
    }
}
