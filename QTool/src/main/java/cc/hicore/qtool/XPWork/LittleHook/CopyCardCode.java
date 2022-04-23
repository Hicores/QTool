package cc.hicore.qtool.XPWork.LittleHook;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;

import java.lang.reflect.Method;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(itemName = "复制卡片代码",itemType = 1,itemDesc = "可长按消息复制卡片代码",ID = "CopyCardMsg",mainItemID = 1)
public class CopyCardCode extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @SuppressLint("ResourceType")
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookAfter(getMethod(),param -> {
            if (!isEnable())return;
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)
            {
                mLayout = (RelativeLayout) mGetView;
            }
            else
            {
                return;
            }
            List MessageRecoreList = MField.GetField(param.thisObject,param.thisObject.getClass() ,"a", List.class);
            if(MessageRecoreList==null)return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);
            if(ChatMsg.getClass().getSimpleName().equals("MessageForArkApp") || MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(ChatMsg.getClass())) {
                //复制卡片消息的标题
                TextView tv=mLayout.findViewById(445588);
                if(tv==null) {
                    //长按标签,位于Parent顶部中央,最大化
                    RelativeLayout.LayoutParams RLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    RLP.width= ViewGroup.LayoutParams.MATCH_PARENT;
                    tv = new TextView(mLayout.getContext());
                    mLayout.addView(tv,RLP);
                    tv.setText("长按复制卡片代码");
                    tv.setGravity(Gravity.CENTER);//居中显示
                    tv.setTextColor(Color.RED);
                    tv.setId(445588);
                }


                tv.setTag(ChatMsg);//保存消息对象
                tv.setOnLongClickListener(view -> {
                    Object ChatMessage = view.getTag();
                    try{
                        if(ChatMessage.getClass().getSimpleName().equals("MessageForArkApp")) {
                            Object  ArkAppMsg= MField.GetField(ChatMessage,ChatMessage.getClass(),"ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                            String json= MMethod.CallMethod(ArkAppMsg,MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"),"toAppXml",String.class,new Class[0],new Object[0]);
                            Utils.SetTextClipboard(json);
                            Utils.ShowToast("已复制");
                        }
                        else if(MClass.loadClass("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(ChatMessage.getClass())) {
                            Object Structing = MField.GetField(ChatMessage,ChatMessage.getClass(),"structingMsg",MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                            String xml=MMethod.CallMethod(Structing, MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),"getXml",String.class,new Class[0],new Object[0]);
                            Utils.SetTextClipboard(xml);
                            Utils.ShowToast("已复制");
                        }
                    }
                    catch (Throwable e)
                    {
                        LogUtils.error("CopyXml", Log.getStackTraceString(e));
                    }
                    return false;
                });
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
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(CopyCardCode.class.getName());
    }

    @Override
    public void ListItemClick() {

    }
    public Method getMethod(){
        Method HookMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1","getView", View.class,new Class[]{
                int.class,
                View.class,
                ViewGroup.class
        });
        return HookMethod;
    }
}
