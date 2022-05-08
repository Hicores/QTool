package cc.hicore.qtool.QQCleaner.QQCleanerHook;

import android.content.Context;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@UIItem(name = "屏蔽空间热播广告",groupName = "其他净化",targetID = 2,type = 1,id = "HideQzoneAd")
@HookItem(isDelayInit = false,isRunInAllProc = true)
public class HideQzoneAd extends BaseHookItem implements BaseUiItem {
    static AtomicBoolean IsLoad = new AtomicBoolean();
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0],param -> {
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
                                if(HookEnv.Config.getBoolean("Set","HideQzoneAd",false)){
                                    if((int) MField.GetField(param.args[2],"isAdFeeds")==1)param.setResult(null);
                                    Object Child = MField.GetField(param.args[2],"cellOperationInfo");

                                    Map<Integer,String> map = MField.GetField(Child,"busiParam");
                                    if(map.containsKey(194))param.setResult(null);
                                }
                            }
                        }
                );
            }
        });
        if (m[1]!= null){
            XPBridge.HookBefore(m[1],param -> {
                if(HookEnv.Config.getBoolean("Set","HideQzoneAd",false)){
                    if((int) MField.GetField(param.args[2],"isAdFeeds")==1)param.setResult(null);
                    Object Child = MField.GetField(param.args[2],"cellOperationInfo");

                    Map<Integer,String> map = MField.GetField(Child,"busiParam");
                    if(map.containsKey(194))param.setResult(null);

                    /*
                    for (int ss : map.keySet()){
                        XposedBridge.log(ss  + "->" + map.get(ss));
                    }

                     */
                }
            });
        }
        /*

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.qzone.module.feedcomponent.FeedcomponentModule$1"), "handleDetailCommentOnIdle", ViewGroup.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                ViewGroup group = (ViewGroup) param.args[0];
                XposedBridge.log(group+"->"+group.getChildCount());
                for (int i=0;i<group.getChildCount();i++){
                    XposedBridge.log(group.getChildAt(i)+"->"+group.getChildAt(i).getTag());
                }
            }
        });

         */
        return true;
    }

    @Override
    public boolean isEnable() {
        return HookEnv.Config.getBoolean("Set","HideQzoneAd",false);
    }

    @Override
    public boolean check() {
        return getMethod()[0] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        HookEnv.Config.setBoolean("Set","HideQzoneAd",IsCheck);
        HookLoader.CallHookStart(HideQzoneAd.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.pluginsdk.PluginStatic"),"getOrCreateClassLoaderByPath",ClassLoader.class,new Class[]{
                Context.class, String.class, String.class, boolean.class
        });
        m[1] = MMethod.FindMethod(MClass.loadClass("com.qzone.module.feedcomponent.ui.FeedViewBuilder"),"setFeedViewData",void.class,new Class[]{
                Context.class,
                MClass.loadClass("com.qzone.proxy.feedcomponent.ui.AbsFeedView"),
                MClass.loadClass("com.qzone.proxy.feedcomponent.model.BusinessFeedData"),
                boolean.class,boolean.class,
        });
        return m;
    }
}
