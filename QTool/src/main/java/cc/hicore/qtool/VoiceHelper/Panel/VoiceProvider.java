package cc.hicore.qtool.VoiceHelper.Panel;

import java.util.ArrayList;

import cc.hicore.qtool.HookEnv;

public class VoiceProvider {
    public static final String PROVIDER_LOCAL_FILE = "LocalFile::";
    public static final String PROVIDER_LOCAL_SEARCH = " LOCAL_SEARCH::";
    private String Path;

    private VoiceProvider() {

    }

    public static VoiceProvider getNewInstance(String Path) {
        VoiceProvider provider = new VoiceProvider();
        provider.Path = Path;
        return provider;
    }

    public VoiceProvider getParent() {
        if (!Path.contains("/")) return this;
        return getNewInstance(Path.substring(0, Path.lastIndexOf("/")));
    }

    public VoiceProvider getChild(String Name) {
        return getNewInstance(Path + "/" + Name);
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
        }
        return new ArrayList<>();
    }

    public static class FileInfo {
        public String Name;
        public int type;
        public String Path;
    }
}
