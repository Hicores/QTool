package cc.hicore.qtool.ChatHook.ChatCracker;

import android.content.Context;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isRunInAllProc = false,isDelayInit = false)
@UIItem(name = "屏蔽部分耗流量卡片",groupName = "聊天净化",id = "FuckDLCard",type = 1,targetID = 2)
public class FuckDLCard extends BaseHookItem implements BaseUiItem {
    private static long mStartTime=0;
    private static HashMap<String,Long> mCache = new HashMap<>();
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            if (IsEnable){
                Object paramObj = param.args[1];
                String mUrl = MField.GetField(paramObj,"urlStr",String.class);
                long Size =0;
                if(!mCache.containsKey(mUrl)) {
                    Size = HttpUtils.GetFileLength(mUrl);
                } else {
                    Long mLong = mCache.get(mUrl);
                    Size = mLong.longValue();
                }

                //如果文件大于100M就直接停止加载并,并且缓存地址,防止重复获取大小信息消耗流量
                if(Size>100*1024*1024 || mCache.containsKey(mUrl)) {
                    if(System.currentTimeMillis()>mStartTime+60*1000) {
                        Utils.ShowToast("当前下载的图片可能过大,已停止加载,图片大小:"+ Utils.bytes2kb(Size));
                        mStartTime = System.currentTimeMillis();
                        mCache.put(mUrl,Size);
                    }
                    param.setResult(null);
                }
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(FuckDLCard.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method getMethod(){
        return MMethod.FindMethod("com.tencent.mobileqq.transfile.HttpDownloader","downloadImage", File.class,new Class[]{
                OutputStream.class,
                MClass.loadClass("com.tencent.image.DownloadParams"),
                MClass.loadClass("com.tencent.image.URLDrawableHandler"),
                int.class,
                URL.class
        });
    }
}
