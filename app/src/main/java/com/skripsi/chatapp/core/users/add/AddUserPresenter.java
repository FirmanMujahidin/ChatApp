package com.skripsi.chatapp.core.users.add;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.skripsi.chatapp.models.User;


public class AddUserPresenter implements AddUserContract.Presenter, AddUserContract.OnUserDatabaseListener {
    private AddUserContract.View mView;
    private AddUserInteractor mAddUserInteractor;

    public AddUserPresenter(AddUserContract.View view) {
        this.mView = view;
        mAddUserInteractor = new AddUserInteractor(this);
    }

    @Override
    public void addUser(Context context, FirebaseUser firebaseUser, User user) {
        mAddUserInteractor.addUserToDatabase(context, firebaseUser, user);
    }

    @Override
    public void onSuccess(String message) {
        mView.onAddUserSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mView.onAddUserFailure(message);
    }
}
