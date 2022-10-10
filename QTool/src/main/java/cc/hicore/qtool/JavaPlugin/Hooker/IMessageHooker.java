package cc.hicore.qtool.JavaPlugin.Hooker;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;

@XPItem(name = "IPluginMessageHooker",itemType = XPItem.ITEM_Hook)
public class IMessageHooker {
    @VerController
    @MethodScanner
    public void findMethod(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("pbSendMsg","--->sendRichTextMessageWith_MR : msgseq=",m->true));
    }
    @XPExecutor(methodID = "pbSendMsg")
    @VerController
    public BaseXPExecutor hook_worker(){
        return param -> {
            Object records = param.args[0];

        };
    }
}
