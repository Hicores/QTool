package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashSet;

import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;

public class MyLoveStickerImpl implements MainPanelAdapter.IMainPanelItem {
    ViewGroup cacheView;
    Context mContext;
    LinearLayout panelContainer;
    TextView tv_title;
    HashSet<ImageView> cacheImageView = new HashSet<>();
    @Override
    public View getView(ViewGroup parent) {
        mContext = parent.getContext();
        onViewDestroy(null);
        notifyDataSetChanged();

        return cacheView;
    }
    private void notifyDataSetChanged(){
        if (cacheView == null){
            cacheView = (ViewGroup) View.inflate(mContext, R.layout.sticker_panel_plus_pack_item, null);
            tv_title = cacheView.findViewById(R.id.Sticker_Panel_Item_Name);
            panelContainer = cacheView.findViewById(R.id.Sticker_Item_Container);
            tv_title.setText("收藏表情");
        }
    }



    @Override
    public void onViewDestroy(ViewGroup parent) {
        for (ImageView img:cacheImageView){
            img.setImageBitmap(null);
            Glide.with(HookEnv.AppContext).clear(img);
        }
        cacheImageView.clear();
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public void notifyViewUpdate0() {

    }
}
