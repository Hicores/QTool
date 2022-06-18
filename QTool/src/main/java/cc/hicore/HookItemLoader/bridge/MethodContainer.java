package cc.hicore.HookItemLoader.bridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.qtool.XposedInit.MethodFinder;

public class MethodContainer {
    private final ArrayList<BaseMethodInfo> NeedMethodList = new ArrayList<>();
    public void addMethod(Method m){
        NeedMethodList.add(MethodFinderBuilder.newCommonMethod(m));
    }
    public void addMethod(BaseMethodInfo m){
        NeedMethodList.add(m);
    }
    public List<BaseMethodInfo> getInfo(){
        return NeedMethodList;
    }
}
