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
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.EmoHelper.CloudSync.SyncCore;
import cc.hicore.qtool.EmoHelper.Hooker.RepeatWithPic;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;

import cc.hicore.qtool.R;

import com.bumptech.glide.util.Util;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class EmoPanelView extends BottomPopupView {
    private static String SelectedName = "";
    private ArrayList<View> titleBarList = new ArrayList<>();
    VerticalRecyclerView recyclerView;
    private ArrayList<EmoPanel.EmoInfo> data = new ArrayList<>();
    private ArrayList<ArrayList<EmoPanel.EmoInfo>> multiItem = new ArrayList<>();
    private EasyAdapter<ArrayList<EmoPanel.EmoInfo>> commonAdapter;
    HorizontalScrollView scView;
    Button btnSaveToLocal;

    ExecutorService savePool = Executors.newFixedThreadPool(16);

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
        btnSaveToLocal = findViewById(R.id.SaveToLocal);
        btnSaveToLocal.setOnClickListener(v->{
            EditText ed = new EditText(getContext());
            new AlertDialog.Builder(getContext(),3)
                    .setTitle("输入保存的名字")
                    .setView(ed)
                    .setNeutralButton("保存", (dialog, which) -> {
                        String Name = ed.getText().toString();
                        if (TextUtils.isEmpty(Name)){
                            Utils.ShowToastL("名字不能为空");
                            return;
                        }
                        ProgressDialog mDialog = new ProgressDialog(getContext(),3);
                        mDialog.setTitle("正在保存..");
                        mDialog.setMessage("正在保存图片[0/0]");
                        mDialog.setCancelable(false);
                        mDialog.show();
                        new Thread(()->{
                            try{
                                String RootPath = HookEnv.ExtraDataPath+"Pic/"+Name+"/";
                                AtomicInteger mLock = new AtomicInteger();
                                for (EmoPanel.EmoInfo info : data){
                                    mLock.getAndIncrement();
                                    EmoOnlineLoader.submit2(info,()->{
                                        mLock.getAndDecrement();
                                        new Handler(Looper.getMainLooper()).post(()->mDialog.setMessage("正在保存图片["+ (data.size()-mLock.get()) +"/"+data.size()+"]"));
                                        FileUtils.copy(info.Path,RootPath+info.MD5);
                                    });
                                }
                                for(int iaa=0;iaa<60;iaa++){
                                    if (mLock.get()==0)break;
                                    Thread.sleep(1000);
                                }
                                Utils.ShowToastL("保存完成");
                            }catch (Exception e){

                            }finally {
                                new Handler(Looper.getMainLooper()).post(()->mDialog.dismiss());
                            }
                        }).start();


                    }).show();
        });

        ArrayList<String> barList = EmoSearchAndCache.searchForPathList();
        for(String name : barList){
            TextView view = new TextView(getContext());
            view.setText(name);
            view.setTextColor(getResources().getColor(R.color.font_plugin,null));
            view.setTextSize(24);
            LinearLayout.LayoutParams parans = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parans.setMargins(Utils.dip2px(getContext(),10),0,Utils.dip2px(getContext(),10),0);
            PathBar.addView(view,parans);
            view.requestLayout();
            titleBarList.add(view);

            view.setOnClickListener(v-> {
                CacheScrollTop =0;
                updateShowPath(name);
                for (View otherItem : titleBarList){
                    otherItem.setBackgroundColor(getResources().getColor(R.color.bg_plugin,null));
                }
                v.setBackground(getResources().getDrawable(R.drawable.menu_item_base,null));

            });
            view.setOnLongClickListener(v->{
                EditText edName = new EditText(getContext());
                edName.setText(name);

                new AlertDialog.Builder(getContext(),3)
                        .setTitle("输入名字")
                        .setView(edName)
                        .setNeutralButton("改名", (dialog, which) -> {
                             new File(HookEnv.ExtraDataPath+"Pic/"+name).renameTo(new File(HookEnv.ExtraDataPath+"Pic/"+edName.getText().toString()));
                             dismiss();
                        }).setNegativeButton("上传", (dialog, which) -> {
                             SyncCore.requestShare(getContext(),edName.getText().toString(),name);
                        }).show();

                return true;
            });
        }


        TextView view = new TextView(getContext());
        view.setText("+");
        view.setTextColor(Color.parseColor("#99FFFF"));
        view.setTextSize(24);
        LinearLayout.LayoutParams parans = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parans.setMargins(Utils.dip2px(getContext(),10),0,Utils.dip2px(getContext(),10),0);
        view.setOnClickListener(v-> searchOnline());
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
                        Glide.with(HookEnv.AppContext)
                                .load(new File(info.Path))
                                .fitCenter()
                                .into(view);
                        cacheImageView.add(view);

                        view.setOnLongClickListener(v->{
                            LinearLayout root = new LinearLayout(getContext());
                            root.setGravity(Gravity.CENTER);
                            LinearLayout.LayoutParams paramaaaa =
                                    new LinearLayout.LayoutParams(XPopupUtils.getScreenWidth(getContext())/2,XPopupUtils.getScreenWidth(getContext())/2);
                            ImageView newView = new ImageView(getContext());
                            root.addView(newView,paramaaaa);
                            Glide.with(HookEnv.AppContext)
                                    .load(new File(info.Path))
                                    .fitCenter()
                                    .into(newView);
                            cacheImageView.add(newView);
                            new AlertDialog.Builder(getContext(),Utils.getDarkModeStatus(getContext()) ? AlertDialog.THEME_HOLO_DARK : AlertDialog.THEME_HOLO_LIGHT)
                                    .setTitle("是否删除此图片")
                                    .setView(root)
                                    .setNeutralButton("删除", (dialog, which) -> {
                                        new File(info.Path).delete();
                                        FindNameToSelectID(SelectedName);
                                    }).show();
                            return true;
                        });
                    }else if (info.type == 2){
                        try {
                            Glide.with(HookEnv.AppContext)
                                    .load(new URL(info.URL))
                                    .fitCenter()
                                    .into(view);
                            cacheImageView.add(view);
                            view.setOnClickListener(null);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                    container.addView(view,param);
                    view.setOnClickListener(v->{
                        if (info.type == 2){
                            EmoOnlineLoader.submit(info,()->{
                                if (RepeatWithPic.IsAvailable()){
                                    RepeatWithPic.AddToPreSendList(info.Path);
                                }else {
                                    QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo,info.Path));
                                }
                                dismiss();
                            });
                        }else {
                            if (RepeatWithPic.IsAvailable()){
                                RepeatWithPic.AddToPreSendList(info.Path);
                            }else {
                                QQMsgSender.sendPic(HookEnv.SessionInfo, QQMsgBuilder.buildPic(HookEnv.SessionInfo,info.Path));
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
    private void searchOnline(){
        PositionPopupView view = new PositionPopupView(getContext()){
            @Override
            protected int getImplLayoutId() {
                return R.layout.emo_bundle_list;
            }

            @Override
            protected void onCreate() {
                super.onCreate();
                LinearLayout mRoot = findViewById(R.id.Bundle_Content_List);
                TextView text = new TextView(getContext());
                text.setText("正在加载...");
                mRoot.addView(text);

                new Thread(()->{
                    String Content = HttpUtils.getContent("https://qtool.haonb.cc/sharePic/getList");
                    try {
                        JSONObject json = new JSONObject(Content);
                        new Handler(Looper.getMainLooper()).post(()->{
                            mRoot.removeAllViews();
                            try {
                                JSONArray itemArr = json.getJSONArray("data");
                                LinearLayout mChild = null;
                                for(int i=0;i<itemArr.length();i++){
                                    if (i % 3 == 0){
                                        mChild = new LinearLayout(getContext());
                                        mRoot.addView(mChild);
                                    }
                                    JSONObject bundleInfo = itemArr.getJSONObject(i);
                                    String coverPath = "https://cdn.haonb.cc/"+bundleInfo.optString("cover");
                                    String ID = bundleInfo.getString("id");
                                    LinearLayout.LayoutParams paramab = new LinearLayout.LayoutParams(XPopupUtils.getScreenWidth(getContext())/3, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    paramab.setMargins(0,10,0,10);

                                    LinearLayout mItem = new LinearLayout(getContext());
                                    mItem.setGravity(Gravity.LEFT);
                                    mItem.setOrientation(LinearLayout.VERTICAL);
                                    mChild.addView(mItem,paramab);

                                    ImageView cover = new ImageView(getContext());
                                    Glide.with(HookEnv.AppContext)
                                            .load(new URL(coverPath))
                                            .fitCenter()
                                            .into(cover);
                                    cacheImageView.add(cover);

                                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(XPopupUtils.getScreenWidth(getContext())/4,XPopupUtils.getScreenWidth(getContext())/4);
                                    param.setMargins(0,10,0,10);
                                    mItem.addView(cover,param);

                                    cover.setOnClickListener(v-> {
                                        LoadForOnlinePicList(ID);
                                        dismiss();
                                    });



                                    TextView title = new TextView(getContext());
                                    title.setText(bundleInfo.getString("name"));
                                    mItem.addView(title);
                                    TextView size = new TextView(getContext());
                                    size.setText("总大小:"+Utils.bytes2kb(bundleInfo.optLong("size")));
                                    mItem.addView(size);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            protected int getPopupWidth() {
                return XPopupUtils.getScreenHeight(getContext());
            }
            @Override
            protected int getMaxHeight() {
                return (int) (XPopupUtils.getScreenHeight(getContext()) * .8f);
            }
            @Override
            protected int getPopupHeight() {
                return (int) (XPopupUtils.getScreenHeight(getContext()) * .8f);
            }

        };
        new XPopup.Builder(getContext())
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.TranslateFromTop)
                .asCustom(view)
                .show();
    }

    private void LoadForOnlinePicList(String ID){
        Context context = getContext();
        ProgressDialog dialog = new ProgressDialog(context,3);
        dialog.setTitle("正在加载...");
        dialog.setMessage("正在加载图片列表...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread(()->{
            try{
                String listData = HttpUtils.getContent("https://qtool.haonb.cc/sharePic/getItems?id="+ID);
                JSONObject newJson = new JSONObject(listData);
                JSONArray dataArr = newJson.getJSONArray("data");
                new Handler(Looper.getMainLooper()).post(()->{
                    try{
                        data.clear();
                        btnSaveToLocal.setVisibility(VISIBLE);
                        for (int i=0;i<dataArr.length();i++){
                            JSONObject item = dataArr.getJSONObject(i);
                            EmoPanel.EmoInfo infoItem = new EmoPanel.EmoInfo();
                            infoItem.type = 2;
                            infoItem.URL = "https://cdn.haonb.cc/"+item.getString("uri");
                            infoItem.MD5 = item.getString("md5");
                            data.add(infoItem);
                        }
                        for (View otherItem : titleBarList)otherItem.setBackgroundColor(Color.WHITE);
                        multiItem.clear();

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

                    }catch (Exception e){
                        Utils.ShowToastL("发生错误:"+e);
                    }

                });
            }catch (Exception e){

            }finally {
                new Handler(Looper.getMainLooper()).post(()->dialog.dismiss());
            }
        }).start();
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
        btnSaveToLocal.setVisibility(GONE);
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

        recyclerView.postDelayed(()-> recyclerView.scrollToPosition(CacheScrollTop),100);
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
        for (View v : cacheImageView){
            Glide.with(HookEnv.AppContext).clear(v);
        }

    }
}
