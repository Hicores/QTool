package cc.hicore.qtool.ChatHook.ChatCracker;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.qtool.QQManager.QQEnvUtils;

@XPItem(itemType = XPItem.ITEM_Hook,name = "闪照破解",targetVer = QQVersion.QQ_8_7_0)
public class DisableFlushPic{
    @MethodScanner
    @VerController
    public void getAllMethod(MethodContainer container){
        container.addMethod("replaceRecord",MMethod.FindMethod("com.tencent.mobileqq.app.FlashPicHelper",null,boolean.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")}));

        container.addMethod("replaceRecord2",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.typesupplier.PicTypeSupplier","get",int.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
        }));

        container.addMethod("replaceRecord3",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1", "getView", View.class, new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        }));
    }

    @XPExecutor(methodID = "replaceRecord")
    @VerController
    public BaseXPExecutor onReplaceRecord1(){
        return param -> {
            boolean result = (boolean) param.getResult();
            if (result){
                Object MessageRecord = param.args[0];
                MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{
                        String.class,String.class
                },"flash_pic_flag","1");
                String UserUin = MField.GetField(MessageRecord, "senderuin", String.class);
                if(!UserUin.equals(QQEnvUtils.getCurrentUin()))param.setResult(false);
            }
        };
    }

    @XPExecutor(methodID = "replaceRecord2")
    @VerController
    public BaseXPExecutor onReplaceRecord2(){
        return param -> {
            int re = (int) param.getResult();
            if (re == 66){
                Object MessageRecord = param.args[1];
                MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{
                        String.class,String.class
                },"flash_pic_flag","1");
                param.setResult(1);
            }
        };
    }

    @XPExecutor(methodID = "replaceRecord3")
    @VerController
    public BaseXPExecutor onReplaceRecord3(){
        return param -> {
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)mLayout = (RelativeLayout) mGetView;else return;
            List MessageRecoreList = MField.GetFirstField(param.thisObject,List.class);
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
        };
    }
}
