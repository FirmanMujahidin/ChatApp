package com.skripsi.chatapp.utils;

/**
 * Created by firma on 26-Nov-17.
 */

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.skripsi.chatapp.ui.activities.LoginActivity;


/**
 * Created by samuelerwardi on 10/15/16.
 */
public class Authenticator  extends AbstractAccountAuthenticator{
    private Context mContext;
    private final Handler handler = new Handler();

    public static final String ACCOUNT_TYPE = "com.skripsi.chatapp";

    public Authenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    public static String getUsername(Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        return accounts.length > 0 ? accounts[0].name : null;
    }

    public static String getToken(Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if(accounts.length > 0) {
            String token = accountManager.peekAuthToken(accounts[0], Authenticator.ACCOUNT_TYPE);
            return token;
        }
        return null;
    }
    public static String getBundle(Context context, String key){
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        return accounts.length > 0 ? accountManager.getUserData(accounts[0], key) : null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s1, String[] strings, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(mContext);
        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        if(accounts.length == 0) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
            options.putParcelable(AccountManager.KEY_INTENT, intent);
        }else{
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "There can't be more than an account", Toast.LENGTH_LONG).show();
                }
            });
        }
        return options;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
//        // Extract the username and password from the Account Manager, and ask
//        // the server for an appropriate AuthToken.
//        final AccountManager am = AccountManager.get(mContext);
//
//        String authToken = am.peekAuthToken(account, authTokenType);
//
//        // Lets give another try to authenticate the user
//        if (TextUtils.isEmpty(authToken)) {
//            final String password = am.getPassword(account);
//            if (password != null) {
////                authToken = sServerAuthenticate.userSignIn(account.name, password, authTokenType);
//            }
//        }
//
//        // If we get an authToken - we return it
//        if (!TextUtils.isEmpty(authToken)) {
//            final Bundle result = new Bundle();
//            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
//            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
//            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
//            return result;
//        }
//
//        // If we get here, then we couldn't access the user's password - so we
//        // need to re-prompt them for their credentials. We do that by creating
//        // an intent to display our AuthenticatorActivity.
//        final Intent intent = new Intent(mContext, LoginActivity.class);
////        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
////        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
////        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}