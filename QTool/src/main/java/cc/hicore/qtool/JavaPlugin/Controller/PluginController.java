package cc.hicore.qtool.JavaPlugin.Controller;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import bsh.BshMethod;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.Utils.FileUtils;
import cc.hicore.Utils.NameUtils;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.JavaPlugin.ListForm.JavaPluginAct;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQSessionUtils;

public class PluginController {
    private static HashMap<String, PluginInfo> runningInfo = new HashMap<>();

    //脚本菜单信息
    public static class ItemInfo {
        public int itemType;
        public String ItemName;
        public String ItemID;
        public String CallbackName;
    }

    //添加一个菜单项目到脚本信息中
    public static String AddItem(String PluginVerifyID, String ItemName, String Callback, int type) {
        PluginInfo info = runningInfo.get(PluginVerifyID);
        if (info != null) {
            String ID = NameUtils.getRandomString(32);
            ItemInfo NewInfo = new ItemInfo();
            NewInfo.CallbackName = Callback;
            NewInfo.ItemID = ID;
            NewInfo.itemType = type;
            NewInfo.ItemName = ItemName;
            info.ItemFunctions.put(ID, NewInfo);
            return ID;
        }
        return null;
    }

    //添加一个菜单显示回调到脚本运行环境中
    public static void setItemClickFunctionName(String PluginVerifyID, String Callback) {
        PluginInfo info = runningInfo.get(PluginVerifyID);
        if (info != null) {
            info.ItemClickFunctionName = Callback;
        }
    }

    //移除一个脚本菜单项目
    public static void RemoveItem(String PluginVerifyID, String ItemID) {
        PluginInfo info = runningInfo.get(PluginVerifyID);
        if (info != null) {
            info.ItemFunctions.remove(ItemID);
        }
    }

    //这里负责处理被脚本使用 load 加载的项目
    public static void loadExtra(String PluginVerifyID, String path) {
        File f = new File(path);
        if (!f.exists()) {
            Utils.ShowToast("调用load加载的:" + path + "不存在");
            return;
        }
        String fileContent = FileUtils.ReadFileString(f);
        try {
            LoadInner(fileContent, path, PluginVerifyID);
        } catch (Exception e) {
            throw new RuntimeException("load " + path + " error", e);
        }
    }

    //通过脚本ID判断脚本是否正在运行
    public static boolean IsRunning(String PluginID) {
        for (String VerifyID : runningInfo.keySet()) {
            PluginInfo info = runningInfo.get(VerifyID);
            if (PluginID.equals(info.PluginID)) return info.IsRunning;
        }
        return false;
    }

    //通过脚本ID判断脚本是否加载中
    public static boolean IsLoading(String PluginID) {
        for (String VerifyID : runningInfo.keySet()) {
            PluginInfo info = runningInfo.get(VerifyID);
            if (PluginID.equals(info.PluginID)) return info.IsLoading;
        }
        return false;
    }

    //在脚本环境中扫描指定脚本ID的插件信息
    public static PluginInfo SearchInfoFromID(String PluginID) {
        for (String VerifyID : runningInfo.keySet()) {
            PluginInfo info = runningInfo.get(VerifyID);
            if (PluginID.equals(info.PluginID)) return info;
        }
        return null;
    }

    //插件被从脚本列表点击加载时调用的方法进行加载
    public static boolean LoadOnce(PluginInfo info) {
        try {
            if (IsRunning(info.PluginID)) {
                Utils.ShowToast("已经有相同ID的脚本在加载了,请修改ID再重试");
                return false;
            }
            info.PluginVerifyID = NameUtils.getRandomString(32);
            info.Instance = new Interpreter();
            info.Instance.setClassLoader(HookEnv.mLoader);
            info.IsLoading = true;
            info.IsBlackMode = PluginSetController.IsBlackMode(info.PluginID);
            info.ListStr = PluginSetController.getModeList(info.PluginID);
            File mainJava = new File(info.LocalPath, "main.java");
            if (!mainJava.exists()) {
                Utils.ShowToast("当前脚本目录不存在 main.java 文件,无法加载,脚本:" + info.PluginName);
                return false;
            }
            String fileContent = FileUtils.ReadFileString(mainJava);
            runningInfo.put(info.PluginVerifyID, info);
            LoadFirst(info);
            LoadInner(fileContent, mainJava.getAbsolutePath(), info.PluginVerifyID);
            info.IsRunning = true;
            info.IsLoading = false;
            JavaPluginAct.NotifyLoadSuccess(info.PluginID);
            return true;
        } catch (Throwable th) {
            Utils.ShowToast("脚本 " + info.PluginName + " 加载错误,已停止执行:\n" + Log.getStackTraceString(th));
            forceEnd(info);
            PluginErrorOutput.Print(info.LocalPath, Log.getStackTraceString(th));

            return false;
        }
    }

    //实时更新脚本使用群聊黑白名单的状态
    public static void reportToUpdateList(String PluginID, Boolean IsBlack, ArrayList<String> list) {
        PluginInfo info = SearchInfoFromID(PluginID);
        if (info != null) {
            if (IsBlack != null) {
                info.IsBlackMode = IsBlack;
            }

            if (list != null) {
                info.ListStr = list;
            }

        }
    }

    //安全地调用脚本onUnload来终止插件
    public static void endPlugin(String PluginID) {
        PluginInfo Info = SearchInfoFromID(PluginID);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> InvokeToPlugin(Info.Instance, "onUnload"));
        int count = 0;
        while (count < 100) {
            count++;
            if (future.isDone()) {
                break;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        forceEnd(Info);

    }

    //最后清扫脚本,直接清空脚本运行环境
    //如果这时脚本还在线程在运行,会直接抛出异常被终止
    private static void forceEnd(PluginInfo info) {
        if (info != null) {
            info.IsRunning = false;
            info.Instance.getNameSpace().clear();
            runningInfo.remove(info.PluginVerifyID);
        }

    }

    //这里负责清理脚本不支持的泛型和注解
    private static String checkAndRemoveNode(String Content) {
        //TODO 这里需要添加内容清理脚本中的 @xxx 注解和 <>括号
        return Content;
    }

    //脚本环境首次初始化,注册对脚本开放的接口
    private static void LoadFirst(PluginInfo info) throws Exception {
        NameSpace space = info.Instance.getNameSpace();
        PluginMethod env = new PluginMethod(info);
        info.ItemFunctions.clear();
        space.setMethod("sendMsg", new BshMethod(PluginMethod.class.getMethod("sendMsg", String.class, String.class, String.class), env));
        space.setMethod("sendPic", new BshMethod(PluginMethod.class.getMethod("sendPic", String.class, String.class, String.class), env));
        space.setMethod("sendCard", new BshMethod(PluginMethod.class.getMethod("sendCard", String.class, String.class, String.class), env));
        space.setMethod("sendShake", new BshMethod(PluginMethod.class.getMethod("sendShake", String.class), env));
        space.setMethod("sendShow", new BshMethod(PluginMethod.class.getMethod("sendShow", String.class, String.class, int.class), env));
        space.setMethod("sendVoice", new BshMethod(PluginMethod.class.getMethod("sendVoice", String.class, String.class, String.class), env));
        space.setMethod("sendTip", new BshMethod(PluginMethod.class.getMethod("sendTip", Object.class, String.class), env));
        space.setMethod("sendReply", new BshMethod(PluginMethod.class.getMethod("sendReply", String.class, Object.class, String.class), env));
        space.setMethod("Pai", new BshMethod(PluginMethod.class.getMethod("Pai", String.class, String.class), env));
        space.setMethod("sendLike", new BshMethod(PluginMethod.class.getMethod("sendLike", String.class, int.class), env));
        space.setMethod("sendAntEmo", new BshMethod(PluginMethod.class.getMethod("sendAntEmo", String.class, String.class, int.class), env));

        space.setMethod("getGroupList", new BshMethod(PluginMethod.class.getMethod("getGroupList"), env));
        space.setMethod("getGroupMemberList", new BshMethod(PluginMethod.class.getMethod("getGroupMemberList", String.class), env));
        space.setMethod("getForbiddenList", new BshMethod(PluginMethod.class.getMethod("getForbiddenList", String.class), env));

        space.setMethod("GetChatType", new BshMethod(PluginMethod.class.getMethod("getChatType"), env));
        space.setMethod("GetGroupUin", new BshMethod(PluginMethod.class.getMethod("getGroupUin"), env));
        space.setMethod("GetFriendUin", new BshMethod(PluginMethod.class.getMethod("getFriendUin"), env));

        space.setMethod("getSkey", new BshMethod(PluginMethod.class.getMethod("getSkey"), env));
        space.setMethod("getPskey", new BshMethod(PluginMethod.class.getMethod("getPskey", String.class), env));
        space.setMethod("getSuperkey", new BshMethod(PluginMethod.class.getMethod("getSuperkey"), env));
        space.setMethod("getPT4Token", new BshMethod(PluginMethod.class.getMethod("getPT4Token", String.class), env));
        space.setMethod("getBKN", new BshMethod(PluginMethod.class.getMethod("getBKN"), env));
        space.setMethod("getGTK", new BshMethod(PluginMethod.class.getMethod("getGTK", String.class), env));

        space.setMethod("setCard", new BshMethod(PluginMethod.class.getMethod("setCard", String.class, String.class, String.class), env));
        space.setMethod("setTitle", new BshMethod(PluginMethod.class.getMethod("setTitle", String.class, String.class, String.class), env));
        space.setMethod("revokeMsg", new BshMethod(PluginMethod.class.getMethod("revokeMsg", Object.class), env));
        space.setMethod("Forbidden", new BshMethod(PluginMethod.class.getMethod("Forbidden", String.class, String.class, int.class), env));
        space.setMethod("Kick", new BshMethod(PluginMethod.class.getMethod("Kick", String.class, String.class, boolean.class), env));

        space.setMethod("AddItem", new BshMethod(PluginMethod.class.getMethod("AddItem", String.class, String.class), env));
        space.setMethod("AddItem", new BshMethod(PluginMethod.class.getMethod("AddItem", String.class, String.class, String.class), env));
        space.setMethod("AddUserItem", new BshMethod(PluginMethod.class.getMethod("AddUserItem", String.class, String.class), env));
        space.setMethod("RemoveItem", new BshMethod(PluginMethod.class.getMethod("RemoveItem", String.class, String.class), env));
        space.setMethod("RemoveItem", new BshMethod(PluginMethod.class.getMethod("RemoveItem", String.class), env));
        space.setMethod("RemoveUserItem", new BshMethod(PluginMethod.class.getMethod("RemoveUserItem", String.class), env));
        space.setMethod("setItemCallback", new BshMethod(PluginMethod.class.getMethod("setItemCallback", String.class), env));

        space.setMethod("putString", new BshMethod(PluginMethod.class.getMethod("putString", String.class, String.class, String.class), env));
        space.setMethod("putInt", new BshMethod(PluginMethod.class.getMethod("putInt", String.class, String.class, int.class), env));
        space.setMethod("putBoolean", new BshMethod(PluginMethod.class.getMethod("putBoolean", String.class, String.class, boolean.class), env));
        space.setMethod("putLong", new BshMethod(PluginMethod.class.getMethod("putLong", String.class, String.class, long.class), env));
        space.setMethod("getString", new BshMethod(PluginMethod.class.getMethod("getString", String.class, String.class), env));
        space.setMethod("getInt", new BshMethod(PluginMethod.class.getMethod("getInt", String.class, String.class, int.class), env));
        space.setMethod("getBoolean", new BshMethod(PluginMethod.class.getMethod("getBoolean", String.class, String.class, boolean.class), env));
        space.setMethod("getLong", new BshMethod(PluginMethod.class.getMethod("getLong", String.class, String.class, long.class), env));


        space.setMethod("Toast", new BshMethod(PluginMethod.class.getMethod("Toast", Object.class), env));
        space.setMethod("GetActivity", new BshMethod(PluginMethod.class.getMethod("GetActivity"), env));
        space.setMethod("load", new BshMethod(PluginMethod.class.getMethod("load", String.class), env));
        space.setMethod("setFlag", new BshMethod(PluginMethod.class.getMethod("setFlag", String.class), env));
        space.setMethod("IncludeFile", new BshMethod(PluginMethod.class.getMethod("IncludeFile", String.class), env));
        space.setMethod("HandleRequest", new BshMethod(PluginMethod.class.getMethod("HandlerRequest", Object.class, boolean.class, String.class, boolean.class), env));
        space.setMethod("getFileUrl", new BshMethod(PluginMethod.class.getMethod("getFileDirectUrl", Object.class), env));

        space.setMethod("sendPackMsg",new BshMethod(PluginMethod.class.getMethod("sendPackMsg", String.class, String.class, String.class, String.class, String.class),env));

    }

    //这里负责真正加载脚本代码,会把在内存中的脚本代码进行加载并注入必要的环境信息
    public static void LoadInner(String FileContent, String LocalPath, String BandVerifyID) throws Exception {
        String LoadContent = checkAndRemoveNode(FileContent);
        PluginInfo info = runningInfo.get(BandVerifyID);
        Interpreter instance = info.Instance;
        instance.set("context", HookEnv.AppContext);
        instance.set("PluginID", BandVerifyID);
        instance.set("SDKVer", 11);
        instance.set("loader", HookEnv.mLoader);
        instance.set("AppPath", info.LocalPath);
        instance.set("MyUin", QQEnvUtils.getCurrentUin());

        instance.eval(LoadContent, LocalPath);
    }

    //判断该脚本是否有权利被调用回调
    public static void checkAndInvoke(String GroupUin, String MethodName, Object... param) {
        for (String VerifyID : runningInfo.keySet()) {
            PluginInfo info = runningInfo.get(VerifyID);
            if (info.IsRunning && !info.IsLoading) {
                if (info.IsAvailable(GroupUin)) {
                    try {
                        InvokeToPlugin(info.Instance, MethodName, param);
                    } catch (RuntimeException runtime) {
                        Throwable cause = runtime.getCause();
                        PluginErrorOutput.Print(info.LocalPath, Log.getStackTraceString(cause));
                    }

                }
            }
        }
    }

    //判断该脚本是否有权利被调用回调
    public static Object checkAndInvokeNotNull(String GroupUin, String MethodName, Object... param) {
        for (String VerifyID : runningInfo.keySet()) {
            PluginInfo info = runningInfo.get(VerifyID);
            if (info.IsRunning && !info.IsLoading) {
                if (info.IsAvailable(GroupUin)) {
                    try {
                        Object ret = InvokeToPlugin(info.Instance, MethodName, param);
                        if (ret instanceof Primitive) {
                            Primitive primitive = (Primitive) ret;
                            if (primitive.getValue() == null) return null;
                        }
                        if (ret != null) return ret;
                    } catch (RuntimeException runtime) {
                        Throwable cause = runtime.getCause();
                        PluginErrorOutput.Print(info.LocalPath, Log.getStackTraceString(cause));
                    }

                }
            }
        }
        return null;
    }

    //真正调用脚本中指定名字的回调
    private static Object InvokeToPlugin(Interpreter Instance, String MethodName, Object... param) {
        try {
            NameSpace space = Instance.getNameSpace();
            Class<?>[] clz = new Class[param.length];
            for (int i = 0; i < param.length; i++) clz[i] = param[i].getClass();

            Loop:
            for (BshMethod method : space.getMethods()) {
                if (method.getName().equals(MethodName)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == clz.length) {
                        for (int i = 0; i < param.length; i++) {
                            if (!MClass.CheckClass(params[i], clz[i])) continue Loop;
                        }

                        return method.invoke(param, Instance);
                    }
                }

            }
            return null;
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    //在收到消息时调用对应的回调
    public static void onMessage(PluginInfo.EarlyInfo early, PluginInfo.MessageData data) {
        checkAndInvoke(early.GroupUin, "Callback_OnRawMsg", data.msg);
        if (data.MessageType != 0) {
            checkAndInvoke(early.GroupUin, "onMsg", data);
        }
    }

    //获取某一个群聊中可以被显示的菜单信息
    public static HashMap<String, PluginInfo> checkHasAvailMenu(String Uin, boolean isTroop) {
        HashMap<String, PluginInfo> avail = new HashMap<>();
        for (String key : runningInfo.keySet()) {
            PluginInfo info = runningInfo.get(key);
            if (info != null && (info.ItemFunctions.size() != 0 || !TextUtils.isEmpty(info.ItemClickFunctionName)) && !info.IsLoading && info.IsRunning) {
                if (info.IsAvailable(Uin)) {
                    avail.put(key, info);
                }
            }
        }
        return avail;
    }

    //调用菜单预加载回调
    public static void InvokeToPreCheckItem(Object Session, String Name, PluginInfo info) {
        if (Session != null) {
            String GroupUin = QQSessionUtils.getGroupUin();
            String UserUin = QQSessionUtils.getFriendUin();
            int type = QQSessionUtils.getSessionID();
            try {
                if (type == 1) {
                    InvokeToPlugin(info.Instance, Name, GroupUin, "", 1);
                    InvokeToPlugin(info.Instance, Name, GroupUin);
                } else if (type == 0) {
                    InvokeToPlugin(info.Instance, Name, "", UserUin, 2);
                } else if (type == 1000) {
                    InvokeToPlugin(info.Instance, Name, GroupUin, UserUin, 2);
                } else if (type == 10014) {
                    InvokeToPlugin(info.Instance, Name, QQSessionUtils.getGuildID(Session) + "&" + QQSessionUtils.getChannelID(Session), "", 1);
                    InvokeToPlugin(info.Instance, Name, QQSessionUtils.getGuildID(Session) + "&" + QQSessionUtils.getChannelID(Session));
                }
            } catch (Exception e) {
                Utils.ShowToast("调用到 " + Name + " 时发生错误");
                PluginErrorOutput.Print(info.LocalPath, "脚本 " + info.PluginName + " 回调 " + Name + " 执行过程中发生错误:\n" + Log.getStackTraceString(e));
            }
        }
    }

    //传递菜单点击事件
    public static void InvokeToPluginItem(Object Session, String Name, PluginInfo info) {
        if (Session != null) {
            String GroupUin = QQSessionUtils.getGroupUin(Session);
            String UserUin = QQSessionUtils.getFriendUin(Session);
            int type = QQSessionUtils.getSessionID(Session);
            try {
                if (type == 1) {
                    InvokeToPlugin(info.Instance, Name, GroupUin, "", 1);
                    InvokeToPlugin(info.Instance, Name, GroupUin);
                } else if (type == 0) {
                    InvokeToPlugin(info.Instance, Name, "", UserUin, 2);
                } else if (type == 1000) {
                    InvokeToPlugin(info.Instance, Name, GroupUin, UserUin, 2);
                } else if (type == 10014) {
                    InvokeToPlugin(info.Instance, Name, QQSessionUtils.getGuildID(Session) + "&" + QQSessionUtils.getChannelID(Session), "", 1);
                }
            } catch (Exception e) {
                Utils.ShowToast("调用到 " + Name + " 时发生错误");
                PluginErrorOutput.Print(info.LocalPath, "脚本 " + info.PluginName + " 回调 " + Name + " 执行过程中发生错误:\n" + Log.getStackTraceString(e));
            }
        }
    }

    public static void WaitForgetMsgInvoke(Object msg) {
        String clzName = msg.getClass().getName();
        if (clzName.contains("MessageForText") || clzName.contains("MessageForLongTextMsg")) {
            try {
                String msgContain = MField.GetField(msg, "msg", String.class);
                int istroop = MField.GetField(msg, "istroop", int.class);
                if (istroop == 1) {
                    String friendUin = MField.GetField(msg, "frienduin", String.class);
                    Object change = checkAndInvokeNotNull(friendUin, "getMsg", msgContain, friendUin, 1);
                    if (change instanceof String) {
                        String textChange = (String) change;
                        MField.SetField(msg, "msg", textChange);
                        MMethod.CallMethodNoParam(msg, "prewrite", void.class);
                    }
                } else if (istroop == 0 || istroop == 1000) {
                    String friendUin = MField.GetField(msg, "frienduin", String.class);
                    Object change = checkAndInvokeNotNull("", "getMsg", msgContain, friendUin, 1);
                    if (change instanceof String) {
                        String textChange = (String) change;
                        MField.SetField(msg, "msg", textChange);
                        MMethod.CallMethodNoParam(msg, "prewrite", void.class);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
