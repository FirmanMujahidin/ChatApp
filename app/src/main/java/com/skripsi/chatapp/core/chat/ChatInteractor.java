package com.skripsi.chatapp.core.chat;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.scottyab.aescrypt.AESCrypt;
import com.skripsi.chatapp.fcm.FcmNotificationBuilder;
import com.skripsi.chatapp.models.Chat;
import com.skripsi.chatapp.utils.Authenticator;
import com.skripsi.chatapp.utils.Constants;
import com.skripsi.chatapp.utils.RSAUtil;
import com.skripsi.chatapp.utils.SharedPrefUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 10:15 PM
 * Project: Chatapp
 */

public class ChatInteractor extends AppCompatActivity implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";
    String messageRsa;
    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverFirebaseToken, final String receiverRsaPublicKey, final String receiverRsaPrivateKey) {
        File saveEncryPath, saveDecryPath;
        final String publicKey, privateKey;

        Log.d(TAG, "chatDebug"+ chat.getMessageFrom());
        Log.d(TAG, "chatDebug"+ chat.getMessageTo());
        Log.d(TAG, "paramsPub" + receiverRsaPublicKey);
        Log.d(TAG, "paramsPri" + receiverRsaPrivateKey);


        //ini implementai AES
    /*    String password = "password";
        String encryptedMsg = "WsZSUlTlZ+bbQBqrPFJpTZU/qNyOdozIaqNRH6trNLc=";
        String decrypteMsg = "Ini tes encrip aes";
        try {
            String messageAfterDecrypt = AESCrypt.decrypt(password, encryptedMsg);
            String massageDecrypt = AESCrypt.encrypt(password,decrypteMsg);

            Log.d(TAG, "AesDec : " + messageAfterDecrypt);
            Log.d(TAG, "AesEec : " + massageDecrypt);

        }catch (GeneralSecurityException e){
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }*/

        File sdcard = Environment.getExternalStorageDirectory();
        saveEncryPath = new File(sdcard.getPath()+"/diapers_encry.txt");
        saveDecryPath = new File(sdcard.getPath()+"/diapers_decry.txt");

        try {

            String publickeyRsaFrom = Authenticator.getBundle(context, "publickey_rsa");
            String privatekeyRsaFrom = Authenticator.getBundle(context, "privatekey_rsa");
//            String passwordkeyAesFrom = Authenticator.getBundle(context, "firebaseToken");
            String passwordkeyAesFrom = chat.senderUid;
            Log.d(TAG, "pubkeyFrom: "+publickeyRsaFrom);
            Log.d(TAG, "priKeyFrom: "+privatekeyRsaFrom);




            String rsaEncryptFrom = RSAUtil.encrypt(chat.getMessageFrom(), publickeyRsaFrom);
            String rsaEncryptTo = RSAUtil.encrypt(chat.getMessageFrom(), receiverRsaPublicKey);

            String aesEncryptFrom = AESCrypt.encrypt(passwordkeyAesFrom,rsaEncryptFrom);
            String aesEncryptTo = AESCrypt.encrypt(passwordkeyAesFrom,rsaEncryptTo);



            Log.d(TAG, "aesEncryptFrom : " + aesEncryptFrom);
            Log.d(TAG, "aesDec : " + AESCrypt.decrypt(passwordkeyAesFrom, aesEncryptFrom));
            Log.d(TAG, "passwordAes :  " + passwordkeyAesFrom);

            Log.d(TAG, "publickeyRsaFrom : " + rsaEncryptFrom);
            Log.d(TAG, "receiverRsaPublicKey : " + rsaEncryptTo);
            Log.d(TAG, "rsaUtilDec : " + RSAUtil.decrypt(rsaEncryptFrom, privatekeyRsaFrom));
            Log.d(TAG, "plaintext : " + chat.getMessageFrom());

            chat.setMessageFrom(aesEncryptFrom);
            chat.setMessageTo(aesEncryptTo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.d(TAG, "room1: " + room_type_1 + chat);
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.d(TAG, "room2: " + room_type_2 + chat);
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else {
                    Log.d(TAG, "room3" + chat);
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    getMessageFromFirebaseUser(context, chat.senderUid, chat.receiverUid);
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.sender,
                        chat.messageFrom,
                        chat.senderUid,
                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                        receiverFirebaseToken);
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send messageFrom: " + databaseError.getMessage());
            }
        });
    }

    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
                                                        FcmNotificationBuilder.initialize()
                                                                .title(username)
                                                                .message(message)
                                                                .username(username)
                                                                .uid(uid)
                                                                .firebaseToken(firebaseToken)
                                                                .receiverFirebaseToken(receiverFirebaseToken)
                                                                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(final Context context, final String senderUid, String receiverUid) {
        Log.d(TAG, "senderUid : " + senderUid);
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;
//        Log.d(TAG, "tokenFirebas" + getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN));
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // POSISI SEBAGAI PENGIRIM
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUserRoom1: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            try {
                                Chat chat = dataSnapshot.getValue(Chat.class);
                                String privatekeyRsaFrom = Authenticator.getBundle(context, "privatekey_rsa");
                                String passwordkeyAesFrom = chat.senderUid;
                                String email = Authenticator.getBundle(context, "email");
                                Log.d(TAG, "ifcondition : "+ (chat.receiver.equals(email)));
                                Log.d(TAG, "receiver" + chat.receiver);
                                Log.d(TAG, "auth " + email);

                                if (chat.receiver.equals(email)){
                                    messageRsa = chat.getMessageTo();

                                }
                                else{
                                    messageRsa = chat.getMessageFrom();
                                }

                                Log.d(TAG, "getMessagePrivKey : " +privatekeyRsaFrom);


                                String aesDecrypt = AESCrypt.decrypt(passwordkeyAesFrom, messageRsa);
                                String rsaDecrypt = RSAUtil.decrypt(aesDecrypt, privatekeyRsaFrom);


                                Log.d(TAG, "messageEnc : " + chat.getMessageFrom());
                                Log.d(TAG, "aesDecrypt_room1: "+ aesDecrypt);
                                Log.d(TAG, "rsaDecrypt_room1: "+ rsaDecrypt);
                                chat.setMessageFrom(rsaDecrypt);
                                mOnGetMessagesListener.onGetMessagesSuccess(chat);
                            } catch (Exception e) {
                                Log.d(TAG, "errorRoom1 : "+e.getMessage());
                                e.getMessage();
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get messageFrom: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUserRoom2: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            try {
                                String privatekeyRsaTo = Authenticator.getBundle(context, "privatekey_rsa");
                                String email = Authenticator.getBundle(context, "email");
                                String passwordkeyAsaFrom = chat.senderUid;
                                Log.d(TAG, "ifcondition : "+ (chat.receiver.equals(email)));
                                Log.d(TAG, "receiver" + chat.receiver);
                                Log.d(TAG, "auth " + email);
                                if (chat.receiver.equals(email)){
                                    messageRsa = chat.getMessageTo();
                                }
                                else{
                                    messageRsa = chat.getMessageFrom();
                                }

                                String aesDecrypt = AESCrypt.decrypt(passwordkeyAsaFrom,messageRsa);
                                String rsaDecrypt = RSAUtil.decrypt(aesDecrypt, privatekeyRsaTo);

                                Log.d(TAG, "messageEnc" + chat.getMessageFrom());
                                Log.d(TAG, "rsaDecrypt_room2: "+ rsaDecrypt);
                                Log.d(TAG, "aesDecryptFrom_room2: "+ aesDecrypt);
                                chat.setMessageFrom(rsaDecrypt);
                            }catch (Exception e){
                                Log.d(TAG, "errorRoom2"+e.getMessage());
                            }
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get messageFrom: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.d(TAG, "elseOtherRoom");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get messageFrom: " + databaseError.getMessage());
            }
        });
    }
}
