package com.hicore.qtool.XposedInit.ItemLoader;

public abstract class BaseHookItem {
    public BaseHookItem(){ }
    private boolean isLoad = false;
    private boolean isTryLoad = false;
    public String getTag(){
        return getClass().getSimpleName();
    }
    public abstract boolean startHook() throws Throwable;
    public abstract boolean isEnable();
    public abstract boolean check();
    public String getErrorInfo(){
        return null;
    }


    public final boolean isLoaded(){return isLoad;}
    public final void setTryLoad(){isTryLoad = true;}
    public final boolean isTryLoad(){return isTryLoad;}
    public final void setLoad(boolean load){isLoad = load;}
}
