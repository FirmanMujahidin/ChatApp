package com.skripsi.chatapp.models;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 11:10 PM
 * Project: Chatapp
 */
public class User {
    public String uid;
    public String name;
    public String email;
    public String pushToken;
    public String firebaseToken;
    public String rsaPublicKey;
    public String rsaPrivateKey;

    public User(){

    }

    public User(String uid, String name, String email,String pushToken, String firebaseToken, String rsaPublicKey, String rsaPrivateKey){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.pushToken = pushToken;
        this.firebaseToken = firebaseToken;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }
//  public User(String uid, String email, String firebaseToken) {
//    }
}
