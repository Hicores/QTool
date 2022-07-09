package cc.hicore.HookItemLoader.core;

import android.util.Log;

public class ApiHelper {
    public static <Any> Any invoke(Class<?> ApiClass,Object... AnyParam){
        CoreLoader.XPItemInfo info = CoreLoader.clzInstance.get(ApiClass);
        if (info != null){
            try {
                return (Any) info.apiExecutor.invoke(info.Instance,AnyParam);
            }catch (Throwable th){
                info.ExecutorException.put(info.apiExecutor+"", Log.getStackTraceString(th));
                return null;
            }
        }else {
            throw new RuntimeException("No impl found for "+ApiClass);
        }
    }
}
