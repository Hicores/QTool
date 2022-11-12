package cc.hicore.qtool.JavaPlugin.Hooker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.QQReflect;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.JavaPlugin.InChatControl.FloatWindowControl;
import cc.hicore.qtool.QQMessage.QQSessionUtils;
import cc.hicore.qtool.StickerPanelPlus.PanelUtils;

@XPItem(name = "IMessageMenuHooker",itemType = XPItem.ITEM_Hook)
public class IMessageMenuHooker {
    @MethodScanner
    @VerController
    public void doFindMethod(MethodContainer container){
        Method before = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder"));
        container.addMethod(MethodFinderBuilder.newFinderByMethodInvoking("menu_add",before,m->{
            Method checkMethod = (Method) m;
            if (checkMethod.getParameterCount() == 0 && checkMethod.getReturnType().isArray()){
                return true;
            }
            return false;
        }));
        container.addMethod("item_click", MMethod.FindMethod("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder", null, void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")}));
    }
    @VerController
    @XPExecutor(methodID = "menu_add",period = XPExecutor.After)
    public BaseXPExecutor xpWorker(){
        return param -> {
            if (FloatWindowControl.IsAvailable(QQSessionUtils.getGroupUin(),QQSessionUtils.getSessionID() == 1,2)){
                Object arr = param.getResult();
                Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
                System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                Object MenuItem = MClass.NewInstance(arr.getClass().getComponentType(), 3150, "脚本");
                MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
                Array.set(ret, 0, MenuItem);
                param.setResult(ret);
            }
        };
    }

    @VerController
    @XPExecutor(methodID = "item_click")
    public BaseXPExecutor inject_pic_menu_click() {
        return param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3150) {
                FloatWindowControl.ShowButtonDialog(HookEnv.SessionInfo,2,chatMsg);
                param.setResult(null);
            }
        };
    }
}
