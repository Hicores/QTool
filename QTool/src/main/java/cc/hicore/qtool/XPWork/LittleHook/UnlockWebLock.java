package cc.hicore.qtool.XPWork.LittleHook;

import android.content.Context;

import java.lang.reflect.Method;
import java.net.URLDecoder;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQCleaner.QQCleanerHook.HideQzoneAd;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "解除风险网址拦截",groupName = "功能辅助",targetID = 1,type = 1,id = "UnlockWebLock")
public class UnlockWebLock extends BaseHookItem implements BaseUiItem {
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            if (isEnable()){
                String loadUrl = (String) param.args[0];
                if(loadUrl.startsWith("https://c.pc.qq.com/middlem.html?") || loadUrl.startsWith("https://c.pc.qq.com/index.html?")) {
                    String RedictUrl = GetStringMiddle(loadUrl,"url=","&");
                    if(RedictUrl!=null) {
                        String SourceUrl = URLDecoder.decode(RedictUrl);
                        param.args[0] = SourceUrl;
                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return  HookEnv.Config.getBoolean("Set","UnlockWebLock",false);
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        HookEnv.Config.setBoolean("Set","UnlockWebLock",IsCheck);
        HookLoader.CallHookStart(UnlockWebLock.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.smtt.sdk.WebView"), "loadUrl",void.class,new Class[]{
                String.class
        });
    }
    private static String GetStringMiddle(String str,String before,String after) {
        int index1 = str.indexOf(before);
        if(index1==-1)return null;
        int index2 = str.indexOf(after,index1+before.length());
        if(index2==-1)return null;
        return str.substring(index1+before.length(),index2);
    }
}
