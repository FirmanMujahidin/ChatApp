package com.skripsi.chatapp.core.users.add;


import android.content.Context;
import android.support.annotation.NonNull;
import com.skripsi.chatapp.R;
import com.skripsi.chatapp.javalib.FileEncryptionManager;
import com.skripsi.chatapp.models.User;
import com.skripsi.chatapp.utils.Constants;
import com.skripsi.chatapp.utils.SharedPrefUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddUserInteractor implements AddUserContract.Interactor {
    private AddUserContract.OnUserDatabaseListener mOnUserDatabaseListener;

    public AddUserInteractor(AddUserContract.OnUserDatabaseListener onUserDatabaseListener) {
        this.mOnUserDatabaseListener = onUserDatabaseListener;
    }

    @Override
    public void addUserToDatabase(final Context context, final FirebaseUser firebaseUser) {
        FileEncryptionManager mFileEncryptionManager = FileEncryptionManager.getInstance();
        try {
            mFileEncryptionManager.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String rsaPublicKey = mFileEncryptionManager.getPublicKey();
        final String rsaPrivateKey = mFileEncryptionManager.getPrivateKey();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                rsaPublicKey,
                rsaPrivateKey);
        database.child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
                        } else {
                            mOnUserDatabaseListener.onFailure(context.getString(R.string.user_unable_to_add));
                        }
                    }
                });
    }
}