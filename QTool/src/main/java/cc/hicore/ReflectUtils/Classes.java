package cc.hicore.ReflectUtils;

public final class Classes {
    public static Class MessageRecord() {
        return MClass.loadClass("com.tencent.mobileqq.data.MessageRecord");
    }

    public static Class AppInterface() {
        return MClass.loadClass("com.tencent.common.app.AppInterface");
    }

    public static Class QQAppinterFace() {
        return MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface");
    }

    public static Class SessionInfo() {
        return MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo");
    }
}
