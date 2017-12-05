package com.skripsi.chatapp.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.skripsi.chatapp.FirebaseChatMainApp;
import com.skripsi.chatapp.R;
import com.skripsi.chatapp.base.BaseActivity;
import com.skripsi.chatapp.ui.fragments.ChatFragment;
import com.skripsi.chatapp.utils.Constants;

import java.util.ArrayList;

public class ChatActivity extends BaseActivity {
    private Toolbar mToolbar;

    public static void startActivity(Context context,
                                     String receiver,
                                     String receiverUid,
                                     String rsaPublicKeyTo,
                                     String rsaPrivateKeyTo,
                                     String firebaseToken) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.putExtra("receiverRsaPublicKey", rsaPublicKeyTo);
        intent.putExtra("receiverRsaPrivateKey", rsaPrivateKeyTo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind(R.layout.activity_chat);
        initToolbar(true);
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);

        // set toolbar title
        mToolbar.setTitle(getIntent().getExtras().getString(Constants.ARG_RECEIVER));

        // set the register screen fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Constants.ARG_FIREBASE_TOKEN),
                        getIntent().getExtras().getString("receiverRsaPublicKey"),
                        getIntent().getExtras().getString("receiverRsaPrivateKey")),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false);
    }
}
