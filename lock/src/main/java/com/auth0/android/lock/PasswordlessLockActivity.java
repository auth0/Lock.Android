/*
 * LockPasswordlessActivity.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.auth0.Auth0Exception;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.views.LockProgress;
import com.auth0.android.lock.views.PasswordlessFormView;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.AuthenticationRequest;
import com.auth0.authentication.PasswordlessType;
import com.auth0.authentication.result.Authentication;
import com.auth0.callback.BaseCallback;
import com.auth0.request.ParameterizableRequest;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class PasswordlessLockActivity extends AppCompatActivity {

    private static final String TAG = PasswordlessLockActivity.class.getSimpleName();

    private Options options;
    private Handler handler;
    private Bus lockBus;
    private LinearLayout rootView;
    private LockProgress progress;
    private PasswordlessFormView passwordlessForm;
    private String lastPasswordlessEmailOrNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLaunchConfigValid()) {
            finish();
            return;
        }

        lockBus = new Bus();
        lockBus.register(this);
        handler = new Handler(getMainLooper());

        setContentView(R.layout.com_auth0_lock_activity_lock);
        progress = (LockProgress) findViewById(R.id.com_auth0_lock_progress);
        progress.showResult("");
        rootView = (LinearLayout) findViewById(R.id.com_auth0_lock_content);
        initLockUI();
    }

    private boolean isLaunchConfigValid() {
        options = getIntent().getParcelableExtra(Lock.OPTIONS_EXTRA);
        if (options == null) {
            Log.e(TAG, "Lock Options are missing in the received Intent and PasswordlessLockActivity will not launch. " +
                    "Use the PasswordlessLock.Builder to generate a valid Intent.");
            finish();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (passwordlessForm != null && passwordlessForm.onBackPressed()) {
            return;
        }
        if (options != null && options.isClosable()) {
            Intent intent = new Intent(Lock.CANCELED_ACTION);
            LocalBroadcastManager.getInstance(PasswordlessLockActivity.this).sendBroadcast(intent);
            return;
        }
        super.onBackPressed();
    }

    /**
     * Show the LockUI with all the panels and custom widgets.
     */
    private void initLockUI() {
        passwordlessForm = new PasswordlessFormView(PasswordlessLockActivity.this, lockBus, options.passwordlessMode());
        rootView.addView(passwordlessForm, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void deliverResult(Authentication result) {
        Intent intent = new Intent(Lock.AUTHENTICATION_ACTION);
        intent.putExtra(Lock.ID_TOKEN_EXTRA, result.getToken().getIdToken());
        intent.putExtra(Lock.ACCESS_TOKEN_EXTRA, result.getToken().getAccessToken());
        intent.putExtra(Lock.REFRESH_TOKEN_EXTRA, result.getToken().getRefreshToken());
        intent.putExtra(Lock.TOKEN_TYPE_EXTRA, result.getToken().getType());
        intent.putExtra(Lock.PROFILE_EXTRA, result.getProfile());

        //TODO: Check if sendBroadcast works on background
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OnActivityResult called with intent: " + data);
        processIncomingIntent(data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "OnNewIntent called with intent: " + intent);
        processIncomingIntent(intent);

        super.onNewIntent(intent);
    }

    private void processIncomingIntent(Intent intent) {
        if (options != null && lastPasswordlessEmailOrNumber != null && options.passwordlessMode() == PasswordlessMode.EMAIL_LINK) {
            String code = intent.getData().getQueryParameter("code");
            if (code == null || code.isEmpty()) {
                Log.w(TAG, "Passwordless Code is missing or could not be parsed");
                Snackbar.make(rootView, R.string.com_auth0_lock_error_parsing_passwordless_code, Snackbar.LENGTH_LONG).show();
            } else {
                PasswordlessLoginEvent event = new PasswordlessLoginEvent(options.passwordlessMode(), lastPasswordlessEmailOrNumber, code);
                onPasswordlessAuthenticationRequest(event);
            }
        } else {
            progress.showResult(getString(R.string.com_auth0_lock_error_unexpected_passwordless_intent));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPasswordlessAuthenticationRequest(PasswordlessLoginEvent event) {
        if (options == null) {
            return;
        } else if (event.getEmailOrNumber().isEmpty()) {
            return;
        }

        progress.showProgress();
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(options.getAccount());
        if (event.getCode() != null) {
            AuthenticationRequest loginRequest = event.getLoginRequest(apiClient);
            if (loginRequest != null) {
                loginRequest.addParameters(options.getAuthenticationParameters());
                loginRequest.start(authCallback);
            }
            return;
        }

        lastPasswordlessEmailOrNumber = event.getEmailOrNumber();
        ParameterizableRequest<Void> codeRequest = event.getCodeRequest(apiClient);
        if (codeRequest != null) {
            codeRequest.start(passwordlessCodeCallback);
        }
    }

    //Callbacks

    private BaseCallback<Void> passwordlessCodeCallback = new BaseCallback<Void>() {
        @Override
        public void onSuccess(Void payload) {
            Log.d(TAG, "Passwordless authentication succeeded");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progress.showResult("Passwordless authentication succeeded");
                }
            });
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.d(TAG, "Passwordless authentication failed");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progress.showResult(error.getMessage());
                }
            });
        }
    };

    private BaseCallback<Authentication> authCallback = new BaseCallback<Authentication>() {
        @Override
        public void onSuccess(Authentication authentication) {
            Log.d(TAG, "Login success: " + authentication.getProfile());
            deliverResult(authentication);
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "Login failed");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progress.showResult(error.getMessage());
                }
            });
        }
    };
}
