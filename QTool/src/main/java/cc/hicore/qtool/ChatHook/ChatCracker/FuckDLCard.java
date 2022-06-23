package cc.hicore.qtool.ChatHook.ChatCracker;

import android.content.Context;

import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.HttpUtils;
import cc.hicore.Utils.Utils;

public class FuckDLCard{
    private static long mStartTime=0;
    private static HashMap<String,Long> mCache = new HashMap<>();

    @UIItem
    @VerController
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽部分耗流量卡片";
        ui.groupName = "聊天净化";
        ui.targetID = 2;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod("com.tencent.mobileqq.transfile.HttpDownloader","downloadImage", File.class,new Class[]{
                OutputStream.class,
                MClass.loadClass("com.tencent.image.DownloadParams"),
                MClass.loadClass("com.tencent.image.URLDrawableHandler"),
                int.class,
                URL.class
        }));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor xpWorker(){
        return param -> {
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
        };
    }
}
