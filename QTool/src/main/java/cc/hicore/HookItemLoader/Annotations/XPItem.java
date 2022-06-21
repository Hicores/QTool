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

    int targetVer() default -1;
    int max_targetVer() default -1;

    int PROC_MAIN = 1;
    int PROC_ALL = 2;
    int proc() default PROC_MAIN;

    String name();

    int Period_Early = 1;
    int Period_InitData = 2;
    int period() default Period_Early;

    int Target_App_QQ = 1;
    int Target_App_Tim = 1 << 1;
    int targetApp() default Target_App_QQ;

}
