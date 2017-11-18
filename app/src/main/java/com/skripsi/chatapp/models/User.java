package com.skripsi.chatapp.models;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 11:10 PM
 * Project: Chatapp
 */
public class User {
    public String uid;
    public String email;
    public String firebaseToken;

    public User(){

    }

    public User(String uid, String email, String firebaseToken){
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }
//  public User(String uid, String email, String firebaseToken) {
//    }
}
