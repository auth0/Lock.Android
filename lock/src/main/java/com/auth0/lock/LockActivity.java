package com.auth0.lock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.LoadingFragment;
import com.auth0.lock.provider.BusProvider;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboFragmentActivity;


public class LockActivity extends RoboFragmentActivity {

    @Inject private BusProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoadingFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.provider.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.provider.getBus().unregister(this);
    }

    @Subscribe public void onApplicationLoaded(Application application) {
        Log.d(LockActivity.class.getName(), "Application configuration loaded for id " + application.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DatabaseLoginFragment())
                .commit();
    }

    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Token token = event.getToken();
        Log.i(LockActivity.class.getName(), "Authenticated user " + profile.getName());
        Intent result = new Intent();
        result.putExtra("profile", profile);
        result.putExtra("token", token);
        setResult(RESULT_OK, result);
        finish();
    }

    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(LockActivity.class.getName(), "Failed to authenticate user", error.getThrowable());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(error.getTitle(this))
                .setMessage(error.getMessage(this))
                .setPositiveButton(R.string.ok_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.show();
    }

    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        Log.v(LockActivity.class.getName(), "About to display " + event);
    }
}
