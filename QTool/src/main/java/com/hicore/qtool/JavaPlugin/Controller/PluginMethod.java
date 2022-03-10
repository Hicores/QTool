package com.hicore.qtool.JavaPlugin.Controller;

import android.app.Activity;

import com.hicore.Utils.Utils;
import com.hicore.qtool.QQManager.QQGroupManager;
import com.hicore.qtool.QQMessage.QQMsgSendUtils;

public class PluginMethod {
    private PluginInfo info_;
    public PluginMethod(PluginInfo cacheInfo){
        info_ = cacheInfo;
    }
    public void sendMsg(String GroupUin,String UserUin,String Content){
        if (info_.IsAvailable(GroupUin)){
            QQMsgSendUtils.decodeAndSendMsg(GroupUin,UserUin,Content);
        }
    }
    public void sendPic(String GroupUin,String UserUin,String Path){
        if (info_.IsAvailable(GroupUin)){
            QQMsgSendUtils.decodeAndSendMsg(GroupUin,UserUin,"[PicUrl="+Path+"]");
        }
    }
    public void sendCard(String GroupUin,String UserUin,String CardText){

    }
    public void sendShake(String GroupUin){

    }
    public void sendShow(String GroupUin,String Path,int type){

    }
    public void sendTip(Object source,String text){

    }
    public void sendVoice(String GroupUin,String UserUin,String Path){

    }
    public void sendReply(String GroupUin,Object target,String text){

    }
    public void getGroupList(){

    }
    public void getGroupMemberList(String GroupUin){

    }
    public void getForbiddenList(String GroupUin){

    }
    public void setCard(String GroupUin,String UserUin,String Name){

    }
    public void setTitle(String GroupUin,String UserUin,String title){

    }
    public void AddItem(String PluginID,String ItemName,String CallbackName){
        AddItem(ItemName, CallbackName);
    }
    public void AddItem(String ItemName,String CallbackName){

    }
    public void AddUserItem(String ItemName,String CallbackName){

    }
    public void RemoveUserItem(String ItemID){

    }
    public void RemoveItem(String PluginID,String ItemID){
        RemoveItem(ItemID);
    }
    public void RemoveItem(String ItemID){

    }
    public void Toast(Object obj){
        Utils.ShowToast(obj);
    }
    public void revokeMsg(Object msg){

    }
    public void Forbidden(String GroupUin,String UserUin,int time){
        if (info_.IsAvailable(GroupUin)) {
            QQGroupManager.Group_Mute(GroupUin,UserUin,time);
        }
    }
    public void Kick(String GroupUin,String UserUin,boolean isBlack){
        if (info_.IsAvailable(GroupUin)){
            QQGroupManager.Group_Kick(GroupUin,UserUin,isBlack);
        }
    }
    public Activity GetActivity(){
        return Utils.getTopActivity();
    }
    public void load(String Path){

    }
    public String getSkey(){
        return null;
    }
    public String getPskey(String Domain){
        return null;
    }
    public String getSuperkey(){
        return null;
    }
    public String getPT4Token(String Domain){
        return null;
    }


    public String getString(String key){
        return PluginStoreUtils.getString(info_.PluginID,key);
    }
    public void putString(String key,String value){
        PluginStoreUtils.putString(info_.PluginID,key,value);
    }
    public boolean getBoolean(String key,boolean def){
        return PluginStoreUtils.getBoolean(info_.PluginID,key,def);
    }
    public void putBoolean(String key,boolean value){
        PluginStoreUtils.putBoolean(info_.PluginID,key,value);
    }
    public int getInt(String key,int def){
        return PluginStoreUtils.getInt(info_.PluginID,key,def);
    }
    public void putInt(String key,int value){
        PluginStoreUtils.putInt(info_.PluginID,key,value);
    }
    public long getLong(String key,long def){
        return PluginStoreUtils.getLong(info_.PluginID,key,def);
    }
    public void putLong(String key,long value){
        PluginStoreUtils.putLong(info_.PluginID,key,value);
    }
    public void setFlag(String flag){
        //Nothing
    }
    public void IncludeFile(String path){
        //Nothing
    }
    public void getChatType(){

    }
    public void getGroupUin(){

    }
    public void getFriendUin(){

    }
    public void Pai(String GroupUin,String UserUin){

    }
    public void setItemCallback(String callbackName){

    }
    public void sendLike(String UserUin,int count){

    }
    public void sendAntEmo(String GroupUin,String UserUin,int servID){

    }
    public void HandlerRequest(Object request,boolean isAccept,String reason,boolean isBlack){

    }


}
