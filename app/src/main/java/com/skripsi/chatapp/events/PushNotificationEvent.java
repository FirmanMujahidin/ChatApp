package com.skripsi.chatapp.events;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 8:05 PM
 * Project: Chatapp
 */
public class PushNotificationEvent {
    private String title;
    private String message;
    private String username;
    private String uid;
    private String fcmToken;
    private String firebaseToken;
    public PushNotificationEvent() {
    }

    public PushNotificationEvent(String title, String message, String username, String uid, String fcmToken, String firebaseToken) {
        this.title = title;
        this.message = message;
        this.username = username;
        this.uid = uid;
        this.fcmToken = fcmToken;
        this.firebaseToken = firebaseToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}