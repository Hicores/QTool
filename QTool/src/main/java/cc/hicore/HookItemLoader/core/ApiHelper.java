package cc.hicore.HookItemLoader.core;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ApiHelper {
    protected static HashMap<Class<?>,Method> ApiInvoker = new HashMap<>();
    public static <Any> Any invoke(Class<?> ApiClass,Object... AnyParam){
        return null;
    }
}
