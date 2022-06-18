package cc.hicore.HookItemLoader.Annotations;

public @interface UIClick {
    int target() default -1;
    boolean isStrict() default false;
}
