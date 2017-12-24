package com.skripsi.chatapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skripsi.chatapp.R;
import com.skripsi.chatapp.models.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;



public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ChatRecyclerAdapter";
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private List<Chat> mChats;
    public static Context mContext;
    public ChatRecyclerAdapter(List<Chat> chats) {
        mChats = chats;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        String alphabet = chat.userName.substring(0, 1);


        myChatViewHolder.textNmaeView.setText(chat.userName);
        myChatViewHolder.txtChatMessage.setText(chat.messageFrom);
        myChatViewHolder.txtUserAlphabet.setText(alphabet);
        myChatViewHolder.textTimeMessage.setText(SIMPLE_DATE_FORMAT.format(chat.getTimestamp()));
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);
        String alphabet = chat.userName.substring(0, 1);

        otherChatViewHolder.textNameViewOther.setText(chat.userName);
        otherChatViewHolder.txtChatMessage.setText(chat.messageFrom);
        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
        otherChatViewHolder.textTimeMessage.setText(SIMPLE_DATE_FORMAT.format(chat.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet, textTimeMessage, textNmaeView;

        public MyChatViewHolder(View itemView) {
            super(itemView);

            textNmaeView = (TextView) itemView.findViewById(R.id.name_chat_reply_author);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            textTimeMessage = (TextView) itemView.findViewById(R.id.text_view_time_chat_message);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet, textTimeMessage, textNameViewOther;

        public OtherChatViewHolder(View itemView) {
            super(itemView);


            textNameViewOther = (TextView) itemView.findViewById(R.id.name_chat_reply_other);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
            textTimeMessage = (TextView) itemView.findViewById(R.id.text_view_time_chat_message);
        }
    }
}
