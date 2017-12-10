package com.skripsi.chatapp.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skripsi.chatapp.R;
import com.skripsi.chatapp.core.registration.RegisterContract;
import com.skripsi.chatapp.core.registration.RegisterPresenter;
import com.skripsi.chatapp.core.users.add.AddUserContract;
import com.skripsi.chatapp.core.users.add.AddUserPresenter;
import com.skripsi.chatapp.javalib.FileEncryptionManager;
import com.skripsi.chatapp.models.User;
import com.skripsi.chatapp.ui.activities.LoginActivity;
import com.skripsi.chatapp.ui.activities.UserListingActivity;
import com.google.firebase.auth.FirebaseUser;
import com.skripsi.chatapp.utils.Constants;
import com.skripsi.chatapp.utils.SharedPrefUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener, RegisterContract.View, AddUserContract.View {
    private static final String TAG = RegisterFragment.class.getSimpleName();

    private RegisterPresenter mRegisterPresenter;
    private AddUserPresenter mAddUserPresenter;

    public EditText mETxtName, mETxtEmail, mETxtPassword, mETxtConfirmPassword;
    public String name;
    private Button mBtnRegister;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private ProgressDialog mProgressDialog;

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_register, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mETxtName = (EditText) view.findViewById(R.id.edit_text_name);
        mETxtEmail = (EditText) view.findViewById(R.id.edit_text_email_id);
        mETxtPassword = (EditText) view.findViewById(R.id.edit_text_password);
        mETxtConfirmPassword = (EditText) view.findViewById(R.id.edit_text_password_confirm);
        mBtnRegister = (Button) view.findViewById(R.id.button_register);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mRegisterPresenter = new RegisterPresenter(this);
        mAddUserPresenter = new AddUserPresenter(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        switch (viewId) {
            case R.id.button_register:
                onRegister(view);
                break;
        }
    }

    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (password.length() > 0 || password.equals(";")) && matcher.find();
    }

    private void onRegister(View view) {
        name = mETxtName.getText().toString();
        String emailId = mETxtEmail.getText().toString();
        String password = mETxtPassword.getText().toString();
        String confirmPassword = mETxtConfirmPassword.getText().toString();
        if (name.matches("")) {
            mETxtName.setError("Your Name Empty");
        }
        if (emailId.matches("")) {
            mETxtEmail.setError("Your Email Empty");
        }
        if (password.matches("")) {
            mETxtPassword.setError("Your Password Empty");
        }
        if (confirmPassword.matches("")) {
            mETxtConfirmPassword.setError("Your Password Empty");
        }
        if (validate(emailId,password)){
            mRegisterPresenter.register(getActivity(), emailId, password);
            mProgressDialog.show();
        }
        else {
            Toast.makeText(getActivity(), "Invalid email or password or confirm not valid", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRegistrationSuccess(FirebaseUser firebaseUser) {
        mProgressDialog.setMessage(getString(R.string.adding_user_to_db));
        Toast.makeText(getActivity(), "Registration Successful!", Toast.LENGTH_SHORT).show();
        FileEncryptionManager mFileEncryptionManager = FileEncryptionManager.getInstance();
        try {
            mFileEncryptionManager.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String rsaPublicKey = mFileEncryptionManager.getPublicKey();
        final String rsaPrivateKey = mFileEncryptionManager.getPrivateKey();
        final String pushToken= FirebaseInstanceId.getInstance().getToken();

        User user = new User(firebaseUser.getUid(),
                this.name,
                firebaseUser.getEmail(),
                pushToken,
                new SharedPrefUtil(getContext()).getString(Constants.ARG_FIREBASE_TOKEN),
                rsaPublicKey,
                rsaPrivateKey);

        mAddUserPresenter.addUser(getActivity().getApplicationContext(), firebaseUser, user);
    }

    @Override
    public void onRegistrationFailure(String message) {
        mProgressDialog.dismiss();
        mProgressDialog.setMessage(getString(R.string.please_wait));
        Log.e(TAG, "onRegistrationFailure: " + message);
        Toast.makeText(getActivity(), "Registration failed!+\n" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAddUserSuccess(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this.getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAddUserFailure(String message) {
        mProgressDialog.dismiss();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
