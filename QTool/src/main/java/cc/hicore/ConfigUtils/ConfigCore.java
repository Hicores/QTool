package cc.hicore.ConfigUtils;

import java.util.List;

public interface ConfigCore {
    void removeKey(String PathName, String Key);

    void removeAll(String PathName);

    boolean getBoolean(String PathName, String Key, boolean Def);

    void setBoolean(String PathName, String Key, boolean Value);

    String getString(String PathName, String Key, String Def);

    void setString(String PathName, String Key, String Value);

    int getInt(String PathName, String Key, int Def);

    void setInt(String PathName, String Key, int Value);

    long getLong(String PathName, String Key, long Def);

    void setLong(String PathName, String Key, long Value);

    List<String> getList(String PathName, String Key, boolean isCreate);

    void setList(String PathName, String Key, List<String> Value);

    byte[] getBytes(String PathName, String Key);

    void setBytes(String PathName, String Key, byte[] Value);

    String[] getKeys(String PathName);
}
