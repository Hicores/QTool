package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;

import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.ICreator;
import cc.hicore.qtool.StickerPanelPlus.LocalDataHelper;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;
import cc.hicore.qtool.StickerPanelPlus.RecentStickerHelper;
import de.robv.android.xposed.XposedBridge;

public class RecentStickerImpl implements MainPanelAdapter.IMainPanelItem {
    ViewGroup cacheView;
    Context mContext;
    LinearLayout panelContainer;
    HashSet<ImageView> cacheImageView = new HashSet<>();
    TextView tv_title;

    List<RecentStickerHelper.RecentItemInfo> items;
    public RecentStickerImpl(Context mContext){
        this.mContext = mContext;
        items = RecentStickerHelper.getAllRecentRecord();

        cacheView = (ViewGroup) View.inflate(mContext, R.layout.sticker_panel_plus_pack_item, null);
        tv_title = cacheView.findViewById(R.id.Sticker_Panel_Item_Name);
        panelContainer = cacheView.findViewById(R.id.Sticker_Item_Container);
        tv_title.setText("最近使用");

        View setButton = cacheView.findViewById(R.id.Sticker_Panel_Set_Item);

        try {
            LinearLayout itemLine = null;
            for (int i = 0; i < items.size(); i++){
                RecentStickerHelper.RecentItemInfo item = items.get(items.size() - i - 1);
                if (i % 5 == 0){
                    itemLine = new LinearLayout(mContext);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = Utils.dip2px(mContext,16);
                    panelContainer.addView(itemLine,params);
                }
                if (item.type == 2){
                    itemLine.addView(getItemContainer(mContext,item.url, i % 5,item));
                }else if (item.type == 1){
                    itemLine.addView(getItemContainer(mContext,LocalDataHelper.getLocalItemPath(item), i % 5,item));
                }

            }
        }catch (Exception e){
            XposedBridge.log(Log.getStackTraceString(e));
        }
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
    private View getItemContainer(Context context, String coverView, int count, RecentStickerHelper.RecentItemInfo item){
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
                    RecentStickerHelper.addPicItemToRecentRecord(item);
                },mContext);
                ICreator.dismissAll();

            }else {
                QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo,coverView));
                RecentStickerHelper.addPicItemToRecentRecord(item);
                ICreator.dismissAll();
            }
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
