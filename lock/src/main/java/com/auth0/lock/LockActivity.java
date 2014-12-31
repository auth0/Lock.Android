package com.auth0.lock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;

import com.auth0.core.Application;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.event.AlertDialogEvent;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.event.ResetPasswordEvent;
import com.auth0.lock.event.SocialAuthenticationRequestEvent;
import com.auth0.lock.fragment.LoadingFragment;
import com.auth0.lock.identity.IdentityProvider;
import com.auth0.lock.provider.BusProvider;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboFragmentActivity;


public class LockActivity extends RoboFragmentActivity {

    public static final String AUTHENTICATION_ACTION = "Lock.Authentication";

    @Inject BusProvider provider;
    @Inject LockFragmentBuilder builder;
    @Inject Lock lock;

    private Application application;
    private ProgressDialog progressDialog;
    private IdentityProvider identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_lock);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoadingFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.provider.getBus().register(this);
        lock.resetAllProviders();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.provider.getBus().unregister(this);
        lock.resetAllProviders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Uri uri = getIntent().getData();
        Log.v(LockActivity.class.getName(), "Resuming activity with data " + uri);
        if (identity != null) {
            boolean valid = identity.authorize(this, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, RESULT_OK, getIntent());
            if (!valid) {
                dismissProgressDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getIntent().setData(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(LockActivity.class.getName(), "Received new Intent with URI " + intent.getData());
        identity = lock.getDefaultProvider();
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        identity = null;
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LockActivity.class.getName(), "Child activity result obtained");
        identity.authorize(this, requestCode, resultCode, data);
    }

    @Subscribe public void onApplicationLoaded(Application application) {
        Log.d(LockActivity.class.getName(), "Application configuration loaded for id " + application.getId());
        builder.setApplication(application);
        this.application = application;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, builder.root())
                .commit();
    }

    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Token token = event.getToken();
        Log.i(LockActivity.class.getName(), "Authenticated user " + profile.getName());
        Intent result = new Intent(AUTHENTICATION_ACTION);
        result.putExtra("profile", profile);
        result.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(result);
        dismissProgressDialog();
        finish();
    }

    @Subscribe public void onResetPassword(ResetPasswordEvent event) {
        Log.d(LockActivity.class.getName(), "Changed password");
        showAlertDialog(event);
        getSupportFragmentManager().popBackStack();
    }

    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(LockActivity.class.getName(), "Failed to authenticate user", error.getThrowable());
        if (identity != null) {
            identity.clearSession();
        }
        dismissProgressDialog();
        showAlertDialog(error);
    }

    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        Log.v(LockActivity.class.getName(), "About to handle navigation " + event);
        if (NavigationEvent.BACK.equals(event)) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        Fragment fragment = null;
        switch (event) {
            case SIGN_UP:
                fragment = builder.signUp();
                break;
            case RESET_PASSWORD:
                fragment = builder.resetPassword();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack(event.name())
                    .commit();
        }
    }

    @Subscribe public void onSocialAuthentication(SocialAuthenticationRequestEvent event) {
        Log.v(LockActivity.class.getName(), "About to authenticate with service " + event.getServiceName());
        identity = lock.providerForName(event.getServiceName());
        identity.start(this, event, application);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void showAlertDialog(AlertDialogEvent event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(event.getTitle(this))
                .setMessage(event.getMessage(this))
                .setPositiveButton(R.string.ok_btn_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

}
