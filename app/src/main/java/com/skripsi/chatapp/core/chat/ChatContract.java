package com.skripsi.chatapp.core.chat;

import android.content.Context;

import com.skripsi.chatapp.models.Chat;



public interface ChatContract {
    interface View {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);

        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

    interface Presenter {
        void sendMessage(Context context, Chat chat, String receiverFirebaseToken, String receiverRsaPublicKey, String receiverRsaPrivateKey);

        void getMessage(Context context, String senderUid, String receiverUid);
    }

    interface Interactor {
        void sendMessageToFirebaseUser(Context context, Chat chat, String receiverFirebaseToken, String receiverRsaPublicKey, String receiverRsaPrivateKey);

        void getMessageFromFirebaseUser(Context context, String senderUid, String receiverUid);
    }

    interface OnSendMessageListener {
        void onSendMessageSuccess();

        void onSendMessageFailure(String message);
    }

    interface OnGetMessagesListener {
        void onGetMessagesSuccess(Chat chat);

        void onGetMessagesFailure(String message);
    }

}
