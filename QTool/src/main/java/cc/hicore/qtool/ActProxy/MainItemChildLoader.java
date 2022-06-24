package cc.hicore.qtool.ActProxy;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;

public class MainItemChildLoader {
    public static void startLoad(int i, Activity act){
        BaseProxyAct.createNewView("ChildItem" + i, act, context -> createView(i,context));
    }
    public static void createAlarmView(int i, Activity act){
        BaseProxyAct.createNewView("ChildItem" + i, act, context -> {
            FrameLayout container = new FrameLayout(act);
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout alarmView = (LinearLayout) inflater.inflate(R.layout.alarm_view,null);
            Button btn = alarmView.findViewById(R.id.Confirm);
            btn.setOnClickListener(v->{
                container.removeAllViews();
                container.addView(createView(i,context));
            });

            container.addView(alarmView);
            return container;
        });

    }
    private static View createView(int i,Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        LinkedHashSet<UIInfo> infos = new LinkedHashSet<>();
        for (CoreLoader.XPItemInfo itemInfo : CoreLoader.clzInstance.values()){
            if (itemInfo.ui != null){
                infos.add(itemInfo.ui);
            }
        }

        HashMap<String, ArrayList<UIInfo>> sortsUIItems = new HashMap<>();
        for (UIInfo info : infos){
            if (info.targetID == i){
                ArrayList<UIInfo> groupItems = sortsUIItems.computeIfAbsent(info.groupName, s -> new ArrayList<>());
                groupItems.add(info);
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
            for (UIInfo info : sortsUIItems.get(groupName)){
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
                    mSwitch.setChecked(HookEnv.Config.getBoolean("Main_Switch", info.connectTo.id, false));
                    mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        HookEnv.Config.setBoolean("Main_Switch", info.connectTo.id, isChecked);
                        info.connectTo.isEnabled = isChecked;
                    });
                    if (info.connectTo.uiClick != null){
                        layout.setOnClickListener(v ->{
                            try{
                                info.connectTo.uiClick.invoke(info.connectTo.Instance,context);
                            }catch (Throwable th){
                                info.connectTo.ExecutorException.put(info.connectTo.uiClick.getName(), Log.getStackTraceString(th));
                            }
                        });
                    }
                }else {
                    layout.findViewById(R.id.Item_Click).setVisibility(View.VISIBLE);
                    layout.setOnClickListener(v-> {
                        if (info.connectTo.uiClick != null){
                            try{
                                info.connectTo.uiClick.invoke(info.connectTo.Instance,context);
                            }catch (Throwable th){
                                info.connectTo.ExecutorException.put(info.connectTo.uiClick.getName(), Log.getStackTraceString(th));
                            }
                        }

                    });
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
