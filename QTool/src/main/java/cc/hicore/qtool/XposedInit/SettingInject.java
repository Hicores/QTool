package cc.hicore.qtool.XposedInit;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.ActProxy.MainMenu;
import cc.hicore.qtool.BuildConfig;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import de.robv.android.xposed.XposedBridge;

public class SettingInject {
    public static void startInject() {

        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", boolean.class, new Class[]{Bundle.class});
        Method m2 = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.fragment.QQSettingSettingFragment"), "doOnCreateView", void.class, new Class[]{LayoutInflater.class, ViewGroup.class, Bundle.class});
        XPBridge.AfterHook afterHook = param -> {
            Utils.PostToMainDelay(() -> {
                try {
                    Activity act = null;
                    if (param.thisObject instanceof Activity) {
                        act = (Activity) param.thisObject;
                    } else {
                        act = MMethod.CallMethodNoParam(param.thisObject, "getActivity", MClass.loadClass("androidx.fragment.app.FragmentActivity"));
                    }

                    ResUtils.StartInject(act);
                    ViewGroup mRoot = null;
                    {
                        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem");
                        for (Field f : param.thisObject.getClass().getDeclaredFields()) {
                            try {
                                if (clz.equals(f.getType())) {
                                    f.setAccessible(true);
                                    View item = (View) f.get(param.thisObject);
                                    mRoot = (ViewGroup) item.getParent();
                                    if (mRoot instanceof LinearLayout) {
                                        break;
                                    }
                                }

                            } catch (Exception ignored) {
                                XposedBridge.log(ignored);
                            }
                        }
                    }
                    Activity finalAct = act;
                    View newItem = FormItemUtils.createMultiItem_Appent(act,"QTool" , BuildConfig.VERSION_NAME, v -> MainMenu.onCreate(finalAct));

                    TextView view = MField.GetFirstField(newItem,TextView.class);
                    if (view != null){
                        Utils.PostToMain(()->{
                            view.setText(Html.fromHtml("QTool  <img src=\"InnerCode\"> ㅤ ", source -> {
                                Drawable drawable = HookEnv.AppContext.getResources().getDrawable(R.drawable.append_icon, null);
                                drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/2-10 ,drawable.getIntrinsicHeight()/2-10);
                                return drawable;
                            },null));
                            ViewGroup.LayoutParams params = view.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            view.setLayoutParams(params);
                        });

                    }

                    newItem.setOnLongClickListener(v -> {
                        DebugDialog.startShow(v.getContext());
                        return true;
                    });
                    mRoot.addView(newItem, 0);
                } catch (Exception e) {
                    XposedBridge.log(e);
                    if (!GlobalConfig.Get_Boolean("Add_Menu_Button_to_Main", false)) {
                        Utils.ShowToastL("QTool无法创建设置选项,已自动开启主界面加号入口\n" + Log.getStackTraceString(e));
                        GlobalConfig.Put_Boolean("Add_Menu_Button_to_Main", true);
                    }
                }
            }, 200);
        };
        XPBridge.HookAfter(m, afterHook);
        if (m2 != null) {
            XPBridge.HookAfter(m2, afterHook);
        }
    }
}
