package cc.hicore.DexFinder;

public class DexFinderNative {
    public static native long InitPath(String apkPath);

    public static native String[] findMethodUsingString(long dexkitInstance,String str);

    public static native String[] findMethodInvoked(long dexkitInstance,String invokeMethodDesc);
}
