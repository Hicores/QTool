package cc.hicore.qtool.EmoHelper.Hooker;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.qtool.HookEnv;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "GuildSessionMonitor",itemType = XPItem.ITEM_Hook,targetVer = QQVersion.QQ_8_9_3)
public class GuildSessionMonitor {
    public static String guildID;
    public static String channelID;
    @VerController
    @MethodScanner
    public void findMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("hook","aioFactory.javaClass.name",m->true));
    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor xpWorker(){
        return param -> {
            Object currentContext = param.args[0];
            guildID = MField.GetField(currentContext,"a",String.class);
            channelID = MField.GetField(currentContext,"b",String.class);
            HookEnv.isCurrentGuild = true;
        };
    }
}
