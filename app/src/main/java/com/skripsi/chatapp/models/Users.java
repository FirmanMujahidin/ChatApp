package com.skripsi.chatapp.models;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 10:15 PM
 * Project: Chatapp
 */
public class Users {

    private String emailId;
    private String lastMessage;
    private int notifCount;

    public String getEmailId(){ return emailId; }

    public void setEmailId(){ this.emailId = emailId; }

    public String getLastMessage(){ return lastMessage; }

    public void setLastMessage(){ this.lastMessage = lastMessage; }

    public int getNotifCount(){ return notifCount; }

    public void setNotifCount(int notifCount){ this.notifCount = notifCount; }
}
