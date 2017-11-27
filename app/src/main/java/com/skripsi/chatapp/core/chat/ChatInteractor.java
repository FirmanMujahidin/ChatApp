package com.skripsi.chatapp.core.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.skripsi.chatapp.fcm.FcmNotificationBuilder;
import com.skripsi.chatapp.javalib.Base64Utils;
import com.skripsi.chatapp.javalib.FileEncryptionManager;
import com.skripsi.chatapp.models.Chat;
import com.skripsi.chatapp.utils.Authenticator;
import com.skripsi.chatapp.utils.Constants;
import com.skripsi.chatapp.utils.SharedPrefUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 10:15 PM
 * Project: Chatapp
 */

public class ChatInteractor extends AppCompatActivity implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";
    FileEncryptionManager mFileEncryptionManager = FileEncryptionManager.getInstance();

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
    public void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverFirebaseToken) {
        File saveEncryPath, saveDecryPath;
        String publicKey, privateKey;

        File sdcard = Environment.getExternalStorageDirectory();
        saveEncryPath = new File(sdcard.getPath()+"/diapers_encry.txt");
        saveDecryPath = new File(sdcard.getPath()+"/diapers_decry.txt");

        try {
            String publickeyRsaFrom = Authenticator.getBundle(context, "publickey_rsa");
            String privatekeyRsaFrom = Authenticator.getBundle(context, "privatekey_rsa");
            Log.d(TAG, "asdf: "+publickeyRsaFrom);

            mFileEncryptionManager.setRSAKey(publickeyRsaFrom, privatekeyRsaFrom, true);

            byte[] encryptByte = mFileEncryptionManager.encryptByPublicKey(chat.message.getBytes());
            String afterencrypt = Base64Utils.encode(encryptByte);
//            byte[] decryptByte = mFileEncryptionManager.decryptByPrivateKey(Base64Utils.decode(afterencrypt));
//            Log.d(TAG, "message_enc: " + afterencrypt);
//            Log.d(TAG, "message_dec: "+ new String(decryptByte));
            chat.setMessage(afterencrypt);

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
                    Log.d(TAG, "room1");
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.d(TAG, "room2");
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else {
                    Log.d(TAG, "room3");
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    getMessageFromFirebaseUser(context, chat.senderUid, chat.receiverUid);
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.sender,
                        chat.message,
                        chat.senderUid,
                        new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                        receiverFirebaseToken);
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
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
    public void getMessageFromFirebaseUser(final Context context, String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // POSISI SEBAGAI PENGIRIM
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            try {
                                Chat chat = dataSnapshot.getValue(Chat.class);
                                String publickeyRsaFrom = Authenticator.getBundle(context, "publickey_rsa");
                                String privatekeyRsaFrom = Authenticator.getBundle(context, "privatekey_rsa");
                                mFileEncryptionManager.setRSAKey(publickeyRsaFrom, privatekeyRsaFrom, true);
                                byte[] decryptByte = mFileEncryptionManager.decryptByPrivateKey(Base64Utils.decode(chat.getMessage()));
                                Log.d(TAG, "onDataChangeasd: "+ new String(decryptByte));
                                chat.setMessage(new String(decryptByte));
                                mOnGetMessagesListener.onGetMessagesSuccess(chat);
                            } catch (Exception e) {
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
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            Log.d(TAG, "jomblos");
                            try {
                                String publickeyRsaFrom = Authenticator.getBundle(context, "publickey_rsa");
                                String privatekeyRsaFrom = Authenticator.getBundle(context, "privatekey_rsa");
                                mFileEncryptionManager.setRSAKey(publickeyRsaFrom, privatekeyRsaFrom, true);
                                byte[] decryptByte = mFileEncryptionManager.decryptByPrivateKey(Base64Utils.decode(chat.getMessage()));
                                Log.d(TAG, "onDataChangeasdRoom2: "+ new String(decryptByte));
                                chat.setMessage(new String(decryptByte));
                            }catch (Exception e){
                                e.getMessage();
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
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }
}
