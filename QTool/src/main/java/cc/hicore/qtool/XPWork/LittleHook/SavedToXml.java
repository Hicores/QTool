package cc.hicore.qtool.XPWork.LittleHook;

import cc.hicore.HookItem;
import cc.hicore.HookItemLoader.Annotations.CommonExecutor;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.ReflectUtils.Classes;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@XPItem(name = "XP_Save",itemType = XPItem.ITEM_Hook)
public class SavedToXml{
    @VerController
    @CommonExecutor
    public void execute(){
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.data.MessageForStarLeague"), "decodeMsgFromXmlBuff", Classes.QQAppinterFace(), int.class, long.class, byte[].class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                byte[] arr = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.structmsg.StructMsgUtils"), "a",
                        byte[].class, new Class[]{byte[].class, int.class}, param.args[3], -1);
                MMethod.CallMethodParams(param.getResult(), "saveExtInfoToExtStr", void.class, "SavedXml", new String(arr));
            }
        });
    }
}
