package cc.hicore.qtool.XPWork.QQUIUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;

public class FormItemUtils {
    private static final String TAG = "FormItemUtils";
    public static View createSingleItem(Context context, String title, View.OnClickListener listener){
        try {
            View NewItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"),context);
            MMethod.CallMethodSingle(NewItem,"setLeftText",void.class,title);
            NewItem.setOnClickListener(listener);
            return NewItem;
        } catch (Exception e) {
            LogUtils.error(TAG,"Can't create ListItem because:"+ Log.getStackTraceString(e));
            return null;
        }
    }
    public static View createMultiItem(Context context,String title,String rightLine,View.OnClickListener listener){
        try {
            View NewItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"),context);
            MMethod.CallMethodSingle(NewItem,"setLeftText",void.class,title);
            MMethod.CallMethodSingle(NewItem,"setRightText",void.class,rightLine);
            NewItem.setOnClickListener(listener);
            return NewItem;
        } catch (Exception e) {
            LogUtils.error(TAG,"Can't create MultiItem because:"+ Log.getStackTraceString(e));
            return null;
        }
    }
}
