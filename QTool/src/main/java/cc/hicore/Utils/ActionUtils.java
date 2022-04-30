package cc.hicore.Utils;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;

public class ActionUtils {
    private static HashMap<String,onAction> cacheAction = new HashMap<>();
    public interface onAction{
        void onAction(Activity activity);
    }
    public static void startAction(String ActionName,Activity activity){
        onAction action = cacheAction.get(ActionName);
        action.onAction(activity);
    }
    public static void registerAction(String ActionName,onAction action){
        cacheAction.put(ActionName,action);
    }
}
