package com.hicore.qtool.JavaPlugin.ListForm;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hicore.LogUtils.LogUtils;
import com.hicore.Utils.FileUtils;
import com.hicore.Utils.NameUtils;
import com.hicore.qtool.JavaPlugin.Controller.PluginSetController;
import com.hicore.qtool.R;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class LocalPluginItemController {
    private static final String TAG = "LocalPluginController";
    private RelativeLayout mRoot;
    private String PluginPath;
    public static LocalPluginItemController create(Context context){
        LocalPluginItemController controller = new LocalPluginItemController(context);
        return controller;
    }
    private LocalPluginItemController(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (RelativeLayout) inflater.inflate(R.layout.plugin_item_local,null);
    }
    public boolean checkAndLoadPluginInfo(String PluginRootPath){
        File propPath = new File(PluginRootPath,"info.prop");
        if (!propPath.exists())return false;
        try {
            Properties props = new Properties();
            String propString = FileUtils.ReadFileString(propPath.getAbsolutePath());
            props.load(new StringReader(propString));
            setTitle(props.getProperty("name","未设定名字"), Color.BLACK);
            setAuthor("作者:"+props.getProperty("author","未设定作者"));
            setDesc(FileUtils.ReadFileString(new File(PluginRootPath,"desc.txt")));
            String PluginID = props.getProperty("id", NameUtils.GetRandomName());
            setBlackOrWhileMode(PluginSetController.IsBlackMode(PluginID));


            return true;

        } catch (IOException e) {
            LogUtils.warning(TAG,"Can't decode plugin prop file:"+propPath.getAbsolutePath());
            return false;
        }
    }
    public RelativeLayout getRoot(){
        return mRoot;
    }
    public void setTitleColor(int color){
        TextView titleView = mRoot.findViewById(R.id.plugin_title);
        titleView.setTextColor(color);
    }
    public void setTitle(String title,int color){
        TextView titleView = mRoot.findViewById(R.id.plugin_title);
        titleView.setText(title);
        titleView.setTextColor(color);
    }
    public void setVersion(String version){
        TextView versionView = mRoot.findViewById(R.id.plugin_version);
        versionView.setText(version);
    }
    public void setAuthor(String author){
        TextView authorView = mRoot.findViewById(R.id.plugin_author);
        authorView.setText(author);
    }
    public void setDesc(String desc){
        TextView descView = mRoot.findViewById(R.id.plugin_desc);
        descView.setText(desc);
    }
    public void setAutoLoad(boolean isEnable){
        CheckBox authLoad = mRoot.findViewById(R.id.plugin_autoload);
        authLoad.setChecked(isEnable);
    }
    public void setBlackOrWhileMode(boolean blackMode){
        if (blackMode){
            RadioButton button = mRoot.findViewById(R.id.plugin_message_black);
            button.setChecked(true);
        }else {
            RadioButton button = mRoot.findViewById(R.id.plugin_message_while);
            button.setChecked(true);
        }
    }
}
