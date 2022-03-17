package com.hicore.qtool.EmoHelper.Panel;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.Cache;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.QQMessage.QQMsgBuilder;
import com.hicore.qtool.QQMessage.QQMsgSender;
import com.hicore.qtool.R;
import com.lxj.easyadapter.EasyAdapter;
import com.lxj.easyadapter.MultiItemTypeAdapter;
import com.lxj.easyadapter.ViewHolder;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import java.io.File;
import java.util.ArrayList;


public class EmoPanelView extends BottomPopupView {
    private static String SelectedName = "";
    private ArrayList<View> titleBarList = new ArrayList<>();
    VerticalRecyclerView recyclerView;
    private ArrayList<EmoPanel.EmoInfo> data;
    private ArrayList<ArrayList<EmoPanel.EmoInfo>> multiItem = new ArrayList<>();
    private EasyAdapter<ArrayList<EmoPanel.EmoInfo>> commonAdapter;
    HorizontalScrollView scView;


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
        for(String name : barList){
            TextView view = new TextView(getContext());
            view.setText(name);
            view.setTextColor(Color.BLACK);
            view.setTextSize(24);
            LinearLayout.LayoutParams parans = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parans.setMargins(Utils.dip2px(getContext(),10),0,Utils.dip2px(getContext(),10),0);
            PathBar.addView(view,parans);
            view.requestLayout();
            titleBarList.add(view);

            view.setOnClickListener(v-> {
                updateShowPath(name);
                for (View otherItem : titleBarList){
                    otherItem.setBackgroundColor(Color.WHITE);
                }
                v.setBackground(getResources().getDrawable(R.drawable.menu_item_base,null));
            });
        }


        TextView view = new TextView(getContext());
        view.setText("+");
        view.setTextColor(Color.parseColor("#99FFFF"));
        view.setTextSize(24);
        LinearLayout.LayoutParams parans = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parans.setMargins(Utils.dip2px(getContext(),10),0,Utils.dip2px(getContext(),10),0);
        PathBar.addView(view,parans);

        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        PathBar.measure(width,height);

        recyclerView = findViewById(R.id.recyclerView);

        commonAdapter = new EasyAdapter<ArrayList<EmoPanel.EmoInfo>>(multiItem, R.layout.emo_pic_container) {
            @Override
            protected void bind(@NonNull ViewHolder viewHolder, ArrayList<EmoPanel.EmoInfo> arrayList, int i) {
                LinearLayout container = (LinearLayout) viewHolder.getConvertView();
                container.removeAllViews();

                //更新宽度
                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.height = XPopupUtils.getScreenWidth(getContext())/5+20;
                container.requestLayout();


                //添加图片项目
                for (EmoPanel.EmoInfo info : arrayList){
                    ImageView view = new ImageView(getContext());
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LinearLayout.LayoutParams param =
                            new LinearLayout.LayoutParams(XPopupUtils.getScreenWidth(getContext())/5,XPopupUtils.getScreenWidth(getContext())/5);

                    param.setMargins(XPopupUtils.getScreenWidth(getContext())/5/5,10,0,10);
                    if (info.type == 1){
                        Glide.with(getContext())
                                .load(new File(info.Path))
                                .fitCenter()
                                .into(view);
                    }else if (info.type == 2){
                        EmoOnlineLoader.submit(info,()->{
                            Glide.with(getContext())
                                    .load(new File(info.Path))
                                    .fitCenter()
                                    .into(view);
                        });
                    }

                    container.addView(view,param);
                    view.setOnClickListener(v->{
                        QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo,info.Path));
                        dismiss();
                    });
                }
                recyclerView.getScrollY();
            }
        };

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, @NonNull RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        recyclerView.setAdapter(commonAdapter);

        FindNameToSelectID(SelectedName);
    }
    private void FindNameToSelectID(String Name){
        ArrayList<String> NameList = EmoSearchAndCache.searchForPathList();
        if (NameList.isEmpty())return;
        if (TextUtils.isEmpty(Name)){
            updateShowPath(NameList.get(0));
            titleBarList.get(0).setBackground(getResources().getDrawable(R.drawable.menu_item_base,null));
        }else if (NameList.contains(Name)){
            for (int i = 0;i<NameList.size();i++){
                if (NameList.get(i).equals(Name)){
                    int finalI = i;
                    scView.post(()->{
                        new Handler(Looper.getMainLooper()).post(()->{
                            scView.scrollTo(titleBarList.get(finalI).getLeft(),0);
                            titleBarList.get(finalI).setBackground(getResources().getDrawable(R.drawable.menu_item_base,null));
                        });
                    });



                    break;
                }
            }
            updateShowPath(Name);
        }else {
            updateShowPath(NameList.get(0));
            titleBarList.get(0).setBackground(getResources().getDrawable(R.drawable.menu_item_base,null));
        }
    }

    private void updateShowPath(String pathName){
        multiItem.clear();
        data = EmoSearchAndCache.searchForEmo(pathName);
        int Count = 0;
        int PageCount = 0;
        if (data != null){
            Count = data.size();
            PageCount = Count / 4 +1;
        }
        //
        for (int i=0;i<PageCount ;i++){
            ArrayList<EmoPanel.EmoInfo> itemInfo = new ArrayList<>();
            multiItem.add(itemInfo);
        }

        for (int i=0;i<data.size();i++){
            int NowPage = i / 4;
            ArrayList<EmoPanel.EmoInfo> cacheItem = multiItem.get(NowPage);
            cacheItem.add(data.get(i));
        }
        commonAdapter.notifyDataSetChanged();

        SelectedName = pathName;
    }

    //完全可见执行
    @Override
    protected void onShow() {
        super.onShow();

    }

    //完全消失执行
    @Override
    protected void onDismiss() {

    }

    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }

    @Override
    protected int getPopupHeight() {
        return (int) (XPopupUtils.getScreenHeight(getContext()) * .7f);
    }
}
