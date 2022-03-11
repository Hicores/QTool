package com.hicore.qtool.XPWork.DebugSetInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hicore.ConfigUtils.GlobalConfig;
import com.hicore.HookItem;
import com.hicore.HookUtils.XPBridge;
import com.hicore.ReflectUtils.ResUtils;
import com.hicore.ReflectUtils.MClass;
import com.hicore.ReflectUtils.MField;
import com.hicore.ReflectUtils.MMethod;
import com.hicore.qtool.XPWork.QQUIUtils.FormItemUtils;
import com.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import com.hicore.qtool.XposedInit.ItemLoader.HookLoader;

import java.lang.reflect.Method;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@SuppressLint("ResourceType")
public class DebugSetHook extends BaseHookItem {
    private static final String TAG = "DEBUG_SET_INJECT_HOOK";
    @Override
    public String getTag() {
        return "DebugSetItemInject";
    }

    @Override
    public boolean startHook() {
        Method hookMethod = getHookMethod();
        XPBridge.HookAfter(hookMethod,param ->{
            ViewGroup group = (ViewGroup) param.args[1];
            Context context = group.getContext();
            ResUtils.StartInject(context);
            View oneItem = MField.GetFirstField(param.thisObject,MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"));
            LinearLayout parent = (LinearLayout) oneItem.getParent();
            parent.addView(FormItemUtils.createSingleItem(context,"QTool基础设置", v->{
                Dialog fullScreen = new Dialog(context,3);
                LinearLayout mRoot = new LinearLayout(context);
                mRoot.setOrientation(LinearLayout.VERTICAL);
                mRoot.setBackgroundColor(Color.WHITE);

                TextView titleBar = new TextView(context);
                titleBar.setTextColor(Color.parseColor("#6666ff"));
                titleBar.setText("状态信息");
                titleBar.setTextSize(24);
                mRoot.addView(titleBar,getMarginParam());

                TextView statusResInject = new TextView(context);
                statusResInject.setText("资源注入状态:"+(ResUtils.CheckResInject(context) ? "注入成功" : "注入失败"));
                statusResInject.setTextSize(16);
                mRoot.addView(statusResInject,getMarginParam());

                TextView statusStoragePath = new TextView(context);
                statusStoragePath.setText("当前设置的存储路径:"+ GlobalConfig.Get_String("StorePath"));
                statusStoragePath.setTextSize(16);
                mRoot.addView(statusStoragePath,getMarginParam());

                TextView titleClzInfo = new TextView(context);
                titleClzInfo.setTextColor(Color.parseColor("#6666ff"));
                titleClzInfo.setText("模块加载状态");
                titleClzInfo.setTextSize(24);
                mRoot.addView(titleClzInfo,getMarginParam());

                TextView hookInfo = new TextView(context);
                hookInfo.setText(getClassReport());
                hookInfo.setTextSize(12);
                mRoot.addView(hookInfo,getMarginParam());

                fullScreen.setContentView(mRoot);
                fullScreen.show();
            }),-1);
        });
        return true;
    }
    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return getHookMethod() != null;
    }

    public Method getHookMethod(){
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.settings.message.AssistantSettingFragment"),
                "doOnCreateView",void.class,new Class[]{
                        LayoutInflater.class, ViewGroup.class, Bundle.class});
        return m;
    }
    public ViewGroup.LayoutParams getMarginParam(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40,0,0,0);
        params.setMarginEnd(30);
        return params;
    }
    public String getClassReport(){
        StringBuilder builder = new StringBuilder();
        for (HookLoader.CheckResult result: HookLoader.CheckForItemsStatus()){
            builder.append(result.Name).append("->开启:").append(result.IsEnable).append("  ,可用:").append(result.IsAvailable).append("\n");
            if (!TextUtils.isEmpty(result.ErrorInfo)){
                builder.append("报告异常信息:\n").append(result.ErrorInfo).append("-------------------------\n");
            }
        }
        return builder.toString();
    }
}
