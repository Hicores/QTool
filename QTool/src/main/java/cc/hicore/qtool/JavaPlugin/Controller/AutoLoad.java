package cc.hicore.qtool.JavaPlugin.Controller;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.Utils.FileUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@XPItem(name = "脚本自动加载",itemType = XPItem.ITEM_Hook,period = XPItem.Period_InitData)
public class AutoLoad{
    @VerController
    @CommonExecutor
    public void work(){
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<String> AutoLoadIDs = PluginSetController.getAutoLoadList();
            for (String Id : AutoLoadIDs) {
                PluginInfo Path = searchPathByID(Id);
                if (Path == null) {
                    //如果插件不存在则从自动加载列表中移除
                    PluginSetController.SetAutoLoad(Id, false);
                } else {
                    PluginController.LoadOnce(Path);
                }
            }
        }).start();
    }
    //通过插件ID在文件中扫描插件文件目录
    private PluginInfo searchPathByID(String ID) {
        File[] searchResult = new File(HookEnv.ExtraDataPath + "/Plugin").listFiles();
        if (searchResult != null) {
            for (File f : searchResult) {
                try {
                    if (f.exists() && f.isDirectory()) {
                        File propPath = new File(f, "info.prop");
                        if (propPath.exists()) {
                            Properties props = new Properties();
                            String propString = FileUtils.ReadFileString(propPath.getAbsolutePath());
                            props.load(new StringReader(propString));
                            String PluginID = props.getProperty("id", f.getName());
                            if (PluginID.equals(ID)) {
                                PluginInfo NewInfo = new PluginInfo();
                                NewInfo.LocalPath = f.getAbsolutePath();
                                NewInfo.PluginID = PluginID;
                                NewInfo.PluginName = props.getProperty("name", "未设定名字");
                                NewInfo.PluginAuthor = props.getProperty("author", "未设定作者");
                                NewInfo.PluginVersion = props.getProperty("version", "未设定版本号");
                                NewInfo.IsRunning = PluginController.IsRunning(PluginID);
                                NewInfo.IsLoading = PluginController.IsLoading(PluginID);
                                return NewInfo;
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return null;
    }
}

