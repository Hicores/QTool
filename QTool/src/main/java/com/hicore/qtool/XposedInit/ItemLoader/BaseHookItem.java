package com.hicore.qtool.XposedInit.ItemLoader;

public abstract class BaseHookItem {
    private boolean isLoad = false;
    private boolean isTryLoad = false;
    public abstract String getTag();
    public abstract boolean startHook();
    public abstract boolean isEnable();
    public abstract boolean check();
    public String getErrorInfo(){
        return getTag()+"未继承getErrorInfo";
    }


    public final boolean isLoaded(){return isLoad;}
    public final void setTryLoad(){isTryLoad = true;}
    public final boolean isTryLoad(){return isTryLoad;}
    public final void setLoad(boolean load){isLoad = load;}
}
