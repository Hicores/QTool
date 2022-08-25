package cc.hicore.qtool.StickerPanelPlus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;

import cc.hicore.Utils.NameUtils;
import cc.hicore.qtool.R;

public class PanelStickerPackHelper {
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
