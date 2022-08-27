package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.app.AlertDialog;
import android.content.Context;
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

import cc.hicore.Utils.NameUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.LocalDataHelper;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;
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
                itemLine.addView(getItemContainer(mContext,item.url, i % 5));
            }
        }catch (Exception e){XposedBridge.log(Log.getStackTraceString(e));
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
    private View getItemContainer(Context context,String coverView,int count){
        int width_item = Utils.getScreenWidth(context) / 6;
        int item_distance = (Utils.getScreenWidth(context) - width_item * 5) / 4;

        ImageView img = new ImageView(context);
        cacheImageView.add(img);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width_item , width_item);
        if (count > 0)params.leftMargin = item_distance;
        img.setLayoutParams(params);

        img.setTag(coverView);

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

    public static class StickerPackViewInfo{
        public ViewGroup packView;
        public String ID;
        public View[] glideViews;
    }
    public static class CreatePanelPackInfo{
        public Context context;
        public List<PackItemInfo> items;
    }
    public static class PackItemInfo{
        public static final int TYPE_LOCAL = 1;
        public static final int TYPE_NET_URL = 2;
        public int type;
        public int path;
    }
    public static StickerPackViewInfo getPanelSticker(CreatePanelPackInfo context){
        StickerPackViewInfo newInfo = new StickerPackViewInfo();
        newInfo.ID = NameUtils.getRandomString(16);
        newInfo.packView = (ViewGroup) LayoutInflater.from(context.context).inflate(R.layout.sticker_panel_plus_pack_item, null);
        initPackItems(newInfo,context);
        return newInfo;
    }

    private static HashMap<String,StickerPackViewInfo> viewCache = new HashMap<>();
    private static void initPackItems(StickerPackViewInfo retInfo,CreatePanelPackInfo info){
        LinearLayout container = (LinearLayout) retInfo.packView.findViewById(R.id.Sticker_Item_Container);
        LinearLayout lineContainer;
        for (int i=0;i<info.items.size();i++){
            if (i % 5 == 0){
                lineContainer = new LinearLayout(info.context);
                container.addView(lineContainer);
            }
        }
    }



    public static void destroyStickerPackView(String ViewID){

    }
}
