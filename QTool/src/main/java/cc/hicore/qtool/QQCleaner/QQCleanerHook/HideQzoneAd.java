package cc.hicore.qtool.QQCleaner.QQCleanerHook;

import android.content.Context;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@XPItem(name = "屏蔽空间热播广告",itemType = XPItem.ITEM_Hook,proc = XPItem.PROC_ALL)
public class HideQzoneAd{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽空间热播广告";
        ui.groupName = "其他净化";
        ui.targetID = 2;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook_1",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pluginsdk.PluginStatic"),"getOrCreateClassLoaderByPath",ClassLoader.class,new Class[]{
                Context.class, String.class, String.class, boolean.class
        }));
        container.addMethod("hook_2",MMethod.FindMethod(MClass.loadClass("com.qzone.module.feedcomponent.ui.FeedViewBuilder"),"setFeedViewData",void.class,new Class[]{
                Context.class,
                MClass.loadClass("com.qzone.proxy.feedcomponent.ui.AbsFeedView"),
                MClass.loadClass("com.qzone.proxy.feedcomponent.model.BusinessFeedData"),
                boolean.class,boolean.class,
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook_1",period = XPExecutor.After)
    public BaseXPExecutor worker_1(){
        return param -> {
            String sName = (String) param.args[1];
            if(sName.equals("qzone_plugin.apk") && !IsLoad.getAndSet(true)){
                ClassLoader pluginLoader = (ClassLoader) param.getResult();
                XposedHelpers.findAndHookMethod("com.qzone.module.feedcomponent.ui.FeedViewBuilder", pluginLoader,
                        "setFeedViewData",
                        Context.class,
                        XposedHelpers.findClass("com.qzone.proxy.feedcomponent.ui.AbsFeedView",pluginLoader),
                        XposedHelpers.findClass("com.qzone.proxy.feedcomponent.model.BusinessFeedData",pluginLoader),
                        boolean.class,boolean.class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                if((int) MField.GetField(param.args[2],"isAdFeeds")==1)param.setResult(null);
                                Object Child = MField.GetField(param.args[2],"cellOperationInfo");

                                Map<Integer,String> map = MField.GetField(Child,"busiParam");
                                if(map.containsKey(194))param.setResult(null);
                            }
                        }
                );
            }
        };
    }
    @VerController(targetVer = QQVersion.QQ_8_8_38)
    @XPExecutor(methodID = "hook_2")
    public BaseXPExecutor worker_2(){
        return param -> {
            if((int) MField.GetField(param.args[2],"isAdFeeds")==1)param.setResult(null);
            Object Child = MField.GetField(param.args[2],"cellOperationInfo");

            Map<Integer,String> map = MField.GetField(Child,"busiParam");
            if(map.containsKey(194))param.setResult(null);
        };
    }
    static AtomicBoolean IsLoad = new AtomicBoolean();
}
