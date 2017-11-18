package com.skripsi.chatapp.core.login;

import android.app.Activity;

/**
 * Author: firman mujahidin
 * Created on: 15/11/2017 , 9:05 AM
 * Project: Chatapp
 */

public interface LoginContract {
    interface View {
        void onLoginSuccess(String message);

        void onLoginFailure(String message);
    }

    interface Presenter {
        void login(Activity activity, String email, String password);
    }

    interface Interactor {
        void performFirebaseLogin(Activity activity, String email, String password);
    }

    interface OnLoginListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
