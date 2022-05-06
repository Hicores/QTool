package cc.hicore.qtool.ChatHook.GameHook;

import android.app.AlertDialog;
import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "自定义骰子猜拳",groupName = "聊天辅助",type = 1,targetID = 1,id = "CrackDict")
public class DictHook extends BaseHookItem implements BaseUiItem {
    int CurrentRamdonDict = -1;
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable){
                String Name = MField.GetField(param.args[3],"name");
                if(Name.contains("骰子") && CurrentRamdonDict==-1) {
                    param.setResult(null);
                    selectDictGame(param);
                }else if(Name.equals("猜拳") && CurrentRamdonDict==-1) {
                    param.setResult(null);
                    selectCFGGame(param);
                }
            }
        });
        XPBridge.HookBefore(m[1],param -> {
            if (IsEnable){
                if(CurrentRamdonDict==-1)return;
                if(CurrentRamdonDict==666) {
                    CurrentRamdonDict=-1;
                    return;
                }
                int MaxValue = (int) param.args[0];
                if(MaxValue==6 || MaxValue ==3) {
                    param.setResult(CurrentRamdonDict);
                    CurrentRamdonDict = -1;
                }
            }
        });
        XPBridge.HookBefore(m[2],param -> {
            if (IsEnable)param.setResult(true);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null && m[2] != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(DictHook.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[3];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.emoticonview.sender.PicEmoticonInfoSender"),"sendMagicEmoticon",void.class,new Class[]{
                MClass.loadClass("com.tencent.common.app.business.BaseQQAppInterface"),
                Context.class,
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                MClass.loadClass("com.tencent.mobileqq.data.Emoticon"),
                MClass.loadClass("com.tencent.mobileqq.emoticon.StickerInfo")
        });
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.magicface.drawable.PngFrameUtil","a",int.class,new Class[]{int.class});
        m[2] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.emoticon.api.impl.EmojiManagerServiceImpl"),"getMagicFaceSendAccessControl",boolean.class,new Class[0]);
        return m;
    }
    private void selectCFGGame(XC_MethodHook.MethodHookParam params){
        final String[] SelectItem = new String[]{"石头","剪刀","布"};
        new AlertDialog.Builder(Utils.getTopActivity(),3)
                .setTitle("设置猜拳的内容")
                .setItems(SelectItem, (dialog, which) -> {
                    CurrentRamdonDict = which;
                    ReInvokeMethod(params);
                }).setNegativeButton("随机", (dialog, which) -> {
            CurrentRamdonDict=666;
            ReInvokeMethod(params);
        }).show();
    }
    private void selectDictGame(XC_MethodHook.MethodHookParam params) {
        final String[] SelectItem = new String[]{"1","2","3","4","5","6"};
        new AlertDialog.Builder(Utils.getTopActivity(),3)
                .setTitle("设置骰子的点数")
                .setItems(SelectItem, (dialog, which) -> {
                    CurrentRamdonDict = which;
                    ReInvokeMethod(params);
                }).setNegativeButton("随机", (dialog, which) -> {
            CurrentRamdonDict=666;
            ReInvokeMethod(params);
        }).show();

    }
    private static void ReInvokeMethod(XC_MethodHook.MethodHookParam params) {
        try {
            XposedBridge.invokeOriginalMethod(params.method,params.thisObject,params.args);
        } catch (Exception e) { }
    }
}
