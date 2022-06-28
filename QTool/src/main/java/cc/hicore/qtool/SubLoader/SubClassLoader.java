package cc.hicore.qtool.SubLoader;

import dalvik.system.PathClassLoader;

public class SubClassLoader extends PathClassLoader {

    public SubClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
        super(dexPath, librarySearchPath, parent);
    }
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try{
            Class<?> clz = findClass(name);
            if (clz != null)return clz;
        }catch (Exception ignored){
            return super.loadClass(name,resolve);
        }
        throw new ClassNotFoundException(name+" not found.");
    }

}
