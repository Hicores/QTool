package cc.hicore.HookItemLoader.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class CoreLoader {
    private static final HashMap<Class<?>,XPItemInfo> clzInstance = new HashMap<>();
    private static class XPItemInfo{
        HashMap<String,String> ExecutorException = new HashMap<>();
        boolean isVersionAvailable;
        Object Instance;
        boolean ScannerSuccess;
    }
    static {
        ClassLoader loader = CoreLoader.class.getClassLoader();
        try {
            Class<?> clzItemInfo = loader.loadClass("cc.hicore.HookItemLoader.bridge.XPItems");
            Field f = clzItemInfo.getField("XPItems");
            ArrayList<String> ItemInfo = (ArrayList<String>) f.get(null);
            for (String clzName : ItemInfo){
                try {
                    Class<?> ItemClz = loader.loadClass(clzName);
                    Object newInstance = ItemClz.newInstance();
                    XPItemInfo newInfo = new XPItemInfo();
                    newInfo.Instance = newInstance;
                    clzInstance.put(ItemClz,newInfo);
                }catch (Exception e){

                }
            }
        } catch (Exception e) {

        }

    }
    public static void onBeforeLoad(){
        //
    }
    public static void onAfterLoad(){

    }
}
