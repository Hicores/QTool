package cc.hicore.HookItemLoader.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPExecutor {
    int Before = 1;
    int After = 1 << 1;

    int index() default 0;
    int period() default Before;

    int target() default -1;
    boolean isStrict() default false;
}
