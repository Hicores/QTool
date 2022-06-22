package cc.hicore.qtool.XposedInit.ItemLoader;

import java.util.HashMap;

public class HookLoader {

    private static final String TAG = "HookLoader";

    private static HashMap<String, BaseHookItem> cacheHookInst = new HashMap<>();
    //插到指定的HookItem类并尝试进行加载,一般用于主菜单界面从未打开到打开进行动态挂钩
    public static void CallHookStart(String ClzName) {

    }
}
