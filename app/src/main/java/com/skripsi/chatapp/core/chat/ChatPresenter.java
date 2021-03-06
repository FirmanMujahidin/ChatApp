package com.skripsi.chatapp.core.chat;

import android.content.Context;

import com.skripsi.chatapp.models.Chat;

/**
 * Author: firman mujahidin
 * Created on: 9/11/2017 , 10:05 PM
 * Project: Chatapp
 */

public class ChatPresenter implements ChatContract.Presenter, ChatContract.OnSendMessageListener,
        ChatContract.OnGetMessagesListener {
    private ChatContract.View mView;
    private ChatInteractor mChatInteractor;

    public ChatPresenter(ChatContract.View view) {
        this.mView = view;
        mChatInteractor = new ChatInteractor(this, this);
    }

    @Override
    public void sendMessage(Context context, Chat chat, String receiverFirebaseToken, String receiverRsaPublicKey, String receiverRsaPrivateKey) {
        mChatInteractor.sendMessageToFirebaseUser(context, chat, receiverFirebaseToken, receiverRsaPublicKey, receiverRsaPrivateKey);
    }

    @Override
    public void getMessage(Context context, String senderUid, String receiverUid) {
        mChatInteractor.getMessageFromFirebaseUser(context, senderUid, receiverUid);
    }

    @Override
    public void onSendMessageSuccess() {
        mView.onSendMessageSuccess();
    }

    @Override
    public void onSendMessageFailure(String message) {
        mView.onSendMessageFailure(message);
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        mView.onGetMessagesSuccess(chat);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        mView.onGetMessagesFailure(message);
    }



}
