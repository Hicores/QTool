package cc.hicore.HookItemLoader.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerController {
    int targetVer() default -1;
    int max_targetVer() default -1;

    int Target_App_QQ = 1;
    int Target_App_Tim = 1 << 1;
    int targetApp() default Target_App_QQ;
}
