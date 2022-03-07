package com.hicore.qtool.XPWork;

public abstract class BaseHookItem {
    public String getTag(){
        return this.getClass().getName();
    }
    public boolean startHook(){
        return false;
    }
    public boolean startDelayHook(){ return false;}
    public String getErrorInfo(){
        return getTag()+"未继承getErrorInfo";
    }
    public boolean check(){
        return false;
    }
    public boolean isLoaded(){return false;}
}
