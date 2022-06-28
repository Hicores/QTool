package cc.hicore.qtool.QQManager.ManagerImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPChecker;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.HookEnv;

@XPItem(name = "Group_Change_Title",itemType = XPItem.ITEM_Api)
public class Group_Change_Title {
    @VerController
    @ApiExecutor
    public void change(String GroupUin, String UserUin, String title) throws Exception {
        Object mProxy = Proxy.newProxyInstance(HookEnv.mLoader, new Class[]{MClass.loadClass("mqq.observer.BusinessObserver")}, (proxy, method, args) -> null);
        MMethod.CallMethod(null, MClass.loadClass("com.tencent.biz.troop.EditUniqueTitleActivity"), null, void.class, new Class[]{
                        HookEnv.AppInterface.getClass(), String.class, String.class, String.class, MClass.loadClass("mqq.observer.BusinessObserver")
                },
                HookEnv.AppInterface, GroupUin, UserUin, title, mProxy);
    }
    @VerController
    @XPChecker
    public void check(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.biz.troop.EditUniqueTitleActivity"),null,void.class,new Class[]{
                HookEnv.AppInterface.getClass(), String.class, String.class, String.class, MClass.loadClass("mqq.observer.BusinessObserver")
        });
        Assert.notNull(m,"change title method is null");
    }
}
