package com.hicore.ConfigUtils;

import android.text.TextUtils;

import com.hicore.Utils.DataUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class ConfigCore_Json implements ConfigCore{
    /*
    采用JSON的方式来储存信息,由于是多读少写的情况,应该不会有多大的性能损失
     */

    @Override
    public void removeKey(String PathName, String Key) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            MapJson.remove(Key);
            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }
    }

    @Override
    public boolean getBoolean(String PathName, String Key,boolean Def) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            return MapJson.optBoolean(Key,Def);
        }catch (Exception e){
            return Def;
        }
    }

    @Override
    public void setBoolean(String PathName, String Key, boolean Value) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            MapJson.put(Key,Value);
            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }
    }

    @Override
    public String getString(String PathName, String Key,String Def) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            return MapJson.optString(Key,Def);
        }catch (Exception e){
            return Def;
        }
    }

    @Override
    public void setString(String PathName, String Key, String Value) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            MapJson.put(Key,Value);
            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }

    }

    @Override
    public int getInt(String PathName, String Key,int Def) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            return MapJson.optInt(Key,Def);
        }catch (Exception e){
            return Def;
        }
    }

    @Override
    public void setInt(String PathName, String Key, int Value) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            MapJson.put(Key,Value);
            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }

    }

    @Override
    public long getLong(String PathName, String Key,long Def) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            return MapJson.optLong(Key,Def);
        }catch (Exception e){
            return Def;
        }
    }

    @Override
    public void setLong(String PathName, String Key, long Value) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            MapJson.put(Key,Value);
            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }
    }

    @Override
    public List<String> getList(String PathName, String Key,boolean isCreate) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            JSONArray Data = MapJson.optJSONArray(Key);
            if (Data == null)return isCreate ? new ArrayList<>() :null;
            ArrayList<String> newArr = new ArrayList<>();
            for (int i=0;i<Data.length();i++)newArr.add(Data.getString(i));

            return newArr;
        }catch (Exception e){
            return isCreate ? new ArrayList<>() :null;
        }
    }

    @Override
    public void setList(String PathName, String Key, List<String> Value) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);

            JSONArray NewArr = new JSONArray();
            for (String s : Value)NewArr.put(s);
            MapJson.put(Key,NewArr);

            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }
    }

    @Override
    public byte[] getBytes(String PathName, String Key) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            String Data = MapJson.optString(Key);
            return DataUtils.HexToByteArray(Data);
        }catch (Exception e){
            return new byte[0];
        }
    }

    @Override
    public void setBytes(String PathName, String Key, byte[] Value) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);

            MapJson.put(Key,DataUtils.ByteArrayToHex(Value));
            ConfigUtils_MapFile.WriteFile(PathName,MapJson.toString());
        }catch (Exception e){
        }
    }

    @Override
    public String[] getKeys(String PathName) {
        try{
            String PathData = ConfigUtils_MapFile.ReadFile(PathName);
            JSONObject MapJson = TextUtils.isEmpty(PathData) ? new JSONObject() : new JSONObject(PathData);
            ArrayList<String> keys = new ArrayList<>();
            Iterator<String> its = MapJson.keys();
            while (its.hasNext()){
                keys.add(its.next());
            }
            return keys.toArray(new String[0]);
        }catch (Exception e){
            return new String[0];
        }
    }

}
