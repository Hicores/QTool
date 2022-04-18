package cc.hicore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface UIItem {
    int mainItemID();
    String itemName();
    String itemDesc() default "";
    int itemType();
    String ID();
    boolean IsDefChecked() default false;
}
