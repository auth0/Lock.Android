package com.auth0.lock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.event.AlertDialogEvent;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.event.ResetPasswordEvent;
import com.auth0.lock.event.SocialAuthenticationEvent;
import com.auth0.lock.fragment.LoadingFragment;
import com.auth0.lock.provider.BusProvider;
import com.auth0.lock.web.CallbackParser;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import java.util.Map;

import roboguice.activity.RoboFragmentActivity;


public class LockActivity extends RoboFragmentActivity {

    @Inject BusProvider provider;
    @Inject LockFragmentBuilder builder;
    @Inject CallbackParser parser;

    private Application application;
    private ProgressDialog progressDialog;

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
        final Uri uri = getIntent().getData();
        Log.v(LockActivity.class.getName(), "Resuming activity with data " + uri);
        if (uri != null) {
            final Map<String, String> values = parser.getValuesFromUri(uri);
            if (values.containsKey("error")) {
                final int message = "access_denied".equalsIgnoreCase(values.get("error")) ? R.string.social_access_denied_message : R.string.social_error_message;
                final AuthenticationError error = new AuthenticationError(R.string.social_error_title, message);
                provider.getBus().post(error);
                dismissProgressDialog();
            }
            dismissProgressDialog(); //Remove Later
        } else {
            dismissProgressDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.provider.getBus().unregister(this);
        getIntent().setData(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(LockActivity.class.getName(), "Received new Intent with URI " + intent.getData());
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
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
        Intent result = new Intent();
        result.putExtra("profile", profile);
        result.putExtra("token", token);
        setResult(RESULT_OK, result);
        finish();
    }

    @Subscribe public void onResetPassword(ResetPasswordEvent event) {
        Log.d(LockActivity.class.getName(), "Changed password");
        showAlertDialog(event);
        getSupportFragmentManager().popBackStack();
    }

    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(LockActivity.class.getName(), "Failed to authenticate user", error.getThrowable());
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

    @Subscribe public void onSocialAuthentication(SocialAuthenticationEvent event) {
        Log.v(LockActivity.class.getName(), "About to authenticate with service " + event.getServiceName());
        final Uri url = event.getAuthenticationUri(application);
        final Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
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
