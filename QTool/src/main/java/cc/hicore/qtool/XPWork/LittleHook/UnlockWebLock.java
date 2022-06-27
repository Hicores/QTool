package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;

import java.lang.reflect.Method;
import java.net.URLDecoder;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
@XPItem(name = "解除风险网址拦截",itemType = XPItem.ITEM_Hook,proc = XPItem.PROC_ALL)
public class UnlockWebLock{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "解除风险网址拦截";
        ui.groupName = "功能辅助";
        ui.targetID = 1;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.smtt.sdk.WebView"), "loadUrl",void.class,new Class[]{
                String.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> {
            String loadUrl = (String) param.args[0];
            if(loadUrl.startsWith("https://c.pc.qq.com/middlem.html?") || loadUrl.startsWith("https://c.pc.qq.com/index.html?")) {
                String RedictUrl = GetStringMiddle(loadUrl,"url=","&");
                if(RedictUrl!=null) {
                    String SourceUrl = URLDecoder.decode(RedictUrl);
                    param.args[0] = SourceUrl;
                }
            }
        };
    }
    private static String GetStringMiddle(String str,String before,String after) {
        int index1 = str.indexOf(before);
        if(index1==-1)return null;
        int index2 = str.indexOf(after,index1+before.length());
        if(index2==-1)return null;
        return str.substring(index1+before.length(),index2);
    }
}
