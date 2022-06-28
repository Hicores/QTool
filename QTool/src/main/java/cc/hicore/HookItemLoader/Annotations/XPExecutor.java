package cc.hicore.HookItemLoader.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.robv.android.xposed.XC_MethodHook;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPExecutor {
    int Before = 1;
    int After = 1 << 1;

    String methodID();
    int period() default Before;
    int hook_period() default XC_MethodHook.PRIORITY_DEFAULT;
}
