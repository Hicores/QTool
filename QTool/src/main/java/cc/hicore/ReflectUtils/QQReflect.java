package cc.hicore.ReflectUtils;

import android.view.View;

import java.lang.reflect.Method;

public class QQReflect {
    public static Method GetItemBuilderMenuBuilder(Class clz, String MethodName) {
        for (Method med : clz.getDeclaredMethods()) {
                if (med.getParameterTypes().length == 1) {
                    if (med.getParameterTypes()[0] == View.class) {
                        Class ReturnClz = med.getReturnType();
                        if (ReturnClz.isArray() && ReturnClz.getComponentType()== MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem")) {
                            return med;
                        }
                    }
                }
        }
        return null;
    }
}
