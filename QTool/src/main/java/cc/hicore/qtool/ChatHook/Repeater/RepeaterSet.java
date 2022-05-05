package cc.hicore.qtool.ChatHook.Repeater;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

public class RepeaterSet {
    public static void startShow(Context context){

        LinearLayout ll = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.repeater_set_dialog,null);
        CheckBox allSwitch = ll.findViewById(R.id.All_Switch);
        allSwitch.setChecked(HookEnv.Config.getBoolean("Repeater","Open",false));

        EditText edSize = ll.findViewById(R.id.Repeater_Size_Input);
        edSize.setText(""+HookEnv.Config.getInt("Repeater","Size",32));

        TextView tipText = ll.findViewById(R.id.Tip);
        tipText.setText("图标文件请存放在"+ HookEnv.ExtraDataPath + "res/repeat.png,如果不存在或者无法访问将会使用默认图标");

        CheckBox doubleClick = ll.findViewById(R.id.Double_Click_Mode);
        doubleClick.setChecked(HookEnv.Config.getBoolean("Repeater","DoubleClickMode",false));


        new AlertDialog.Builder(context)
                .setTitle("设置复读消息+1")
                .setView(ll)
                .setNegativeButton("确定", (dialog, which) -> {
                    HookEnv.Config.setBoolean("Repeater","Open",allSwitch.isChecked());
                    HookEnv.Config.setInt("Repeater","Size",Integer.parseInt(edSize.getText().toString()));
                    HookEnv.Config.setBoolean("Repeater","DoubleClickMode",doubleClick.isChecked());
                    HookLoader.CallHookStart(Hooker.class.getName());
                }).show();



    }
}
