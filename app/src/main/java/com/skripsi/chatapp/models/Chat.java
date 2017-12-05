package com.skripsi.chatapp.models;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 11:05 PM
 * Project: Chatapp
 */
public class Chat  {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String messageFrom;
    public String messageTo;
    public long timestamp;

    public Chat(){

    }

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String messageFrom, String messageTo, long timestamp){
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
        this.timestamp = timestamp;
    }


    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }
}
