package cc.hicore.qtool.SubLoader;

import cc.hicore.ReflectUtils.MClass;

public class SubBridgeClassLoader extends ClassLoader {
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> clz = MClass.loadClass(name);
        if (clz != null) return clz;
        return super.loadClass(name, resolve);
    }
}
