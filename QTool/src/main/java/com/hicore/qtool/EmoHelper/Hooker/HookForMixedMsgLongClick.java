package com.hicore.qtool.EmoHelper.Hooker;

import android.content.Context;

import com.hicore.HookItem;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.ReflectUtils.QQReflect;
import com.hicore.ReflectUtils.XPBridge;
import com.hicore.Utils.Utils;
import com.hicore.qtool.EmoHelper.Panel.EmoPanel;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/*
   挂钩QQ混合消息长按菜单的获取,以及点击事件
 */
@HookItem(isRunInAllProc = false,isDelayInit = true)
public class HookForMixedMsgLongClick extends BaseHookItem {

    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0], param -> {
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
                List list = MField.GetField(chatMsg,"msgElemList",List.class);
                ArrayList<String> MD5 = new ArrayList<>();
                for(Object msgItem : list){
                    if(msgItem.getClass().getName().contains("MessageForPic")){
                        String PicMd5 = MField.GetField(msgItem,"md5",String.class);
                        MD5.add(PicMd5);
                    }
                }
                if (MD5.size() == 0){
                    Utils.ShowToastL("没有图片");
                } else if (MD5.size() == 1){//如果为单张图片则直接显示了
                    String url = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+MD5.get(0)+"/0?term=2";
                    EmoPanel.PreSavePicToList(url,MD5.get(0),mContext);
                }else {
                    ArrayList<String> urls = new ArrayList<>();
                    for(String md5 : MD5)urls.add("http://gchat.qpic.cn/gchatpic_new/0/0-0-"+md5+"/0?term=2");
                    EmoPanel.PreSaveMultiPicList(urls,MD5,mContext);
                }
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
        m[0] = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder"),"a");
        m[1] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder","a",void.class,new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")});
        return m;
    }

}
