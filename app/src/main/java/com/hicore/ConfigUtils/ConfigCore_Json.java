package com.hicore.ConfigUtils;

import android.os.Binder;

import com.hicore.LogUtils.LogUtils;
import com.hicore.qtool.XposedInit.HookEnv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ConfigCore_Json <V> implements ConfigCore{
    /*
    采用JSON的方式来储存信息,通过广播事件来进行多进程通信更新,只由主进程写入数据
    由于是多读少写的情况,应该不会有多大的性能损失
     */
    private HashMap<String,HashMap<String,V>> map = new HashMap<>();
    private final boolean IsMainProcess;
    private ServerSocket server;
    private Socket client;
    public ConfigCore_Json(){
        IsMainProcess = HookEnv.ProcessName.equals("com.tencent.mobileqq");
        if (IsMainProcess) InitSocketServer();
        else InitSocketClient();
    }
    @Override
    public boolean getBoolean(String PathName, String Key,boolean Def) {
        CheckPathName(PathName);
        HashMap<String,V> childMap = map.get(PathName);
        if (childMap == null)return Def;
        if (!childMap.containsKey(Key))return Def;
        Object obj = childMap.get(Key);
        if (obj instanceof Boolean)return (Boolean) obj;
        return Def;
    }

    @Override
    public void setBoolean(String PathName, String Key, boolean Value) {
        CheckPathName(PathName);
    }

    @Override
    public String getString(String PathName, String Key,String Def) {
        CheckPathName(PathName);
        return null;
    }

    @Override
    public void setString(String PathName, String Key, String Value) {
        CheckPathName(PathName);

    }

    @Override
    public int getInt(String PathName, String Key,int Def) {
        CheckPathName(PathName);
        return 0;
    }

    @Override
    public void setInt(String PathName, String Key, int Value) {
        CheckPathName(PathName);

    }

    @Override
    public long getLong(String PathName, String Key,long Def) {
        CheckPathName(PathName);
        return 0;
    }

    @Override
    public void setLong(String PathName, String Key, long Value) {
        CheckPathName(PathName);
    }

    @Override
    public List getList(String PathName, String Key) {
        CheckPathName(PathName);
        return null;
    }

    @Override
    public void setList(String PathName, String Key, List Value) {
        CheckPathName(PathName);
    }

    @Override
    public byte[] getBytes(String PathName, String Key) {
        CheckPathName(PathName);
        return new byte[0];
    }

    @Override
    public void setBytes(String PathName, String Key, byte[] Value) {
        CheckPathName(PathName);

    }
    private void InitSocketServer(){
        try {
            server = new ServerSocket(33331);
            while (true){
                Socket socket = server.accept();
                SocketSelected(socket);
            }
        } catch (IOException e) {
            LogUtils.error("ConfigCore_JSON","Can't create socket in port 33331, configs may not be updated in extra process.");
        }
    }
    public void CheckPathName(String PathName){

    }
    private void SocketSelected(Socket toClientSocket){

    }
    private void InitSocketClient(){

    }


    private void NotifyConfigUpdate(String PathName){

    }

}
