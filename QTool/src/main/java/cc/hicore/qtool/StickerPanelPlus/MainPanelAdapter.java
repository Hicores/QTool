package cc.hicore.qtool.StickerPanelPlus;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

public class MainPanelAdapter extends BaseAdapter {
    public interface IMainPanelItem{
        View getView(ViewGroup parent);
        void onViewDestroy(ViewGroup parent);
        long getID();
    }
    ArrayList<IMainPanelItem> viewData = new ArrayList<>();
    @Override
    public int getCount() {
        return viewData.size();
    }

    public int addItemData(IMainPanelItem item){
        viewData.add(item);
        return viewData.size() -1;
    }

    @Override
    public Object getItem(int position) {
        return viewData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return viewData.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            Object tag = convertView.getTag();
            if (tag instanceof IMainPanelItem){
                IMainPanelItem item = (IMainPanelItem) tag;
                item.onViewDestroy(parent);
            }
        }
        View retView = viewData.get(position).getView(parent);
        retView.setTag(getItem(position));
        return retView;
    }

    public void destroyAllViews(){
        for (IMainPanelItem item : viewData){
            item.onViewDestroy(null);
        }
    }

}
