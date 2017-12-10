package com.skripsi.chatapp.core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.skripsi.chatapp.models.User;


public interface AddUserContract {
    interface View {
        void onAddUserSuccess(String message);

        void onAddUserFailure(String message);
    }

    interface Presenter {
        void addUser(Context context, FirebaseUser firebaseUser, User user);
    }

    interface Interactor {
        void addUserToDatabase(Context context, FirebaseUser firebaseUser, User user);
    }

    interface OnUserDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
