package cc.hicore.qtool.StickerPanelPlus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;

import java.util.ArrayList;
import java.util.List;

import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQTools.ContUtil;
import cc.hicore.qtool.R;
import cc.hicore.qtool.StickerPanelPlus.MainItemImpl.OnlineStickerImpl;

@SuppressLint("ResourceType")
public class ICreator extends BottomPopupView implements AbsListView.OnScrollListener {
    public ICreator(@NonNull Context context) {
        super(context);
    }
    public static void createPanel(Context context){
        ResUtils.StartInject(context);
        Context fixContext = ContUtil.getFixContext(context);
        XPopup.Builder NewPop = new XPopup.Builder(fixContext).isDestroyOnDismiss(true);
        BasePopupView base = NewPop.asCustom(new ICreator(fixContext));
        base.show();
    }
    MainPanelAdapter adapter = new MainPanelAdapter();
    int IdOfShareGroup;
    LinearLayout topSelectBar;
    private List<ViewGroup> newTabView = new ArrayList<>();
    private void initTopSelectBar(){
        topSelectBar = findViewById(R.id.Sticker_Pack_Select_Bar);
    }
    private ListView listView;
    private void initListView(){
        listView = findViewById(R.id.Sticker_Panel_Main_List_View);
        listView.setAdapter(adapter);
        listView.setVerticalScrollBarEnabled(false);
        listView.setOnScrollListener(this);
    }
    private void initStickerPacks(){

    }
    private void initDefItemsBefore(){

    }
    private void initDefItemsLast(){
        ViewGroup tabView = (ViewGroup) createPicImage(R.drawable.share,"分享表情", v -> listView.smoothScrollToPositionFromTop(IdOfShareGroup,0));
        IdOfShareGroup = adapter.addItemData(new OnlineStickerImpl());
        tabView.setTag(IdOfShareGroup);
        topSelectBar.addView(tabView);


        topSelectBar.addView(createPicImage(R.drawable.sticker_pack_set_icon,"设置分组",v -> Utils.ShowToast("Click")));
    }
    public void notifyTabViewSelect(ViewGroup vg){
        for (ViewGroup v : newTabView){
            v.findViewById(887533).setVisibility(GONE);
        }
        vg.findViewById(887533).setVisibility(VISIBLE);
    }
    //创建贴纸包面板的滑动按钮
    private View createPicImage(String localPath,String title,View.OnClickListener clickListener){
        ImageView img = new ImageView(getContext());
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout panel = new LinearLayout(getContext());
        panel.setGravity(Gravity.CENTER_HORIZONTAL);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setOnClickListener(clickListener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),30),Utils.dip2px(getContext(),30));
        params.topMargin = Utils.dip2px(getContext(),3);
        params.bottomMargin = Utils.dip2px(getContext(),3);
        params.leftMargin = Utils.dip2px(getContext(),3);
        params.rightMargin = Utils.dip2px(getContext(),3);
        img.setLayoutParams(params);
        panel.addView(img);

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextColor(getResources().getColor(R.color.font_plugin,null));
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setTextSize(10);
        titleView.setSingleLine();
        panel.addView(titleView);


        Glide.with(HookEnv.AppContext).load(localPath).into(img);
        LinearLayout.LayoutParams panel_param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),50),ViewGroup.LayoutParams.WRAP_CONTENT);
        panel_param.leftMargin = Utils.dip2px(getContext(),5);
        panel_param.rightMargin = Utils.dip2px(getContext(),5);
        panel.setLayoutParams(panel_param);

        newTabView.add(panel);
        return panel;
    }
    @SuppressLint("ResourceType")
    private View createPicImage(int resID, String title, View.OnClickListener clickListener){
        ImageView img = new ImageView(getContext());
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout panel = new LinearLayout(getContext());
        panel.setGravity(Gravity.CENTER_HORIZONTAL);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setOnClickListener(clickListener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),30),Utils.dip2px(getContext(),30));
        params.topMargin = Utils.dip2px(getContext(),3);
        params.bottomMargin = Utils.dip2px(getContext(),3);
        params.leftMargin = Utils.dip2px(getContext(),3);
        params.rightMargin = Utils.dip2px(getContext(),3);
        img.setLayoutParams(params);
        panel.addView(img);

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setTextColor(getResources().getColor(R.color.font_plugin,null));
        titleView.setTextSize(10);
        titleView.setSingleLine();
        panel.addView(titleView);

        Glide.with(HookEnv.AppContext).load(resID).into(img);

        LinearLayout.LayoutParams panel_param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),50),ViewGroup.LayoutParams.WRAP_CONTENT);
        panel_param.leftMargin = Utils.dip2px(getContext(),5);
        panel_param.rightMargin = Utils.dip2px(getContext(),5);
        panel.setLayoutParams(panel_param);

        View greenTip = new View(getContext());
        greenTip.setBackgroundColor(Color.GREEN);
        panel_param = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),30),50);
        panel_param.leftMargin = Utils.dip2px(getContext(),5);
        panel_param.rightMargin = Utils.dip2px(getContext(),5);
        greenTip.setLayoutParams(panel_param);
        greenTip.setId(887533);
        greenTip.setVisibility(GONE);
        panel.addView(greenTip);

        newTabView.add(panel);

        return panel;
    }
    @Override
    protected void onCreate() {
        super.onCreate();

        initTopSelectBar();

        initListView();

        initDefItemsBefore();

        initStickerPacks();

        initDefItemsLast();


        adapter.notifyDataSetChanged();
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
        adapter.destroyAllViews();
        Glide.get(HookEnv.AppContext).clearMemory();
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.sticker_panel_plus_main;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ViewGroup vg = findViewByItemNumber(firstVisibleItem);
        if (vg != null){
            notifyTabViewSelect(vg);
        }
    }
    private ViewGroup findViewByItemNumber(int number){
        for (int i = 0; i < newTabView.size(); i++) {
            Object tag = newTabView.get(i).getTag();
            if(tag instanceof Integer && ((Integer) tag) == number){
                return newTabView.get(i);
            }
        }
        return null;
    }
}
