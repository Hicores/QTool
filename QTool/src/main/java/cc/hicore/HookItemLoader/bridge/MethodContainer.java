package cc.hicore.HookItemLoader.bridge;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class MethodContainer {
    private final ArrayList<BaseMethodInfo> NeedMethodList = new ArrayList<>();
    public void addMethod(String id, Member m){
        NeedMethodList.add(MethodFinderBuilder.newCommonMethod(id,m));
    }
    public void addMethod(BaseMethodInfo m){
        NeedMethodList.add(m);
    }
    public List<BaseMethodInfo> getInfo(){
        return NeedMethodList;
    }
}
