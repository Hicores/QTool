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

import cc.hicore.HookItem;
import cc.hicore.LogUtils.LogUtils;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.QQReflect;
import cc.hicore.ReflectUtils.ResUtils;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.Utils.DataUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.EmoHelper.Panel.EmoPanel;
import cc.hicore.qtool.QQTools.QQDecodeUtils.DecodeForEncPic;
import cc.hicore.qtool.R;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;

/*
注入主界面选项菜单,同时在菜单勾选时请求三个钩子的挂钩确认
 */
@UIItem(name = "表情面板",type = 1,targetID = 1,id = "EmoHelper",groupName = "聊天辅助")
@HookItem(isDelayInit = true, isRunInAllProc = false)
public class HookInjectEmoTabView extends BaseHookItem implements BaseUiItem {
    public static boolean IsEnable = true;

    @SuppressLint("ResourceType")
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookAfter(m[0], param -> {
            LinearLayout l = (LinearLayout) param.thisObject;
            if (l.findViewById(11223366)!=null)return;
            View v = l.getChildAt(2);
            if (IsEnable) {
                ResUtils.StartInject(v.getContext());
                ImageView image = new ImageView(v.getContext());
                image.setImageResource(R.drawable.huaji);
                image.setId(11223366);
                image.setTag(123456);
                l.addView(image, 4, v.getLayoutParams());
                new Handler(Looper.getMainLooper()).post(image::invalidate);
                image.setOnClickListener(vxx -> EmoPanel.createShow(image.getContext()));
            }
        });

        XPBridge.HookAfter(m[1], param -> {
            if (IsEnable) {
                Object arr = param.getResult();
                Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
                System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "QT保存");
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
                String MD5 = MField.GetField(chatMsg, "md5");
                String URL = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + MD5 + "/0?term=2";
                new Handler(Looper.getMainLooper()).post(() -> EmoPanel.PreSavePicToList(URL, MD5, mContext));
            }
        });
        XPBridge.HookAfter(m[3], param -> {
            if (IsEnable) {
                Object arr = param.getResult();
                Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
                System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "QT保存");
                MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
                Array.set(ret, 0, MenuItem);

                param.setResult(ret);
            }
        });
        XPBridge.HookBefore(m[4], param -> {
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
        });

        XPBridge.HookAfter(m[5], param -> {
            if (IsEnable) {
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
            }


        });

        XPBridge.HookAfter(m[6], param -> {
            if (IsEnable) {
                View button = MField.GetRoundField(param.thisObject, param.thisObject.getClass(), ImageButton.class, 1);
                if (button != null) {
                    button.setOnLongClickListener(v -> {
                        EmoPanel.createShow(v.getContext());
                        return true;
                    });
                }
            }

        });

        XPBridge.HookAfter(m[7], param -> {
            if (IsEnable) {
                Object arr = param.getResult();
                Object ret = Array.newInstance(arr.getClass().getComponentType(), Array.getLength(arr) + 1);
                System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                Object MenuItem = MClass.NewInstance(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"), 3100, "QT保存");
                MField.SetField(MenuItem, "c", Integer.MAX_VALUE - 1);
                Array.set(ret, 0, MenuItem);

                param.setResult(ret);
            }
        });
        XPBridge.HookBefore(m[8], param -> {
            int InvokeID = (int) param.args[0];
            Context mContext = (Context) param.args[1];
            Object chatMsg = param.args[2];
            if (InvokeID == 3100) {
                Object mMarkFaceMessage = MField.GetField(chatMsg, "mMarkFaceMessage");
                String LocalPath = DecodeForEncPic.decodeGifForLocalPath(MField.GetField(mMarkFaceMessage, "dwTabID"), MField.GetField(mMarkFaceMessage, "sbufID"));
                new Handler(Looper.getMainLooper()).post(() -> EmoPanel.PreSavePicToList(LocalPath, DataUtils.getFileMD5(new File(LocalPath)), mContext));
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
        Method[] methods = getMethod();
        for (Method m : methods) {
            if (m == null) return false;
        }
        return true;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) {
            HookLoader.CallHookStart(HookInjectEmoTabView.class.getName());
        }
    }

    @Override
    public void ListItemClick(Context context) {

    }

    public Method[] getMethod() {
        Method[] m = new Method[9];
        m[0] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout",
                "a", void.class, new Class[]{MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie")});

        m[1] = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder"), "a");
        m[2] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder", "a", void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")});

        m[3] = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder"), "a");
        m[4] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder", "a", void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")});


        m[5] = MMethod.FindMethod("com.tencent.mobileqq.guild.chatpie.helper.GuildInputBarCommonComponent", "b", void.class, new Class[0]);
        m[6] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.SimpleUIAIOHelper", "a", void.class, new Class[0]);

        m[7] = QQReflect.GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MarketFaceItemBuilder"), "a");
        m[8] = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MarketFaceItemBuilder", "a", void.class, new Class[]{
                int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")});

        return m;
    }
}
