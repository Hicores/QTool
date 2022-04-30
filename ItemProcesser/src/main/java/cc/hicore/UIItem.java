package cc.hicore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface UIItem {
    int targetID();
    String groupName();

    String name();
    String desc() default "";
    int type();
    String id();

    boolean defCheck() default false;
}
