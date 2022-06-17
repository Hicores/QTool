package cc.hicore.HookItemLoader.bridge;

public class BaseFindMethodInfo extends BaseMethodInfo{
    public String LinkedToMethodID;
    public String MethodID;

    public BaseMethodInfo.MethodChecker checker;

    public Class<?> InClass;
    public int paramCount;
    public BaseMethodInfo[] paramTypes;
    public BaseMethodInfo returnType;
}
