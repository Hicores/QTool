package cc.hicore.HookItemLoader.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPItem {
    int ITEM_Hook = 1;
    int ITEM_Api = 2;
    int itemType();

    int target() default -1;
    boolean isStrict() default false;

    int PROC_MAIN = 1;
    int PROC_ALL = 2;
    int proc() default PROC_MAIN;

    String name();

    int Period_Early = 1;
    int Period_InitData = 2;
    int period() default Period_Early;

}
