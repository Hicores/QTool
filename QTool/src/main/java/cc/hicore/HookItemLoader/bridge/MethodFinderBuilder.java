package cc.hicore.HookItemLoader.bridge;

import java.lang.reflect.Member;

public class MethodFinderBuilder {
    public static BaseMethodInfo newCommonMethod(String ID,Member member){
        CommonMethodInfo info = new CommonMethodInfo();
        info.methods = member;
        info.type = BaseMethodInfo.TYPE_METHOD;
        info.id = ID;
        return info;
    }
    public static BaseMethodInfo newFinderByString(String ID,String str, BaseMethodInfo.MethodChecker findCallback){
        FindMethodByName findMethodByName = new FindMethodByName();
        findMethodByName.type = BaseMethodInfo.TYPE_FINDER_INFO;
        findMethodByName.name = str;
        findMethodByName.id = ID;
        findMethodByName.checker = findCallback;
        return findMethodByName;
    }
    public static BaseMethodInfo newFinderByMethodInvokingLinked(String ID, String LinkTarget, BaseMethodInfo.MethodChecker findCallback){
        FindMethodInvokingMethod newInfo = new FindMethodInvokingMethod();
        newInfo.type = BaseMethodInfo.TYPE_FINDER_INFO;
        newInfo.id = ID;
        newInfo.LinkedToMethodID = LinkTarget;
        newInfo.checker = findCallback;
        return newInfo;
    }
    public static BaseMethodInfo newFinderByMethodInvoking(String ID, Member checkMethod, BaseMethodInfo.MethodChecker findCallback){
        FindMethodInvokingMethod newInfo = new FindMethodInvokingMethod();
        newInfo.type = BaseMethodInfo.TYPE_FINDER_INFO;
        newInfo.id = ID;
        newInfo.checkMethod = checkMethod;
        newInfo.checker = findCallback;
        return newInfo;
    }
    public static BaseMethodInfo newFinderWhichMethodInvoking(String ID, Member targetMethod, BaseMethodInfo.MethodChecker findCallback){
        FindMethodsWhichInvokeToTargetMethod newInfo = new FindMethodsWhichInvokeToTargetMethod();
        newInfo.type = BaseMethodInfo.TYPE_FINDER_INFO;
        newInfo.id = ID;
        newInfo.checkMethod = targetMethod;
        newInfo.checker = findCallback;
        return newInfo;
    }
    public static BaseMethodInfo newFinderWhichMethodInvokingLinked(String ID, String LinkedMethod, BaseMethodInfo.MethodChecker findCallback){
        FindMethodsWhichInvokeToTargetMethod newInfo = new FindMethodsWhichInvokeToTargetMethod();
        newInfo.type = BaseMethodInfo.TYPE_FINDER_INFO;
        newInfo.id = ID;
        newInfo.LinkedToMethodID = LinkedMethod;
        newInfo.checker = findCallback;
        return newInfo;
    }
}
