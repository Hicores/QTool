package cc.hicore.HookItemLoader.bridge;

import java.lang.reflect.Member;

public class BaseMethodInfo {
    public static final int TYPE_METHOD = 1;
    public static final int TYPE_FINDER_INFO = 2;
    int type;
    public interface MethodChecker{
        boolean onMethod(Member member);
    }
}
