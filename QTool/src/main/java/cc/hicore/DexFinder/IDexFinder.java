package cc.hicore.DexFinder;

import java.lang.reflect.Method;

public interface IDexFinder {
    void init(String apkPath,ClassLoader loader);
    Method[] findMethodByString(String str);
    Method[] findMethodBeInvoked(Method beInvoked);
    Method[] findMethodInvoking(Method beInvoked);
}
