package cc.hicore.qtool.VoiceHelper.Panel;

import android.text.TextUtils;

import java.util.ArrayList;

import cc.hicore.qtool.HookEnv;
import cc.hicore.qtool.VoiceHelper.OnlineHelper.OnlineBundleHelper;

public class VoiceProvider {
    public static final String PROVIDER_LOCAL_FILE = "LocalFile::";
    public static final String PROVIDER_ONLINE = "OnlineFile::";
    public static final String PROVIDER_LOCAL_SEARCH = " LOCAL_SEARCH::";
    public static final String PROVIDER_ONLINE_SEARCH = "OnlineSearch::";

    public static class FileInfo {
        public String Name;
        public int type;
        public String Path;
    }

    private VoiceProvider() {

    }

    private String Path;

    public VoiceProvider getParent() {
        if (!Path.contains("/")) return this;
        return getNewInstance(Path.substring(0, Path.lastIndexOf("/")));
    }

    public VoiceProvider getChild(String Name) {
        return getNewInstance(Path + "/" + Name);
    }

    public static VoiceProvider getNewInstance(String Path) {
        VoiceProvider provider = new VoiceProvider();
        provider.Path = Path;
        return provider;
    }

    public String getPath() {
        return Path;
    }

    public ArrayList<FileInfo> getList() {
        if (Path.startsWith(PROVIDER_LOCAL_FILE)) {
            String truePath = HookEnv.ExtraDataPath + "Voice/" + Path.substring(PROVIDER_LOCAL_FILE.length());
            return LocalVoiceSearchHelper.searchForPath(truePath, Path.length() == PROVIDER_LOCAL_FILE.length());
        } else if (Path.startsWith(PROVIDER_LOCAL_SEARCH)) {
            String searchName = Path.substring(PROVIDER_LOCAL_SEARCH.length());
            return LocalVoiceSearchHelper.searchForName(HookEnv.ExtraDataPath + "Voice/", searchName);
        } else if (Path.startsWith(PROVIDER_ONLINE)) {
            String ControlCode = Path.substring(PROVIDER_ONLINE.length());
            if (TextUtils.isEmpty(ControlCode)) return OnlineBundleHelper.getAllBundle();
            else return OnlineBundleHelper.getBundleContent(ControlCode);
        } else if (Path.startsWith(PROVIDER_ONLINE_SEARCH)) {
            String ControlCode = Path.substring(PROVIDER_ONLINE_SEARCH.length());
            return OnlineBundleHelper.searchForName(ControlCode);
        }
        return new ArrayList<>();
    }
}
