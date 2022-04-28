package cc.hicore.Utils;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedBridge;

public class MTracker {
    private static String getFramework() {
        String Tag = CollectBridgeTag();
        if (Tag.equals("BugHook")) return "应用转生";
        if (Tag.equals("LSPosed-Bridge")) return "LSPosed";
        if (Tag.equals("SandXposed")) return "天鉴";
        if (Tag.equals("PineXposed")) return "DreamLand";
        if (Tag.equals("Xposed")) {
            try {
                Class clz = XposedBridge.class.getClassLoader()
                        .loadClass("me.weishu.exposed.ExposedBridge");
                if (clz != null) return "太极";
            } catch (Exception e) {

            }
        }
        return "未知";
    }

    private static String CollectBridgeTag() {
        String BUGTag = CheckIsBugHook();
        if (BUGTag == null) {
            try {
                Field f = XposedBridge.class.getField("TAG");
                f.setAccessible(true);
                return (String) f.get(null);
            } catch (Exception e) {
                return "未知";
            }
        }
        return BUGTag;
    }

    private static String CheckIsBugHook() {
        ClassLoader BridgeLoader = XposedBridge.class.getClassLoader();
        try {
            Class clz = BridgeLoader.loadClass("com.bug.hook.xposed.HookBridge");
            Field Tag = clz.getField("TAG");
            Tag.setAccessible(true);
            return (String) Tag.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
