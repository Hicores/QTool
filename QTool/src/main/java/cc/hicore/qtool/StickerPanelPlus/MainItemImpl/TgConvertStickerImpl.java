package cc.hicore.qtool.StickerPanelPlus.MainItemImpl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;

import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.ICreator;
import cc.hicore.qtool.StickerPanelPlus.LocalDataHelper;
import cc.hicore.qtool.StickerPanelPlus.MainPanelAdapter;

public class TgConvertStickerImpl implements MainPanelAdapter.IMainPanelItem {
    LinearLayout panelContainer;
    TextView tv_title;
    HashSet<ImageView> cacheImageView = new HashSet<>();

    ViewGroup cacheView;
    Context mContext;
    String updateData;
    String notifyMsg;

    @Override
    public View getView(ViewGroup parent) {
        mContext = parent.getContext();
        onViewDestroy(null);
        notifyViewUpdate();

        return cacheView;
    }

    private void notifyViewUpdate() {
        if (cacheView == null) {
            cacheView = (ViewGroup) View.inflate(mContext, R.layout.sticker_panel_plus_pack_item, null);
            tv_title = cacheView.findViewById(R.id.Sticker_Panel_Item_Name);
            panelContainer = cacheView.findViewById(R.id.Sticker_Item_Container);
            cacheView.findViewById(R.id.Sticker_Panel_Set_Item).setVisibility(View.GONE);
            new Thread(this::getUpdateInfo).start();
            notifyMsg = "TG转换的表情包(加载中...)";
        }
        tv_title.setText(notifyMsg);
        if (!TextUtils.isEmpty(updateData)) {
            try {
                panelContainer.removeAllViews();
                Context context = tv_title.getContext();
                tv_title.setText("TG转换的表情包");
                JSONArray dataArr = new JSONObject(updateData).getJSONArray("data");
                LinearLayout itemLine = null;
                for (int i = 0; i < dataArr.length(); i++) {
                    if (i % 4 == 0) {
                        itemLine = new LinearLayout(context);
                        panelContainer.addView(itemLine);
                    }

                    JSONObject item = dataArr.getJSONObject(i);
                    String id = item.getString("ID");
                    String coverPath = "https://cdn.haonb.cc/" + item.getString("cover");
                    String name = item.getString("name");

                    itemLine.addView(getItemContainer(context, name, coverPath, id, i % 4));
                }
            } catch (Exception e) {
            }
        }
    }

    private void getUpdateInfo() {
        updateData = HttpUtils.getContent("https://qtool.haonb.cc/getByUin?uin=" + QQEnvUtils.getCurrentUin());
        if (TextUtils.isEmpty(updateData)) {
            notifyMsg = "TG转换的表情包(加载失败)";
        } else {
            notifyMsg = "TG转换的表情包";
        }
        Utils.PostToMain(this::notifyViewUpdate);
    }

    private View getItemContainer(Context context, String name, String coverView, String ID, int count) {
        int width_item = Utils.getScreenWidth(context) / 5;
        int item_distance = (Utils.getScreenWidth(context) - width_item * 4) / 3;
        LinearLayout items = new LinearLayout(context);
        items.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width_item, width_item + Utils.dip2px(context, 20));
        if (count > 0) params.leftMargin = item_distance;
        items.setLayoutParams(params);

        ImageView img = new ImageView(context);
        LinearLayout.LayoutParams imgItems = new LinearLayout.LayoutParams(width_item - Utils.dip2px(context, 10), width_item - Utils.dip2px(context, 10));
        imgItems.leftMargin = Utils.dip2px(context, 10) / 2;
        imgItems.topMargin = Utils.dip2px(context, 10) / 2;
        img.setLayoutParams(imgItems);
        items.addView(img);
        cacheImageView.add(img);

        try {
            Glide.with(HookEnv.AppContext).load(new URL(coverView)).into(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TextView title = new TextView(context);
        title.setTextColor(context.getResources().getColor(R.color.font_plugin));
        title.setText(name);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setSingleLine();
        title.setTextSize(10);
        items.addView(title);

        items.setOnClickListener(v -> {
            new AlertDialog.Builder(context, 3).setTitle("提示")
                    .setMessage("是否将该表情包添加到列表?").
                    setPositiveButton("确定", (dialog, which) -> {
                        saveNetShareStickerPackToLocal(ID, name, coverView);
                    }).setNegativeButton("取消", (dialog, which) -> {

                    }).show();
        });

        return items;

    }

    @Override
    public void onViewDestroy(ViewGroup parent) {
        for (ImageView img : cacheImageView) {
            img.setImageBitmap(null);
            Glide.with(HookEnv.AppContext).clear(img);
        }
        cacheImageView.clear();
    }

    @Override
    public long getID() {
        return 9999;
    }

    @Override
    public void notifyViewUpdate0() {

    }

    private void saveNetShareStickerPackToLocal(String ID, String Name, String coverPath) {
        ProgressDialog dialog = new ProgressDialog(mContext, 3);
        dialog.setMessage("正在获取信息...");
        dialog.show();
        new Thread(() -> {
            try {
                String stickerPack = HttpUtils.getContent("https://qtool.haonb.cc/getContent?id=" + ID);
                if (stickerPack == null){
                    Utils.ShowToastL("网络异常");
                    return;
                }
                JSONObject mJson = new JSONObject(stickerPack);
                if (mJson.optInt("code") == 1){
                    String msg = mJson.optString("msg");
                    Utils.ShowToastL("加载错误:"+msg);
                    return;
                }
                JSONArray listArr = new JSONObject(stickerPack).getJSONArray("data");
                for (int i = 0; i < listArr.length(); i++) {
                    JSONObject item = listArr.getJSONObject(i);
                    String uri = item.getString("URI");
                    String url = "https://cdn.haonb.cc/" + uri;
                    String thumbUrl = "https://cdn.haonb.cc/" + item.getString("Thumb");
                    String md5 = item.getString("MD5");
                    LocalDataHelper.LocalPicItems localItem = new LocalDataHelper.LocalPicItems();
                    localItem.url = url;
                    localItem.type = 2;
                    localItem.MD5 = md5;
                    localItem.addTime = System.currentTimeMillis();
                    localItem.fileName = md5;
                    localItem.thumbUrl = thumbUrl;

                    LocalDataHelper.addPicItem(ID, localItem);
                }

                LocalDataHelper.LocalPath path = new LocalDataHelper.LocalPath();
                path.storePath = ID;
                path.coverName = coverPath;
                path.Name = Name;
                LocalDataHelper.addPath(path);

                Utils.PostToMain(() -> {
                    ProgressDialog progress = new ProgressDialog(mContext, 3);
                    progress.setMessage("正在下载贴纸包...");
                    progress.setMessage("正在下载0/0");
                    progress.setCancelable(false);
                    progress.show();

                    new Thread(() -> {
                        try {
                            List<LocalDataHelper.LocalPicItems> items = LocalDataHelper.getPicItems(ID);
                            int count = items.size();
                            int index = 0;
                            for (LocalDataHelper.LocalPicItems item : items) {
                                index++;
                                int finalIndex = index;
                                Utils.PostToMain(() -> {
                                    progress.setMessage("正在下载" + finalIndex + "/" + count);
                                });
                                String localPath = LocalDataHelper.getLocalItemPath(path, item);
                                String thumbPath = LocalDataHelper.getLocalThumbPath(path, item);

                                if (item.thumbUrl.startsWith("http")) {
                                    HttpUtils.DownloadToFile(item.thumbUrl, thumbPath);
                                }
                                if (item.url.startsWith("http")) {
                                    HttpUtils.DownloadToFile(item.url, localPath);
                                }

                                item.type = 1;
                                LocalDataHelper.updatePicItemInfo(path, item);
                            }
                        } catch (Exception e) {
                            Utils.ShowToastL("发生错误:\n" + e);
                        } finally {
                            Utils.PostToMain(progress::dismiss);
                        }
                    }).start();
                });
                Utils.PostToMain(ICreator::dismissAll);

            } catch (Exception e) {
                Utils.ShowToast("发生错误:\n" + e);
            } finally {
                Utils.PostToMain(dialog::dismiss);
            }

        }).start();
    }
}
