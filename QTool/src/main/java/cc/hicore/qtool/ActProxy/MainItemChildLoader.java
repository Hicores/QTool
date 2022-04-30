package cc.hicore.qtool.ActProxy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XposedBridge;

public class MainItemChildLoader {
    public static void startLoad(int i, Activity act){
        BaseProxyAct.createNewView("ChildItem" + i, act, context -> createView(i,act));
    }
    private static View createView(int i,Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        LinkedHashSet<HookLoader.UiInfo> infos = HookLoader.getUiInfos();
        HashMap<String, ArrayList<HookLoader.UiInfo>> sortsUIItems = new HashMap<>();
        for (HookLoader.UiInfo info : infos){
            if (info.targetID == i){
                info.UIInstance = HookLoader.searchForUiInstance(info.clzName);
                if (info.UIInstance != null){
                    ArrayList<HookLoader.UiInfo> groupItems = sortsUIItems.computeIfAbsent(info.groupName, s -> new ArrayList<>());
                    groupItems.add(info);
                }
            }
        }
        ScrollView scrollView = new ScrollView(context);
        LinearLayout rootContainer = new LinearLayout(context);
        scrollView.addView(rootContainer);
        rootContainer.setOrientation(LinearLayout.VERTICAL);

        View block = new View(context);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(1,Utils.dip2px(context,16));
        rootContainer.addView(block,mp);

        for (String groupName : sortsUIItems.keySet()){
            LinearLayout mContainer = (LinearLayout) inflater.inflate(R.layout.child_item_container,null);
            TextView groupNameView = mContainer.findViewById(R.id.Group_Name);
            groupNameView.setText(groupName);

            LinearLayout ItemContainer = mContainer.findViewById(R.id.ItemContainer);
            for (HookLoader.UiInfo info : sortsUIItems.get(groupName)){
                RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.child_item,null);
                TextView item_title = layout.findViewById(R.id.Item_Title);
                item_title.setText(info.name);
                if (info.desc.isEmpty()){
                    layout.findViewById(R.id.Item_SubTitle).setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item_title.getLayoutParams();
                    params.bottomMargin = Utils.dip2px(context,11);
                    item_title.setLayoutParams(params);
                }else {
                    TextView item_desc = layout.findViewById(R.id.Item_SubTitle);
                    item_desc.setText(info.desc);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) item_desc.getLayoutParams();
                    params.bottomMargin = Utils.dip2px(context,11);
                    item_desc.setLayoutParams(params);
                }

                if (info.type == 1){
                    Switch mSwitch = layout.findViewById(R.id.Item_Switch);
                    mSwitch.setVisibility(View.VISIBLE);
                    mSwitch.setChecked(HookEnv.Config.getBoolean("Main_Switch", info.id, info.defCheck));
                    mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        HookEnv.Config.setBoolean("Main_Switch", info.id, isChecked);
                        info.UIInstance.SwitchChange(isChecked);
                    });
                }else {
                    layout.findViewById(R.id.Item_Click).setVisibility(View.VISIBLE);
                    layout.setOnClickListener(v-> info.UIInstance.ListItemClick());
                }
                ItemContainer.addView(layout);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = Utils.dip2px(context,12);
            rootContainer.addView(mContainer,params);
        }

        block = new View(context);
        mp = new LinearLayout.LayoutParams(1,Utils.dip2px(context,24));
        rootContainer.addView(block,mp);
        return scrollView;
    }
}
