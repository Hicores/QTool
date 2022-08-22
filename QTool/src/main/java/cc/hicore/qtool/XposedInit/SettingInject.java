package cc.hicore.qtool.XposedInit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.MainMenu;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import de.robv.android.xposed.XposedBridge;

public class SettingInject {
    public static void startInject(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", boolean.class, new Class[]{Bundle.class});
        XPBridge.HookAfter(m,param -> {
            Utils.PostToMainDelay(()->{
                try {
                    Activity act = (Activity) param.thisObject;
                    ResUtils.StartInject(act);
                    ViewGroup mRoot = null;
                    Class<?> clz = MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem");
                    for (Field f : act.getClass().getDeclaredFields()){
                        try{
                            if (clz.equals(f.getType())){
                                f.setAccessible(true);
                                View item = (View) f.get(act);
                                mRoot = (ViewGroup) item.getParent();
                                if (mRoot instanceof LinearLayout){
                                    break;
                                }
                            }

                        }catch (Exception ignored){
                            XposedBridge.log(ignored);
                        }
                    }
                    View newItem = FormItemUtils.createMultiItem(act, "QTool", BuildConfig.VERSION_NAME, v -> MainMenu.onCreate(act));
                    newItem.setOnLongClickListener(v -> {
                        DebugDialog.startShow(v.getContext());
                        return true;
                    });
                    mRoot.addView(newItem,0);
                } catch (Exception e) {
                    XposedBridge.log(e);
                    if (!GlobalConfig.Get_Boolean("Add_Menu_Button_to_Main",false)){
                        Utils.ShowToastL("QTool无法创建设置选项,已自动开启主界面加号入口\n"+ Log.getStackTraceString(e));
                        GlobalConfig.Put_Boolean("Add_Menu_Button_to_Main",true);
                    }
                }
            },200);


        });
    }
}
