package com.hicore.ConfigUtils;

public class HConfig {
    public static ConfigCore getInstance(){
        return new ConfigCore_Json();
    }

}
