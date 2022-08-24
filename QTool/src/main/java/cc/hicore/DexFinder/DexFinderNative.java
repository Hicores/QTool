package cc.hicore.DexFinder;

public class DexFinderNative {
    public static native void InitPath(String apkPath);

    public static native String[] findMethodUsingString(String apkPath,String str);

    public static native String[] findMethodInvoked(String apkPath,String invokeMethodDesc);
}
