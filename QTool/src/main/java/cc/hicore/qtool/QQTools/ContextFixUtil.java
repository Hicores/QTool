package cc.hicore.qtool.QQTools;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;

import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XposedBridge;

public class ContextFixUtil {
    public static class FixResClassLoader extends ClassLoader {
        protected FixResClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                if (!name.startsWith("com.airbnb.lottie")){
                    Class<?> clz = super.loadClass(name);
                    if (clz != null) return clz;
                }
            } catch (Throwable e) {

            }
            return findClass(name);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                if (!name.startsWith("com.airbnb.lottie")){
                    Class<?> clz = super.loadClass(name, resolve);
                    if (clz != null) return clz;
                }
            } catch (Throwable e) {

            }
            return findClass(name);

        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return HookEnv.moduleLoader.loadClass(name);
        }
    }

    public static class FixContext extends ContextWrapper {
        private ClassLoader mFixLoader;

        @Override
        public ClassLoader getClassLoader() {
            if (mFixLoader != null) return mFixLoader;
            return super.getClassLoader();
        }

        public FixContext(Context base) {
            super(base);
            mFixLoader = new FixResClassLoader(base.getClassLoader());
        }
    }

    //获得注入了ClassLoader的Context,使其能加载模块中的类
    public static Context getFixContext(Context context) {
        return new FixContext(context);
    }

    //获得注入了ClassLoader的LayoutInflater,使其能加载模块中的类界面
    public static LayoutInflater getContextInflater(Context context) {
        return LayoutInflater.from(context).cloneInContext(getFixContext(context));
    }
}
