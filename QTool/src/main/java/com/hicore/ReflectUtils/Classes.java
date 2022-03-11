package com.hicore.ReflectUtils;

public final class Classes {
    public static Class MessageRecord(){
        return MClass.loadClass("com.tencent.mobileqq.data.MessageRecord");
    }
    public static Class AppInterface(){
        return MClass.loadClass("com.tencent.common.app.AppInterface");
    }
}
