package cc.hicore.qtool.QQManager.UtilsImpl;

import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.ApiExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPChecker;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.Assert;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.HostInfo;

@XPItem(name = "get_Group_Name_By_Contact",itemType = XPItem.ITEM_Api)
public class Get_Group_Name_By_Contact {
    @VerController
    @ApiExecutor
    public String GetTroopNameByContact(String GroupUin) throws Exception {
        String mStr = MMethod.CallStaticMethod(MClass.loadClass("com.tencent.mobileqq.utils.ContactUtils"),
                HostInfo.getVerCode() > 8000 ? "W":"a",String.class, HookEnv.AppInterface,GroupUin,true);
        return mStr;
    }
    @VerController
    @XPChecker
    public void check(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.utils.ContactUtils"),
                HostInfo.getVerCode() > 8000 ? "W":"a",
                String.class,
                new Class[]{
                        Classes.AppInterface(),String.class,boolean.class});
        Assert.notNull(m,"method is NULL");
    }
}
