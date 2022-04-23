package cc.hicore.qtool.XPWork.LittleHook;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@HookItem(isRunInAllProc = false,isDelayInit = false)
public class SavedToXml extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.data.MessageForStarLeague"), "decodeMsgFromXmlBuff", Classes.QQAppinterFace(), int.class, long.class, byte[].class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                byte[] arr = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.structmsg.StructMsgUtils"),"a",
                        byte[].class,new Class[]{byte[].class,int.class},param.args[3],-1);
                MMethod.CallMethodParams(param.getResult(),"saveExtInfoToExtStr",void.class,"SavedXml",new String(arr));
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return true;
    }
}
