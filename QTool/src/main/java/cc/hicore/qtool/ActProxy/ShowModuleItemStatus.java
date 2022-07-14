package cc.hicore.qtool.ActProxy;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.core.CoreLoader;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.CrashHandler.CatchInstance;
import cc.hicore.qtool.R;

@SuppressLint("ResourceType")
public class ShowModuleItemStatus {
    public static void onShow(Context context){
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_checker_main,null);
        LinearLayout ll = vg.findViewById(R.id.Item_Show_View);
        LayoutInflater inflater = LayoutInflater.from(context);
        for (CoreLoader.XPItemInfo info : CoreLoader.allInstance.values()){
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.item_checker_hook_item,null);

            TextView title = group.findViewById(R.id.Item_Checker_Title);
            title.setTextColor(checkAvailable(info));
            title.setText(info.ItemName);

            TextView available = group.findViewById(R.id.Item_Checker_Available);
            available.setText(checkIsAvailable(info));

            TextView status = group.findViewById(R.id.Item_Checker_Status);
            status.setText(info.isEnabled ? "状态:开启" : "状态:关闭");

            TextView type = group.findViewById(R.id.Item_Checker_Type);
            type.setText(info.isApi ? "类型:Api" : "类型:Hook");

            group.setOnClickListener(v-> showItemDetail(context,info));
            ll.addView(group);
        }


        Dialog dialog = new Dialog(context,3);
        dialog.setContentView(vg);
        dialog.show();
    }
    private static String checkIsAvailable(CoreLoader.XPItemInfo info){
        if(!info.isVersionAvailable)return "功能:版本不匹配";
        if(!info.cacheException.isEmpty())return "功能:存在加载异常";
        if (!info.ExecutorException.isEmpty())return "功能:存在执行异常";
        for (String s : info.scanResult.keySet()){
            Member m  = info.scanResult.get(s);
            if (m == null){
                return "功能:存在加载异常";
            }
        }
        return "功能:无异常";
    }
    private static int checkAvailable(CoreLoader.XPItemInfo info){
        if(!info.isVersionAvailable)return Color.GRAY;
        if(!info.cacheException.isEmpty())return Color.RED;
        if (!info.ExecutorException.isEmpty())return Color.RED;
        for (String s : info.scanResult.keySet()){
            Member m  = info.scanResult.get(s);
            if (m == null){
                return Color.RED;
            }
        }
        return Color.BLACK;
    }
    private static void showItemDetail(Context context,CoreLoader.XPItemInfo info){
        StringBuilder detail = new StringBuilder();
        detail.append(CatchInstance.IGetAvailableInfo(Thread.currentThread())).append("\n");

        detail.append("项目名称:").append(info.ItemName).append("\n");
        detail.append("项目类:").append(info.Instance.getClass().getName()).append("\n");
        detail.append("项目目标信息").append("\n")
                .append("targetVer:").append(info.item.targetVer()).append("\n")
                .append("maxVer:").append(info.item.max_targetVer()).append("\n")
                .append("targetApp:").append(info.item.targetApp()).append("\n")
                .append("period:").append(info.item.period() == XPItem.Period_Early ? "early" : "initdata").append("\n")
                .append("type:").append(info.item.itemType() == XPItem.ITEM_Api ? "API": " Hook").append("\n\n");

        detail.append("项目启用的方法").append("\n");
        for (Method m : info.fitMethods) {
            detail.append("--------------------------\n").append(m.getName()).append("\n");
            Annotation[] annotations = m.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                detail.append(annotation.toString()).append("\n");
            }
        }
        detail.append("--------------------------\n\n");
        detail.append("项目报告的方法").append("\n");
        detail.append("--------------------------\n");
        for (String ID : info.scanResult.keySet()){
            Member m = info.scanResult.get(ID);
            detail.append(ID).append("->").append(m).append("\n");
        }
        detail.append("--------------------------\n\n");
        detail.append("执行异常列表:\n");
        detail.append("--------------------------\n");
        for (String s : info.ExecutorException.keySet()){
            String value = info.ExecutorException.get(s);
            detail.append(s).append("\n").append(value).append("\n\n");
        }
        detail.append("--------------------------\n\n");
        detail.append("加载异常列表:\n");
        detail.append("--------------------------\n");
        for (String s : info.cacheException){
            detail.append(s).append("\n\n");
        }
        detail.append("--------------------------\n\n");
        ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_checker_show_detail,null);
        EditText ed = vg.findViewById(R.id.EditText_EditTextView);
        ed.setText(detail.toString());
        ed.setKeyListener(null);

        vg.findViewById(R.id.EditText_Button_Save_All).setOnClickListener(v->{
            Utils.SetTextClipboard(detail.toString());
            Utils.ShowToastL("已复制");
        });
        Dialog dialog = new Dialog(context,3);
        dialog.setContentView(vg);
        dialog.show();


    }
}
