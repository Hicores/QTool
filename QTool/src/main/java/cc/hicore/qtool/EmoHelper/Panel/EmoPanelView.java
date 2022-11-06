package cc.hicore.qtool.EmoHelper.Panel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.PositionPopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.EmoHelper.Hooker.RepeatWithPic;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.R;


public class EmoPanelView extends BottomPopupView {
    private static String SelectedName = "";
    private ArrayList<View> titleBarList = new ArrayList<>();
    VerticalRecyclerView recyclerView;
    private ArrayList<EmoPanel.EmoInfo> data = new ArrayList<>();
    private ArrayList<ArrayList<EmoPanel.EmoInfo>> multiItem = new ArrayList<>();
    private EasyAdapter<ArrayList<EmoPanel.EmoInfo>> commonAdapter;
    HorizontalScrollView scView;

    static int CacheScrollTop = 0;

    private HashSet<View> cacheImageView = new HashSet<>();


    public EmoPanelView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.emo_list_panel;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        scView = findViewById(R.id.emo_title);
        LinearLayout PathBar = findViewById(R.id.PathBar);

        ArrayList<String> barList = EmoSearchAndCache.searchForPathList();
        for (String name : barList) {
            TextView view = new TextView(getContext());
            view.setText(name);
            view.setTextColor(getResources().getColor(R.color.font_plugin, null));
            view.setTextSize(24);
            LinearLayout.LayoutParams parans = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parans.setMargins(Utils.dip2px(getContext(), 10), 0, Utils.dip2px(getContext(), 10), 0);
            PathBar.addView(view, parans);
            view.requestLayout();
            titleBarList.add(view);

            view.setOnClickListener(v -> {
                CacheScrollTop = 0;
                updateShowPath(name);
                for (View otherItem : titleBarList) {
                    otherItem.setBackgroundColor(getResources().getColor(R.color.bg_plugin, null));
                }
                v.setBackground(getResources().getDrawable(R.drawable.menu_item_base, null));

            });
            view.setOnLongClickListener(v -> {
                EditText edName = new EditText(getContext());
                edName.setText(name);

                new AlertDialog.Builder(getContext(), 3)
                        .setTitle("输入名字")
                        .setView(edName)
                        .setNeutralButton("改名", (dialog, which) -> {
                            new File(HookEnv.ExtraDataPath + "Pic/" + name).renameTo(new File(HookEnv.ExtraDataPath + "Pic/" + edName.getText().toString()));
                            dismiss();
                        }).show();

                return true;
            });
        }

        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        PathBar.measure(width, height);

        recyclerView = findViewById(R.id.recyclerView);

        commonAdapter = new EasyAdapter<ArrayList<EmoPanel.EmoInfo>>(multiItem, R.layout.emo_pic_container) {
            @Override
            protected void bind(@NonNull ViewHolder viewHolder, ArrayList<EmoPanel.EmoInfo> arrayList, int i) {
                LinearLayout container = (LinearLayout) viewHolder.getConvertView();
                container.removeAllViews();

                //更新宽度
                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.height = XPopupUtils.getScreenWidth(getContext()) / 5 + 20;
                container.requestLayout();


                //添加图片项目
                for (EmoPanel.EmoInfo info : arrayList) {
                    ImageView view = new ImageView(getContext());
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LinearLayout.LayoutParams param =
                            new LinearLayout.LayoutParams(XPopupUtils.getScreenWidth(getContext()) / 5, XPopupUtils.getScreenWidth(getContext()) / 5);

                    param.setMargins(XPopupUtils.getScreenWidth(getContext()) / 5 / 5, 10, 0, 10);
                    if (info.type == 1) {
                        Glide.with(HookEnv.AppContext)
                                .load(new File(info.Path))
                                .fitCenter()
                                .into(view);
                        cacheImageView.add(view);

                        view.setOnLongClickListener(v -> {
                            LinearLayout root = new LinearLayout(getContext());
                            root.setGravity(Gravity.CENTER);
                            LinearLayout.LayoutParams paramaaaa =
                                    new LinearLayout.LayoutParams(XPopupUtils.getScreenWidth(getContext()) / 2, XPopupUtils.getScreenWidth(getContext()) / 2);
                            ImageView newView = new ImageView(getContext());
                            root.addView(newView, paramaaaa);
                            Glide.with(HookEnv.AppContext)
                                    .load(new File(info.Path))
                                    .fitCenter()
                                    .into(newView);
                            cacheImageView.add(newView);
                            new AlertDialog.Builder(getContext(), Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                    .setTitle("是否删除此图片")
                                    .setView(root)
                                    .setNeutralButton("删除", (dialog, which) -> {
                                        new File(info.Path).delete();
                                        FindNameToSelectID(SelectedName);
                                    }).show();
                            return true;
                        });
                    } else if (info.type == 2) {
                        try {
                            if (TextUtils.isEmpty(info.thumb)){
                                Glide.with(HookEnv.AppContext)
                                        .load(new URL(info.URL))
                                        .placeholder(R.drawable.loading)
                                        .fitCenter()
                                        .into(view);
                            }else {
                                Glide.with(HookEnv.AppContext)
                                        .load(new URL(info.thumb))
                                        .placeholder(R.drawable.loading)
                                        .fitCenter()
                                        .into(view);
                            }

                            cacheImageView.add(view);
                            view.setOnClickListener(null);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                    container.addView(view, param);
                    view.setOnClickListener(v -> {
                        if (info.type == 2) {

                        } else {
                            if (RepeatWithPic.IsAvailable()) {
                                RepeatWithPic.AddToPreSendList(info.Path);
                            } else {
                                QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo, info.Path));
                            }
                            dismiss();
                        }

                    });
                }
            }
        };
        recyclerView.setAdapter(commonAdapter);
        FindNameToSelectID(SelectedName);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    CacheScrollTop = lastItemPosition;
                }
            }

        });
    }
    private void FindNameToSelectID(String Name) {
        ArrayList<String> NameList = EmoSearchAndCache.searchForPathList();

        if (NameList.isEmpty()) return;
        if (TextUtils.isEmpty(Name)) {
            updateShowPath(NameList.get(0));
            titleBarList.get(0).setBackground(getResources().getDrawable(R.drawable.menu_item_base, null));
        } else if (NameList.contains(Name)) {
            for (int i = 0; i < NameList.size(); i++) {
                if (NameList.get(i).equals(Name)) {
                    int finalI = i;
                    scView.post(() -> {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            scView.scrollTo(titleBarList.get(finalI).getLeft(), 0);
                            titleBarList.get(finalI).setBackground(getResources().getDrawable(R.drawable.menu_item_base, null));
                        });
                    });
                    break;
                }
            }
            updateShowPath(Name);
        } else {
            updateShowPath(NameList.get(0));
            titleBarList.get(0).setBackground(getResources().getDrawable(R.drawable.menu_item_base, null));
        }


    }

    private void updateShowPath(String pathName) {
        multiItem.clear();
        data = EmoSearchAndCache.searchForEmo(pathName);
        int Count = 0;
        int PageCount = 0;
        if (data != null) {
            Count = data.size();
            PageCount = Count / 4 + 1;
        }
        //
        for (int i = 0; i < PageCount; i++) {
            ArrayList<EmoPanel.EmoInfo> itemInfo = new ArrayList<>();
            multiItem.add(itemInfo);
        }

        for (int i = 0; i < data.size(); i++) {
            int NowPage = i / 4;
            ArrayList<EmoPanel.EmoInfo> cacheItem = multiItem.get(NowPage);
            cacheItem.add(data.get(i));
        }
        commonAdapter.notifyDataSetChanged();

        recyclerView.postDelayed(() -> recyclerView.scrollToPosition(CacheScrollTop), 100);
        SelectedName = pathName;
    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }

    @Override
    protected int getPopupHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        for (View v : cacheImageView) {
            Glide.with(HookEnv.AppContext).clear(v);
        }
        Glide.get(HookEnv.AppContext).clearMemory();

    }
}
