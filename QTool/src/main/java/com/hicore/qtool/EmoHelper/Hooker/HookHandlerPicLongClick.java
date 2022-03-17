package com.hicore.qtool.EmoHelper.Hooker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.QQReflect;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.qtool.EmoHelper.Panel.EmoPanel;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

@HookItem(isDelayInit = true,isRunInAllProc = false)
public class HookHandlerPicLongClick extends BaseHookItem {
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0],param -> {
            if (HookInjectEmoTabView.IsEnable){
                Object arr = param.getResult();
                Object ret = Array.newInstance(arr.getClass().getComponentType(),Array.getLength(arr)+1);
                System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),3100,"保存到QT");
                MField.SetField(MenuItem,"c",Integer.MAX_VALUE-1);
                Array.set(ret,0,MenuItem);

                param.setResult(ret);
            }
        });
        XPBridge.HookBefore(m[1],param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100){
                String MD5 = MField.GetField(chatMsg,"md5");
                String URL = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+MD5+"/0?term=2";
                new Handler(Looper.getMainLooper()).post(()-> EmoPanel.PreSavePicToList(URL,MD5,mContext));
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return HookInjectEmoTabView.IsEnable;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder"),"a");
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder","a",void.class,new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")});
        return m;
    }

}
