package com.hicore.qtool.JavaPlugin.Controller;

import android.graphics.Color;
import android.text.TextUtils;

import com.hicore.HookItem;
import com.hicore.Utils.FileUtils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.ListForm.LocalPluginItemController;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

@HookItem(isDelayInit = true,isRunInAllProc = false)
public class AutoLoad extends BaseHookItem {

    @Override
    public boolean startHook() throws Throwable {
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<String> AutoLoadIDs = PluginSetController.getAutoLoadList();
            for (String Id : AutoLoadIDs){
                PluginInfo Path = searchPathByID(Id);
                if (Path == null){
                    PluginSetController.SetAutoLoad(Id,false);
                }else {
                    PluginController.LoadOnce(Path);
                }
            }
        }).start();
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return false;
    }
    private PluginInfo searchPathByID(String ID){
        File[] searchResult = new File(HookEnv.ExtraDataPath+"/Plugin").listFiles();
        if (searchResult != null){
            for (File f :searchResult){
                try{
                    if (f.exists() && f.isDirectory()){
                        File propPath = new File(f,"info.prop");
                        if (propPath.exists()){
                            Properties props = new Properties();
                            String propString = FileUtils.ReadFileString(propPath.getAbsolutePath());
                            props.load(new StringReader(propString));
                            String PluginID = props.getProperty("id", f.getName());
                            if (PluginID.equals(ID)){
                                PluginInfo NewInfo = new PluginInfo();
                                NewInfo.LocalPath = f.getAbsolutePath();
                                NewInfo.PluginID = PluginID;
                                NewInfo.PluginName = props.getProperty("name","未设定名字");
                                NewInfo.PluginAuthor = props.getProperty("author","未设定作者");
                                NewInfo.PluginVersion = props.getProperty("version","未设定版本号");
                                NewInfo.IsRunning = PluginController.IsRunning(PluginID);
                                NewInfo.IsLoading = PluginController.IsLoading(PluginID);
                                return NewInfo;
                            }
                        }
                    }
                }catch (Exception e){

                }
            }
        }
        return null;
    }
}

