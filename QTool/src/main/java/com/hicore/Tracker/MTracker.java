package com.hicore.Tracker;

import android.os.Build;

import com.hicore.Utils.DataUtils;
import com.hicore.Utils.HttpUtils;
import com.hicore.qtool.QQManager.QQEnvUtils;
import com.hicore.qtool.XposedInit.HostInfo;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import de.robv.android.xposed.XposedBridge;

public class MTracker {
    public static void Track_Load(){
        try{
            JSONObject TrackJson = new JSONObject();
            TrackJson.put("Uin", QQEnvUtils.getCurrentUin());
            TrackJson.put("QQ_Version", HostInfo.getVersion());
            TrackJson.put("Framework",getFramework());
            TrackJson.put("OS_Ver",String.valueOf(Build.VERSION.RELEASE));
            HttpUtils.getContent("https://qtool.haonb.cc/track?data="+ DataUtils.ByteArrayToHex(TrackJson.toString().getBytes(StandardCharsets.UTF_8)));
        }catch (Exception e){}

    }
    private static String getFramework(){
        String Tag = CollectBridgeTag();
        if (Tag.equals("BugHook"))return "应用转生";
        if (Tag.equals("LSPosed-Bridge"))return "LSPosed";
        if (Tag.equals("SandXposed"))return "天鉴";
        if (Tag.equals("PineXposed"))return "DreamLand";
        if (Tag.equals("Xposed")){
            try{
                Class clz = XposedBridge.class.getClassLoader()
                        .loadClass("me.weishu.exposed.ExposedBridge");
                if (clz != null)return "太极";
            }catch (Exception e){

            }
        }
        return "未知";
    }
    private static String CollectBridgeTag(){
        String BUGTag = CheckIsBugHook();
        if (BUGTag == null){
            try{
                Field f = XposedBridge.class.getField("TAG");
                f.setAccessible(true);
                return (String) f.get(null);
            }catch (Exception e){
                return "未知";
            }
        }
        return BUGTag;
    }
    private static String CheckIsBugHook(){
        ClassLoader BridgeLoader = XposedBridge.class.getClassLoader();
        try{
            Class clz = BridgeLoader.loadClass("com.bug.hook.xposed.HookBridge");
            Field Tag = clz.getField("TAG");
            Tag.setAccessible(true);
            return (String) Tag.get(null);
        }catch (Exception e){
            return null;
        }
    }
}
