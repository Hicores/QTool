package cc.hicore.qtool.ServerKiller;

import android.app.AlertDialog;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = true)
@UIItem(name = "屏蔽Servlet注册",desc = "禁用了重要的Servlet可能会导致QQ功能异常",groupName = "服务调节",targetID = 4,type = 2,id = "ServletKiller")
public class ServletKiller extends BaseHookItem implements BaseUiItem {
    private static final ArrayList<String> servletList = new ArrayList<>();
    static {
        servletList.add("com.tencent.mobileqq.servlet.PushServlet");
        servletList.add("com.tencent.mobileqq.servlet.GameCenterServlet");
        servletList.add("com.tencent.mobileqq.mini.servlet.MiniAppGetAppInfoByIdForSDKServlet");
        servletList.add("com.tencent.mobileqq.zplan.servlet.ZPlanRequestServlet");
        servletList.add("com.tencent.mobileqq.compatible.TempServlet");
        servletList.add("mqq.app.BuiltInServlet");
        servletList.add("com.tencent.mobileqq.servlet.CliNotifyPush");
        servletList.add("com.tencent.mobileqq.phonecontact.handler.ContactBindServlet");
        servletList.add("com.tencent.mobileqq.config.QConfigServlet");
        servletList.add("com.tencent.biz.richframework.network.servlet.VSBaseServlet");
        servletList.add("com.tencent.mobileqq.kandian.base.msf.ReadInJoyMSFServlet");

        servletList.add("com.tencent.mobileqq.servlet.ReportServlet");
        servletList.add("com.tencent.mobileqq.newnearby.servlet.NearbyServlet");
        servletList.add("com.tencent.mobileqq.mini.servlet.MiniAppGetNewBaseLibServlet");
        servletList.add("com.qzone.common.servlet.QZoneServlet");
        servletList.add("com.tencent.mobileqq.flashchat.FlashChatServlet");
        servletList.add("com.tencent.mobileqq.activity.aio.stickerrecommended.StickerRecServlet");
        servletList.add("com.tencent.biz.ProtoServlet");
        servletList.add("cooperation.vip.manager.CheckLoveStateRequestManager");
        servletList.add("com.tencent.biz.pubaccount.api.impl.PublicAccountServletImpl");

        servletList.add("com.tencent.biz.pubaccount.weishi.net.WeishiServlet");
        servletList.add("com.tencent.mobileqq.transfile.ProtoServlet");
        servletList.add("com.tencent.relation.common.servlet.RelationServlet");
        servletList.add("cooperation.vip.tianshu.TianShuServlet");
        servletList.add("com.tencent.mobileqq.servlet.QZoneNotifyServlet");
        servletList.add("com.tencent.mobileqq.config.splashlogo.QQStoryConfigServlet");
        servletList.add("com.tencent.mobileqq.kandian.repo.aladdin.config.QQAladdinRequestHandler$AladdinConfigServlet");
        servletList.add("com.tencent.mobileqq.app.servlet.TroopExtensionServlet");
        servletList.add("com.tencent.mobileqq.vashealth.StepCounterServlert");
        servletList.add("com.tencent.mobileqq.qzonestatus.QzoneContactsFeedServlet");
        servletList.add("com.tencent.mobileqq.vas.adv.base.servlet.VasAdvServlet");
        servletList.add("com.tencent.biz.AuthorizeConfigServlet");
        servletList.add("com.tencent.mobileqq.servlet.IPDomainGetServlet");
        servletList.add("com.tencent.mobileqq.zplan.statistics.trace.sdk.component.TraceServlet");
        servletList.add("com.tencent.mobileqq.qqexpand.network.ExpandServlet");
        servletList.add("com.tencent.mobileqq.servlet.MobileReportServlet");
    }

    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            List<String> servletKiller = HookEnv.Config.getList("ServerKiller","Servlet",true);
            String servlet = (String) param.args[0];
            if (servletKiller.contains(servlet)){
                param.setResult(null);
                XposedBridge.log("Kill Servlet:"+servlet);
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        return getMethod() != null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {

    }

    @Override
    public void ListItemClick(Context context) {
        List<String> servletKiller = HookEnv.Config.getList("ServerKiller","Servlet",true);
        boolean[] bl = new boolean[servletList.size()];
        for (int i=0;i<bl.length;i++){
            if (servletKiller.contains(servletList.get(i))){
                bl[i] = true;
            }
        }
        new AlertDialog.Builder(context)
                .setMultiChoiceItems(servletList.toArray(new String[0]), bl, (dialog, which, isChecked) -> {

                }).setTitle("选择要禁用的Servlet项目")
                .setNegativeButton("保存", (dialog, which) -> {
                    ArrayList<String> newList = new ArrayList<>();
                    for (int i=0;i<bl.length;i++){
                        if (bl[i]){
                            newList.add(servletList.get(i));
                        }
                    }
                    HookEnv.Config.setList("ServerKiller","Servlet",newList);
                }).show();
    }
    public Method getMethod(){
        return MMethod.FindMethod("mqq.app.ServletContainer","getServlet", MClass.loadClass("mqq.app.Servlet"),new Class[]{
                String.class
        });
    }
}
