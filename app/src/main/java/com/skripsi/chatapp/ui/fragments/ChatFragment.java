package com.skripsi.chatapp.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skripsi.chatapp.R;
import com.skripsi.chatapp.core.chat.ChatContract;
import com.skripsi.chatapp.core.chat.ChatPresenter;
import com.skripsi.chatapp.events.PushNotificationEvent;
import com.skripsi.chatapp.models.Chat;
import com.skripsi.chatapp.ui.adapters.ChatRecyclerAdapter;
import com.skripsi.chatapp.utils.Authenticator;
import com.skripsi.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class ChatFragment extends Fragment implements ChatContract.View, TextView.OnEditorActionListener{
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    private ImageView mBtnSend;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    public static ChatFragment newInstance(String receiver,
                                           String receiverUid,
                                           String firebaseToken,
                                           String receiverRsaPublicKey,
                                           String receverRsaPrivateKey) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        args.putString(Constants.ARG_RECEIVER_RSAPUBLICKEY, receiverRsaPublicKey);
        args.putString(Constants.ARG_RECEIVER_RSAPRIVATEKEY, receverRsaPrivateKey);

        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }
    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.edit_text_message);
        mBtnSend = (ImageView) view.findViewById(R.id.btn_send);

        mETxtMessage.setOnKeyListener(keyListener);

        mBtnSend.setOnClickListener(clickListener);

        mETxtMessage.addTextChangedListener(watcher1);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mETxtMessage.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getArguments().getString(Constants.ARG_RECEIVER_UID));
    }



    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            /*if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                if (v == mETxtMessage) {
                    sendMessage();
                }
            }
            return true;*/
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage();
                        return true;
                    default:
                        break;
                }
            }
            return true;
        }
    };


    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = mETxtMessage.getText().toString();
            if (message.matches("")) {
                mETxtMessage.setError("Message is Empty");
            }else if (message.matches(" ")){
                mETxtMessage.setError("Message is Empty");
            }else if (message.matches("  ")){
                mETxtMessage.setError("Message is Empty");
            }else if (message.matches("   ")){
                mETxtMessage.setError("Message is Empty");
            }else if (message.matches("    ")){
                mETxtMessage.setError("Message is Empty");
            }else if (message.matches("     ")){
                mETxtMessage.setError("Message is Empty");
            }else if(v==mBtnSend) {
                sendMessage();
            }else {
                mETxtMessage.setText("");
            }
        }


    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (mETxtMessage.getText().toString().equals("")) {

            } else {
                mBtnSend.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.toString().length() <= 0){
                mBtnSend.setImageResource(R.drawable.ic_chat_send);
            }else{
                mBtnSend.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };

  /*  @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String message = mETxtMessage.getText().toString();
        if (message.matches("")) {
            mETxtMessage.setError("Message is Empty");
        }
        else if (message.matches(" ")){
            mETxtMessage.setError("Message is Empty");
        }
        else if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }*/

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }

    private void sendMessage() {
        String message = mETxtMessage.getText().toString();
        String messageTo = mETxtMessage.getText().toString();


        String receiverRsaPublicKey  = getArguments().getString(Constants.ARG_RECEIVER_RSAPUBLICKEY);
        String receiverRsaPrivateKey = getArguments().getString(Constants.ARG_RECEIVER_RSAPRIVATEKEY);

        String name = Authenticator.getBundle(getContext(),Constants.ARG_NAME);
        String receiver = getArguments().getString(Constants.ARG_RECEIVER);
        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(name,
                sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                messageTo,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getActivity().getApplicationContext(),
                chat,
                receiverFirebaseToken,
                receiverRsaPublicKey,
                receiverRsaPrivateKey);
    }

    @Override
    public void onSendMessageSuccess() {
            mETxtMessage.setText("");
            Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
    }


}
