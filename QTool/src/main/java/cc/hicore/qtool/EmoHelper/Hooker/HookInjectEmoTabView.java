package cc.hicore.qtool.EmoHelper.Hooker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.QQReflect;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.EmoHelper.Panel.EmoPanel;
import cc.hicore.qtool.QQTools.QQDecodeUtils.DecodeForEncPic;
import cc.hicore.qtool.R;

/*
注入主界面选项菜单,同时在菜单勾选时请求三个钩子的挂钩确认
 */
@SuppressLint("ResourceType")
@XPItem(name = "表情面板",itemType = XPItem.ITEM_Hook)
public class HookInjectEmoTabView{
    @VerController
    @UIItem
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "表情面板";
        ui.groupName = "聊天辅助";
        ui.type = 1;
        ui.targetID = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getMethod(MethodContainer container){
        Method[] m = new Method[9];
        container.addMethod("common_icon_create",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout",
                null, void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")}));
        container.addMethod("pic_item_menu_inject",QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder")));
        container.addMethod("pic_item_menu_click",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder", null, void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")}));
        container.addMethod("mix_item_menu_inject",QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder")));
        container.addMethod("mix_item_meun_click",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder", null, void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")}));
        container.addMethod("marker_item_menu_inject", QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MarketFaceItemBuilder")));
        container.addMethod("marker_item_menu_click",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MarketFaceItemBuilder", null, void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")}));
    }
    @VerController(targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getPanelIconCreateMethod_8893(MethodContainer container){
        container.addMethod(MethodFinderBuilder.newFinderByString("simple_emo_icon_create","initui() simple mode  bottomMargin 1 = ",m -> m.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.aio.helper.SimpleUIAIOHelper")));
        container.addMethod(MethodFinderBuilder.newFinderByString("guild_emo_icon_create","em_aio_input_box",m->m.getDeclaringClass().getName().equals("com.tencent.mobileqq.guild.chatpie.helper.GuildInputBarCommonComponent")));
    }
    @VerController(max_targetVer = QQVersion.QQ_8_8_93)
    @MethodScanner
    public void getPanelIconCreateMethod(MethodContainer container){
        container.addMethod("simple_emo_icon_create",MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.SimpleUIAIOHelper", "a", void.class, new Class[0]));
        container.addMethod("guild_emo_icon_create",MMethod.FindMethod("com.tencent.mobileqq.guild.chatpie.helper.GuildInputBarCommonComponent", "b", void.class, new Class[0]));

    }
    @VerController
    @XPExecutor(methodID = "common_icon_create",period = XPExecutor.After)
    public BaseXPExecutor inject_emo_bar(){
        return param -> {
            LinearLayout l = (LinearLayout) param.thisObject;
            if (l.findViewById(11223366)!=null)return;
            View v = l.getChildAt(2);
            if (v == null)return;
            ResUtils.StartInject(v.getContext());
            ImageView image = new ImageView(v.getContext());
            image.setImageResource(R.drawable.huaji);
            image.setId(11223366);
            image.setTag(123456);
            l.addView(image, 4, v.getLayoutParams());
            new Handler(Looper.getMainLooper()).post(image::invalidate);
            image.setOnClickListener(vxx -> EmoPanel.createShow(image.getContext()));
        };
    }
    @VerController
    @XPExecutor(methodID = "pic_item_menu_inject",period = XPExecutor.After)
    public BaseXPExecutor inject_pic_menu_builder(){
        return param -> {
            Object arr = param.getResult();
            Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
            System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
            Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "QT保存");
            MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
            Array.set(ret, 0, MenuItem);

            param.setResult(ret);
        };
    }
    @VerController
    @XPExecutor(methodID = "pic_item_menu_click")
    public BaseXPExecutor inject_pic_menu_click(){
        return param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100) {
                String MD5 = MField.GetField(chatMsg, "md5");
                String URL = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + MD5 + "/0?term=2";
                new Handler(Looper.getMainLooper()).post(() -> EmoPanel.PreSavePicToList(URL, MD5, mContext));
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "mix_item_menu_inject",period = XPExecutor.After)
    public BaseXPExecutor inject_mix_menu_builder(){
        return param -> {
            Object arr = param.getResult();
            Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
            System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
            Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "QT保存");
            MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
            Array.set(ret, 0, MenuItem);

            param.setResult(ret);
        };
    }
    @VerController
    @XPExecutor(methodID = "mix_item_meun_click")
    public BaseXPExecutor inject_mix_menu_click(){
        return param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100) {
                List list = MField.GetField(chatMsg, "msgElemList", List.class);
                ArrayList<String> MD5 = new ArrayList<>();
                for (Object msgItem : list) {
                    if (msgItem.getClass().getName().contains("MessageForPic")) {
                        String PicMd5 = MField.GetField(msgItem, "md5", String.class);
                        MD5.add(PicMd5);
                    }
                }
                if (MD5.size() == 0) {
                    Utils.ShowToastL("没有图片");
                } else if (MD5.size() == 1) {//如果为单张图片则直接显示了
                    String url = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + MD5.get(0) + "/0?term=2";
                    EmoPanel.PreSavePicToList(url, MD5.get(0), mContext);
                } else {
                    ArrayList<String> urls = new ArrayList<>();
                    for (String md5 : MD5)
                        urls.add("http://gchat.qpic.cn/gchatpic_new/0/0-0-" + md5 + "/0?term=2");
                    EmoPanel.PreSaveMultiPicList(urls, MD5, mContext);
                }
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "marker_item_menu_inject",period = XPExecutor.After)
    public BaseXPExecutor inject_market_menu_builder(){
        return param -> {
            Object arr = param.getResult();
            Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
            System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
            Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "QT保存");
            MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
            Array.set(ret, 0, MenuItem);

            param.setResult(ret);
        };
    }
    @VerController
    @XPExecutor(methodID = "marker_item_menu_click")
    public BaseXPExecutor inject_marker_menu_click(){
        return param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100) {
                Object mMarkFaceMessage = MField.GetField(chatMsg, "mMarkFaceMessage");
                String LocalPath = DecodeForEncPic.decodeGifForLocalPath(MField.GetField(mMarkFaceMessage, "dwTabID"), MField.GetField(mMarkFaceMessage, "sbufID"));
                new Handler(Looper.getMainLooper()).post(() -> EmoPanel.PreSavePicToList(LocalPath, DataUtils.getFileMD5(new File(LocalPath)), mContext));
            }
        };
    }
    @VerController
    @XPExecutor(methodID = "guild_emo_icon_create")
    public BaseXPExecutor guild_emo_button(){
        return param -> {
            new Handler(Looper.getMainLooper())
                    .postDelayed(() -> {
                        try {
                            View XEdit = MField.GetFirstField(param.thisObject, MClass.loadClass("com.tencent.widget.XEditTextEx"));
                            ViewGroup parentLayout = (ViewGroup) XEdit.getParent();

                            for (int i = 0; i < parentLayout.getChildCount(); i++) {
                                View v = parentLayout.getChildAt(i);
                                CharSequence content = v.getContentDescription();
                                if (content != null && content.toString().contains("拉起表情面板")) {
                                    v.setOnLongClickListener((a) -> {
                                        EmoPanel.createShow(v.getContext());
                                        return true;
                                    });
                                }
                            }
                        } catch (Exception e) {
                            LogUtils.error("InjectEmoPanelToGuild", e);
                        }

                    }, 200);
        };
    }
    @VerController
    @XPExecutor(methodID = "simple_emo_icon_create")
    public BaseXPExecutor simple_mode_emo_button(){
        return param -> {
            View button = MField.GetRoundField(param.thisObject, param.thisObject.getClass(), ImageButton.class, 1);
            if (button != null) {
                button.setOnLongClickListener(v -> {
                    EmoPanel.createShow(v.getContext());
                    return true;
                });
            }
        };
    }
}
