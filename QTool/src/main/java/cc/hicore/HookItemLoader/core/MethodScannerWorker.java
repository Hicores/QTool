package cc.hicore.HookItemLoader.core;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

import cc.hicore.ConfigUtils.GlobalConfig;
import cc.hicore.HookItemLoader.bridge.BaseFindMethodInfo;
import cc.hicore.HookItemLoader.bridge.BaseMethodInfo;
import cc.hicore.HookItemLoader.bridge.CommonMethodInfo;
import cc.hicore.Utils.Utils;
import cc.hicore.qtool.XposedInit.HostInfo;

public class MethodScannerWorker {
    public static class ScannerLink{
        public String ID;
        public BaseMethodInfo Info;
        public ArrayList<ScannerLink> LinkingID;
        public ScannerLink LinkToID;
    }
    public static boolean checkIsAvailable(){
        String QQVer = HostInfo.getVersion()+"."+HostInfo.getVerCode();
        if (GlobalConfig.Get_String("cacheQQVer").equals(QQVer)){
            return true;
        }
        for (CoreLoader.XPItemInfo info : CoreLoader.clzInstance.values()){
            if (info.isVersionAvailable && info.NeedMethodInfo != null){
                for (BaseMethodInfo methodInfo : info.NeedMethodInfo){
                    if (methodInfo instanceof CommonMethodInfo) continue;
                    return false;
                }
            }
        }
        return true;
    }
    private static final ArrayList<ScannerLink> rootNode = new ArrayList<>();

    private static void CollectLinkInfo(){
        ArrayList<BaseMethodInfo> allFindMethodInfo = new ArrayList<>();
        for (CoreLoader.XPItemInfo item : CoreLoader.clzInstance.values()){
            if (item.NeedMethodInfo != null){
                allFindMethodInfo.addAll(item.NeedMethodInfo);
            }
        }
        //编号查找的Link
        int restLink = 0;
        while (restLink != allFindMethodInfo.size()){
            allFindMethodInfo.removeIf(MethodScannerWorker::checkAndAddItemToTree);
        }
    }
    private static boolean checkAndAddItemToTree(BaseMethodInfo info){
        if (info instanceof CommonMethodInfo){
            ScannerLink newNode = new ScannerLink();
            newNode.ID = info.id;
            newNode.LinkingID = new ArrayList<>();
            newNode.Info = info;
            rootNode.add(newNode);
            return true;
        }
        if (info instanceof BaseFindMethodInfo){
            if (((BaseFindMethodInfo) info).LinkedToMethodID == null){
                ScannerLink newNode = new ScannerLink();
                newNode.ID = info.id;
                newNode.LinkingID = new ArrayList<>();
                newNode.Info = info;
                rootNode.add(newNode);
                return true;
            }else {
                String LinkedID = ((BaseFindMethodInfo) info).LinkedToMethodID;
                for (ScannerLink lnk : rootNode){
                    ScannerLink searchResult = searchNode(lnk,LinkedID);
                    if (searchResult != null){
                        ScannerLink newNode = new ScannerLink();
                        newNode.ID = info.id;
                        newNode.LinkingID = new ArrayList<>();
                        newNode.Info = info;
                        newNode.LinkToID = searchResult;

                        searchResult.LinkingID.add(newNode);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private static ScannerLink searchNode(ScannerLink link,String ID){
        if (link.ID.equals(ID))return link;
        if (link.LinkingID.size() > 0){
            for (ScannerLink childNode : link.LinkingID){
                ScannerLink result = searchNode(childNode,ID);
                if (result != null)return result;
            }
        }
        return null;
    }
    public static void doFindMethod(){
        CollectLinkInfo();
        Context context = Utils.getTopActivity();
    }
}
