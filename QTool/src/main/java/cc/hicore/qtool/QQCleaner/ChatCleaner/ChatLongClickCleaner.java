package cc.hicore.qtool.QQCleaner.ChatCleaner;

import android.app.AlertDialog;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "消息长按菜单净化",targetID = 2,groupName = "聊天界面净化",type = 2,id = "ChatLongClickCleaner")
public class ChatLongClickCleaner extends BaseHookItem implements BaseUiItem {
    private static final ArrayList<String> defCheckItem = new ArrayList<>();
    static {
        defCheckItem.add("复制");
        defCheckItem.add("转发");
        defCheckItem.add("收藏");
        defCheckItem.add("回复");
        defCheckItem.add("多选");
        defCheckItem.add("撤回");
        defCheckItem.add("删除");
        defCheckItem.add("群待办");
        defCheckItem.add("一起写");
        defCheckItem.add("设为精华");
        defCheckItem.add("待办");
        defCheckItem.add("截图");
        defCheckItem.add("相关表情");
        defCheckItem.add("存表情");
        defCheckItem.add("静音播放");
        defCheckItem.add("免提播放");
        defCheckItem.add("2X");
        defCheckItem.add("复制链接");
        defCheckItem.add("存微云");
        defCheckItem.add("发给电脑");
    }
    @Override
    public boolean startHook() throws Throwable {
        XPBridge.HookBefore(getMethod(),param -> {
            List<String> savedList = HookEnv.Config.getList("Set","ChatLongClickItemCleaner",true);
            if (savedList.isEmpty())return;
            String Name = MField.GetField(param.args[0],"a",String.class);
            if (savedList.contains(Name))param.setResult(null);
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        List<String> savedList = HookEnv.Config.getList("Set","ChatLongClickItemCleaner",true);
        return !savedList.isEmpty();
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
        boolean[] b = new boolean[defCheckItem.size()];
        List<String> savedList = HookEnv.Config.getList("Set","ChatLongClickItemCleaner",true);
        for (int i = 0;i<defCheckItem.size();i++){
            String name = defCheckItem.get(i);
            if (savedList.contains(name))b[i] = true;
        }
        new AlertDialog.Builder(context)
                .setMultiChoiceItems(defCheckItem.toArray(new String[0]), b, (dialog, which, isChecked) -> {

                }).setTitle("请选择需要隐藏的项目")
                .setNegativeButton("保存", (dialog, which) -> {
                    ArrayList<String> saveList = new ArrayList<>();
                    for (int i=0;i<b.length;i++){
                        if (b[i]){
                            saveList.add(defCheckItem.get(i));
                        }
                    }
                    if (!saveList.isEmpty()) HookLoader.CallHookStart(ChatLongClickCleaner.class.getName());
                    HookEnv.Config.setList("Set","ChatLongClickItemCleaner",saveList);
                }).show();
    }
    public Method getMethod(){
        return MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenu"),"a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem")
        });
    }
}
