package cc.hicore.qtool.VoiceHelper.Hooker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.QQReflect;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.VoiceHelper.Panel.VoicePanel;
@XPItem(name = "语音面板",itemType = XPItem.ITEM_Hook)
public class QQVoicePanelInject{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "语音面板";
        ui.desc = "在QQ的发送语音界面点击打开";
        ui.type = 1;
        ui.targetID = 1;
        ui.groupName = "聊天辅助";
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        Class<?> clz = MClass.loadClass("com.tencent.mobileqq.activity.aio.audiopanel.PressToSpeakPanel");
        for (Constructor<?> cons : clz.getDeclaredConstructors()) {
            if (cons.getParameterCount() == 2) {
                if (cons.getParameterTypes()[0] == Context.class && cons.getParameterTypes()[1] == AttributeSet.class) {
                    cons.setAccessible(true);
                    container.addMethod("hook_1",cons);
                }
            }
        }
        container.addMethod("hook_2",QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.PttItemBuilder")));
        container.addMethod("hook_3",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.PttItemBuilder", "a", void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")}));

    }
    @VerController(max_targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getSimpleInit(MethodContainer container){
        container.addMethod("hook_4",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.SimpleUIAIOHelper", "a", void.class, new Class[0]));
    }
    @VerController(targetVer = QQVersion.QQ_8_9_0)
    @MethodScanner
    public void getSimpleInit_890(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook_4","initui() simple mode  bottomMargin 1 = ", m -> MMethod.FindMethod(m.getDeclaringClass(), "a", void.class, new Class[0])));
    }
    @VerController
    @XPExecutor(methodID = "hook_1",period = XPExecutor.After)
    public BaseXPExecutor worker_1(){
        return param -> {
            int mSpeakID = HookEnv.AppContext.getResources().getIdentifier("press_to_speak_iv", "id", HookEnv.AppContext.getPackageName());
            RelativeLayout RLayout = (RelativeLayout) param.thisObject;
            ResUtils.StartInject(RLayout.getContext());
            ImageView image = new ImageView(RLayout.getContext());
            image.setImageResource(R.drawable.voice_panel);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.dip2px(RLayout.getContext(), 25), Utils.dip2px(RLayout.getContext(), 25));
            params.addRule(RelativeLayout.BELOW, mSpeakID);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);

            RLayout.addView(image, params);
            image.setOnClickListener(v -> VoicePanel.createVoicePanel());
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_2",period = XPExecutor.After)
    public BaseXPExecutor worker_2(){
        return param -> {
            Object arr = param.getResult();
            Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
            System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
            Object MenuItem = MClass.NewInstance(arr.getClass().getComponentType(), 3100, "保存到QT");
            MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
            Array.set(ret, 0, MenuItem);

            param.setResult(ret);
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_3")
    public BaseXPExecutor worker_3(){
        return param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100) {
                String PTTPath = MMethod.CallMethodNoParam(chatMsg, "getLocalFilePath", String.class);
                VoicePanel.preSaveVoice(mContext, PTTPath);
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "hook_4")
    public BaseXPExecutor worker_4(){
        return param -> {
            View button = MField.GetRoundField(param.thisObject, param.thisObject.getClass(), ImageButton.class, 0);
            if (button != null) {
                button.setOnLongClickListener(v -> {
                    VoicePanel.createVoicePanel();
                    return true;
                });
            }
        };
    }
}
