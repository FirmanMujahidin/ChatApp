package com.skripsi.chatapp.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.skripsi.chatapp.R;
import com.skripsi.chatapp.base.BaseActivity;
import com.skripsi.chatapp.core.logout.LogoutContract;
import com.skripsi.chatapp.core.logout.LogoutInteractor;
import com.skripsi.chatapp.core.logout.LogoutPresenter;
import com.skripsi.chatapp.ui.adapters.UserListingPagerAdapter;
import com.skripsi.chatapp.utils.Authenticator;

public class UserListingActivity extends BaseActivity implements LogoutContract.View {
    private Toolbar mToolbar;
    private TabLayout mTabLayoutUserListing;
    private ViewPager mViewPagerUserListing;

    private LogoutPresenter mLogoutPresenter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserListingActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int flags) {
        Intent intent = new Intent(context, UserListingActivity.class);
        intent.setFlags(flags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind(R.layout.activity_user_listing);
        initToolbar("ChatApp");
        bindViews();
        init();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTabLayoutUserListing = (TabLayout) findViewById(R.id.tab_layout_user_listing);
        mViewPagerUserListing = (ViewPager) findViewById(R.id.view_pager_user_listing);
    }

    private void init() {
        // set the toolbar
        setSupportActionBar(mToolbar);

        // set the view pager adapter
        UserListingPagerAdapter userListingPagerAdapter = new UserListingPagerAdapter(getSupportFragmentManager());
        mViewPagerUserListing.setAdapter(userListingPagerAdapter);

        // attach tab layout with view pager
        mTabLayoutUserListing.setupWithViewPager(mViewPagerUserListing);

        mLogoutPresenter = new LogoutPresenter(this);

        String firebaseInstanceId= FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(getApplicationContext(), "Token : " + firebaseInstanceId, Toast.LENGTH_LONG).show();
        Log.d("TAG ", "Token : " + firebaseInstanceId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_listing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AccountManager accountManager = AccountManager.get(UserListingActivity.this);
                        Account[] accounts = accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
                        if (accounts.length > 0) {
                            Account accountToRemove = accounts[0];
                            accountManager.removeAccount(accountToRemove, null, null);
                        }
                        Intent intent = new Intent(UserListingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                        mLogoutPresenter.logout();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onLogoutSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        LoginActivity.startIntent(this,
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onLogoutFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
