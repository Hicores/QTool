package cc.hicore.qtool.ActProxy;

import android.content.Intent;
import android.net.Uri;

import cc.hicore.Utils.ActionUtils;
import cc.hicore.Utils.DebugUtils;
import cc.hicore.qtool.JavaPlugin.ListForm.JavaPluginAct;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.DebugDialog;

public class MainMenuActionRegister {
    static {
        ActionUtils.registerAction("OpenJavaPlugin", JavaPluginAct::startActivity);
        MainMenu.addItemData(0,"Java脚本",0,"OpenJavaPlugin", R.drawable.menu_icon_plugin);

        ActionUtils.registerAction("QQHelper", v ->MainItemChildLoader.startLoad(1,v));
        MainMenu.addItemData(1,"QQ辅助功能",0,"QQHelper", R.drawable.main_helper);

        ActionUtils.registerAction("QQCleaner", v ->MainItemChildLoader.startLoad(2,v));
        MainMenu.addItemData(2,"QQ净化功能",0,"QQCleaner", R.drawable.main_cleaner);

        ActionUtils.registerAction("Enjoy", v ->MainItemChildLoader.startLoad(3,v));
        MainMenu.addItemData(3,"娱乐功能",0,"Enjoy", R.drawable.enjoy);


        ActionUtils.registerAction("DebugUtils", DebugDialog::startShow);
        MainMenu.addItemData(10,"调试功能",0,"DebugUtils",R.drawable.debug);


        ActionUtils.registerAction("AddGroup", context->{
            Uri u = Uri.parse("https://t.me/QToolC");
            Intent in = new Intent(Intent.ACTION_VIEW,u);
            context.startActivity(in);
        });
        MainMenu.addItemData(20,"加入TG群组",0,"AddGroup",R.drawable.add_group);

        ActionUtils.registerAction("DownloadNewVer", v->{
            Uri u = Uri.parse("https://github.com/Hicores/QTool");
            Intent in = new Intent(Intent.ACTION_VIEW, u);
            v.startActivity(in);
        });
        MainMenu.addItemData(31,"CI版本下载",0,"DownloadNewVer", R.drawable.main_down);
    }
}
