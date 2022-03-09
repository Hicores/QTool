package com.hicore.qtool.XposedInit.ItemLoader;

public abstract class BaseHookItem {
    private boolean isLoad = false;
    private boolean isTryLoad = false;
    public String getTag(){
        return this.getClass().getName();
    }
    public boolean startHook(){
        return false;
    }
    public boolean isEnable(){return false;}
    public String getErrorInfo(){
        return getTag()+"未继承getErrorInfo";
    }
    public boolean check(){
        return false;
    }
    public final boolean isLoaded(){return isLoad;}
    public final void setTryLoad(){isTryLoad = true;}
    public final boolean isTryLoad(){return isTryLoad;}
    public final void setLoad(boolean load){isLoad = load;}
}
