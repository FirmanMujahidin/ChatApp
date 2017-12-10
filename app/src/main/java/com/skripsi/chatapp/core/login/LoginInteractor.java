package com.skripsi.chatapp.core.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.skripsi.chatapp.core.users.add.AddUserInteractor;
import com.skripsi.chatapp.utils.Authenticator;
import com.skripsi.chatapp.utils.Constants;
import com.skripsi.chatapp.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

/**
 * Author: firman mujahidin
 * Created on: 1/11/2017 , 11:05 PM
 * Project: Chatapp
 */

public class LoginInteractor extends AppCompatActivity implements LoginContract.Interactor {
    private LoginContract.OnLoginListener mOnLoginListener;

    public LoginInteractor(LoginContract.OnLoginListener onLoginListener) {
        this.mOnLoginListener = onLoginListener;
    }

    @Override
    public void performFirebaseLogin(final Activity activity, final String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "performFirebaseLogin:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a messageFrom to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            mOnLoginListener.onSuccess(task.getResult().toString());
                            updateFirebaseToken(task.getResult().getUser().getUid(),
                                    new SharedPrefUtil(activity.getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN, null));
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference mostafa = ref.child("users").child(task.getResult().getUser().getUid());
                            final String uid = task.getResult().getUser().getUid();
                            mostafa.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String email = dataSnapshot.child("email").getValue(String.class);
                                    String rsaPublicKkey = dataSnapshot.child("rsaPublicKey").getValue(String.class);
                                    String rsaPrivateKey = dataSnapshot.child("rsaPrivateKey").getValue(String.class);
                                    String pushToken = dataSnapshot.child("pushToken").getValue(String.class);

                                    //do what you want with the email
                                    Account account = new Account(email, Authenticator.ACCOUNT_TYPE);
                                    AccountManager accountManager = AccountManager.get(activity);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("publickey_rsa",rsaPublicKkey);
                                    bundle.putString("privatekey_rsa",rsaPrivateKey);
                                    bundle.putString("email",email);
                                    bundle.putString("pushToken",pushToken);

                                    accountManager.addAccountExplicitly(account, null, bundle);
                                    accountManager.setAuthToken(account, Authenticator.ACCOUNT_TYPE, uid);
                                    System.out.println("TOKEN => " + accountManager.peekAuthToken(account, Authenticator.ACCOUNT_TYPE));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            mOnLoginListener.onFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(uid)
                .child(Constants.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }
}
