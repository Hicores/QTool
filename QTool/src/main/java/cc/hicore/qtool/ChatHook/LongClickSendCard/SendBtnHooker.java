package cc.hicore.qtool.ChatHook.LongClickSendCard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQMessage.QQMsgBuilder;
import cc.hicore.qtool.QQMessage.QQMsgSender;
import cc.hicore.qtool.XPWork.QQProxy.BaseChatPie;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import cc.hicore.qtool.XposedInit.MethodFinder;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "长按发送卡片消息",groupName = "聊天辅助",targetID = 1,type = 1,id = "LongClickSendCard")
public class SendBtnHooker extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0], param -> {
            if (IsEnable){
                ViewGroup vg;
                if (HostInfo.getVerCode() > 8000){
                    vg = (ViewGroup) m[1].invoke(param.thisObject);
                }else {
                    vg = MField.GetFirstField(param.thisObject,ViewGroup.class);
                }
                if (vg == null)return;
                Context ctx = vg.getContext();
                int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
                View sendBtn = vg.findViewById(fun_btn);
                int ed_id = ctx.getResources().getIdentifier("input", "id", ctx.getPackageName());
                EditText ed = vg.findViewById(ed_id);
                sendBtn.setOnLongClickListener(v->{
                    String input = ed.getText().toString();
                    if (input.startsWith("{")){
                        QQMsgSender.sendArkApp(HookEnv.SessionInfo, QQMsgBuilder.build_arkapp(input));
                        ed.setText("");
                        return true;
                    }else if (input.startsWith("<")){
                        QQMsgSender.sendStruct(HookEnv.SessionInfo, QQMsgBuilder.build_struct(input));
                        ed.setText("");
                        return true;
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
        Method[] m = getMethod();
        if (HostInfo.getVerCode() > 8000){
            return m[0] != null && m[1] != null;
        }else {
            return m[0] != null;
        }

    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(SendBtnHooker.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[2];
        m[0] = BaseChatPie.getMethod();
        if (HostInfo.getVerCode() > 8000){
            m[1] = MethodFinder.findMethodFromCache("getAIOViewGroup");
            if (m[1] == null){
                MethodFinder.NeedReportToFindMethod("getAIOViewGroup_","长按发送卡片消息","em_gb_ice_sticker",ma-> true);
                MethodFinder.NeedReportToFindMethodBeInvokedTag("getAIOViewGroup","长按发送卡片消息","getAIOViewGroup_",ma-> ma.getReturnType().equals(ViewGroup.class));
            }
        }

        return m;
    }
}
