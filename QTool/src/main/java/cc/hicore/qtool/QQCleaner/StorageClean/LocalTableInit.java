package cc.hicore.qtool.QQCleaner.StorageClean;

import android.content.SharedPreferences;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.hicore.Utils.DataUtils;
import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQManager.QQGroupUtils;

public class LocalTableInit {
    private static HashMap<String,String> trooptables = new HashMap<>();
    private static boolean isInit;
    public static void initTable(){
        if (isInit)return;
        isInit = true;
        List<QQGroupUtils.GroupInfo> groupInfos = QQGroupUtils.Group_Get_List();
        for (QQGroupUtils.GroupInfo info : groupInfos){
            trooptables.put(DataUtils.getStrMD5(info.Uin),info.Uin);
        }
        List<QQEnvUtils.FriendInfo> friends = QQEnvUtils.getFriendList();
        for (QQEnvUtils.FriendInfo info : friends){
            trooptables.put(DataUtils.getStrMD5(info.Uin),info.Uin);
        }
        searchForLocal();
        searchForLocalFriend();
    }

    private static void searchForLocalFriend(){
        SharedPreferences share = HookEnv.AppContext.getSharedPreferences("qzone_sp_in_qq",0);
        Map<String,?> map = share.getAll();
        for (String key : map.keySet()){
            try{
                int indexStart = key.lastIndexOf("_");
                String uin = key.substring(indexStart+1);
                Long.parseLong(uin);
                trooptables.put(DataUtils.getStrMD5(uin),uin);
            }catch (Exception e){

            }
        }

        share = HookEnv.AppContext.getSharedPreferences("com.tencent.mobileqq_preferences",0);
        map = share.getAll();
        for (String key : map.keySet()){
            try{
                int indexStart = key.indexOf("last");
                if (indexStart == -1)indexStart = key.indexOf("next");
                if (indexStart == -1)continue;
                String uin = key.substring(0,indexStart);
                Long.parseLong(uin);
                trooptables.put(DataUtils.getStrMD5(uin),uin);
            }catch (Exception e){

            }
        }
    }


    private static void searchForLocal(){
        File f = new File(HookEnv.AppContext.getFilesDir().getParentFile()+"/shared_prefs");
        File[] fs = f.listFiles();
        if (fs != null){
            for (File file : fs){
                if (file.isFile()){
                    try{
                        String name = file.getName();
                        int indexLast = name.lastIndexOf(".");
                        int indexStart = name.lastIndexOf("_");

                        if (indexLast > indexStart){
                            String uin = name.substring(indexStart+1,indexLast);
                            Long.parseLong(uin);
                            trooptables.put(DataUtils.getStrMD5(uin),uin);
                        }
                    }catch (Exception e){

                    }
                }
            }
        }
    }

    public static String query(String md5){
        String uin = trooptables.get(md5.toUpperCase(Locale.ROOT));
        if (uin == null)return "";
        return uin;
    }
}
