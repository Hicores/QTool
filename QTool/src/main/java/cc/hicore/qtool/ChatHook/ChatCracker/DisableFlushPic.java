package cc.hicore.qtool.ChatHook.ChatCracker;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "闪照破解",id = "DisableFlashPic",groupName = "聊天界面增强",targetID = 1,desc = "闪照直接显示为普通的图片",type = 1)
public class DisableFlushPic extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0],param -> {
            if (IsEnable){
                boolean result = (boolean) param.getResult();
                if (result){
                    Object MessageRecord = param.args[0];
                    MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{
                            String.class,String.class
                    },"flash_pic_flag","1");
                    String UserUin = MField.GetField(MessageRecord, "senderuin", String.class);
                    if(!UserUin.equals(QQEnvUtils.getCurrentUin()))param.setResult(false);
                }
            }
        });
        XPBridge.HookAfter(m[1],param -> {
            if (IsEnable){
                int re = (int) param.getResult();
                if (re == 66){
                    Object MessageRecord = param.args[1];
                    MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{
                            String.class,String.class
                    },"flash_pic_flag","1");
                    param.setResult(1);
                }
            }
        });
        XPBridge.HookAfter(m[2],param -> {
            if (IsEnable){
                Object mGetView = param.getResult();
                RelativeLayout mLayout;
                if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
                List MessageRecoreList = MField.GetField(param.thisObject,param.thisObject.getClass() ,"a", List.class);
                if(MessageRecoreList==null)return;
                Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
                String Extstr = MField.GetField(ChatMsg,"extStr",String.class);
                if (!TextUtils.isEmpty(Extstr) && Extstr.contains("flash_pic_flag")){
                    MMethod.CallMethod(mLayout,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,"闪照",null);
                }else {
                    TextView tailView = mLayout.findViewById(QQEnvUtils.getTargetID("chat_item_tail_message"));
                    if (tailView != null){
                        String text = tailView.getText().toString();
                        if (text.equals("闪照")){
                            MMethod.CallMethod(mLayout,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},false,"",null);
                        }
                    }
                }

            }
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
        for (int i=0;i<m.length;i++){
            if (m[i] == null)return false;
        }
        return true;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(DisableFlushPic.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[3];
        m[0] = MMethod.FindMethod("com.tencent.mobileqq.app.FlashPicHelper","a",boolean.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")
        });
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.typesupplier.PicTypeSupplier","a",int.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
        });
        if (m[1]==null){
            m[1] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.typesupplier.PicTypeSupplier","get",int.class,new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
        }

        m[2] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        return m;
    }

}
