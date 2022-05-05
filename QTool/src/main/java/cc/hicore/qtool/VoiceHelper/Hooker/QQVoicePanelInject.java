package cc.hicore.qtool.VoiceHelper.Hooker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.QQReflect;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.R;
import cc.hicore.qtool.VoiceHelper.Panel.VoicePanel;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;


@HookItem(isRunInAllProc = false, isDelayInit = false)
@UIItem(name = "语音面板",desc = "在QQ的发送语音界面点击打开", type = 1, id = "VoicePanelInject", targetID = 1,groupName = "聊天辅助")
public class QQVoicePanelInject extends BaseHookItem implements BaseUiItem {
    boolean IsEnable = false;

    @Override
    public boolean startHook() throws Throwable {
        Member[] m = getMethod();

        XPBridge.HookAfter(m[0], param -> {
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
        });

        XPBridge.HookAfter(m[1], param -> {
            if (IsEnable) {
                Object arr = param.getResult();
                Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
                System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "保存到QT");
                MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
                Array.set(ret, 0, MenuItem);

                param.setResult(ret);
            }
        });

        XPBridge.HookBefore(m[2], param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100) {
                String PTTPath = MMethod.CallMethodNoParam(chatMsg, "getLocalFilePath", String.class);
                VoicePanel.preSaveVoice(mContext, PTTPath);
            }
        });

        XPBridge.HookAfter(m[3], param -> {
            if (IsEnable) {
                View button = MField.GetRoundField(param.thisObject, param.thisObject.getClass(), ImageButton.class, 0);
                if (button != null) {
                    button.setOnLongClickListener(v -> {
                        VoicePanel.createVoicePanel();
                        return true;
                    });
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
        Member[] methods = getMethod();
        for (Member m : methods) {
            if (m == null) return false;
        }
        return true;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(QQVoicePanelInject.class.getName());
        }
    }

    @Override
    public void ListItemClick(Context context) {

    }

    public Member[] getMethod() {
        Member[] m = new Member[4];
        Class clz = MClass.loadClass("com.tencent.mobileqq.activity.aio.audiopanel.PressToSpeakPanel");
        for (Constructor cons : clz.getDeclaredConstructors()) {
            if (cons.getParameterCount() == 2) {
                if (cons.getParameterTypes()[0] == Context.class && cons.getParameterTypes()[1] == AttributeSet.class) {
                    cons.setAccessible(true);
                    m[0] = cons;
                    break;
                }
            }
        }

        m[1] = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.PttItemBuilder"), "a");
        m[2] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.PttItemBuilder", "a", void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")});

        m[3] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.SimpleUIAIOHelper", "a", void.class, new Class[0]);

        return m;
    }
}
