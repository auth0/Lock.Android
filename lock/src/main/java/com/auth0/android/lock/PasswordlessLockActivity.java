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
import android.net.Uri;
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
import com.auth0.android.lock.utils.Application;
import com.auth0.android.lock.views.LockProgress;
import com.auth0.android.lock.views.PasswordlessFormView;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.AuthenticationRequest;
import com.auth0.authentication.result.Authentication;
import com.auth0.callback.BaseCallback;
import com.auth0.request.ParameterizableRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PasswordlessLockActivity extends AppCompatActivity {

    private static final String TAG = PasswordlessLockActivity.class.getSimpleName();
    private static final String JSONP_PREFIX = "Auth0.setClient(";

    private Application application;
    private Configuration configuration;
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
        rootView = (LinearLayout) findViewById(R.id.com_auth0_lock_content);
        fetchApplicationInfo();
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
        configuration = new Configuration(application, options);
        if (configuration.getDefaultPasswordlessStrategy() != null) {
            passwordlessForm = new PasswordlessFormView(PasswordlessLockActivity.this, lockBus, configuration.getPasswordlessMode());
            rootView.addView(passwordlessForm, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void deliverResult(Authentication result) {
        Intent intent = new Intent(Lock.AUTHENTICATION_ACTION);
        intent.putExtra(Lock.ID_TOKEN_EXTRA, result.getToken().getIdToken());
        intent.putExtra(Lock.ACCESS_TOKEN_EXTRA, result.getToken().getAccessToken());
        intent.putExtra(Lock.REFRESH_TOKEN_EXTRA, result.getToken().getRefreshToken());
        intent.putExtra(Lock.TOKEN_TYPE_EXTRA, result.getToken().getType());
        intent.putExtra(Lock.PROFILE_EXTRA, result.getProfile());

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

    /**
     * Fetch application information from Auth0
     */
    private void fetchApplicationInfo() {
        OkHttpClient client = new OkHttpClient();
        Uri uri = Uri.parse(options.getAccount().getConfigurationUrl()).buildUpon().appendPath("client")
                .appendPath(options.getAccount().getClientId() + ".js").build();

        com.squareup.okhttp.Request req = new com.squareup.okhttp.Request.Builder()
                .url(uri.toString())
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, final IOException e) {
                Log.e(TAG, "Failed to fetchApplication: " + e.getMessage());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.showResult(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                application = parseJSONP(response);
                Log.i(TAG, "Application received!: " + application.getId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //this will trigger a hide
                        progress.showResult("");
                        initLockUI();
                    }
                });
            }
        });

    }

    /**
     * Parses the Application JSONP received from the API.
     */
    private Application parseJSONP(Response response) {
        try {
            String json = response.body().string();
            final int length = JSONP_PREFIX.length();
            if (json.length() < length) {
                throw new JSONException("Invalid App Info JSONP");
            }
            json = json.substring(length);
            JSONTokener tokenizer = new JSONTokener(json);
            if (!tokenizer.more()) {
                throw tokenizer.syntaxError("Invalid App Info JSONP");
            }
            Object nextValue = tokenizer.nextValue();
            if (!(nextValue instanceof JSONObject)) {
                tokenizer.back();
                throw tokenizer.syntaxError("Invalid JSON value of App Info");
            }
            JSONObject jsonObject = (JSONObject) nextValue;
            return new ObjectMapper().reader(Application.class).readValue(jsonObject.toString());
        } catch (IOException | JSONException e) {
            throw new Auth0Exception("Failed to parse response to request", e);
        }
    }

    private void processIncomingIntent(Intent intent) {
        if (options != null && lastPasswordlessEmailOrNumber != null && configuration.getPasswordlessMode() == PasswordlessMode.EMAIL_LINK) {
            String code = intent.getData().getQueryParameter("code");
            if (code == null || code.isEmpty()) {
                Log.w(TAG, "Passwordless Code is missing or could not be parsed");
                Snackbar.make(rootView, R.string.com_auth0_lock_error_parsing_passwordless_code, Snackbar.LENGTH_LONG).show();
            } else {
                PasswordlessLoginEvent event = new PasswordlessLoginEvent(configuration.getPasswordlessMode(), lastPasswordlessEmailOrNumber, code);
                onPasswordlessAuthenticationRequest(event);
            }
        } else {
            progress.showResult(getString(R.string.com_auth0_lock_error_unexpected_passwordless_intent));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPasswordlessAuthenticationRequest(PasswordlessLoginEvent event) {
        if (options == null || configuration.getPasswordlessStrategy() == null) {
            return;
        } else if (event.getEmailOrNumber().isEmpty()) {
            return;
        }

        progress.showProgress();
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(options.getAccount());
        if (event.getCode() != null) {
            AuthenticationRequest loginRequest = event.getLoginRequest(apiClient);
            if (loginRequest != null) {
                HashMap<String, Object> authenticationParameters = options.getAuthenticationParameters();
                authenticationParameters.put("connection", configuration.getPasswordlessStrategy().getConnections().get(0).getName());
                loginRequest.addParameters(authenticationParameters);
                loginRequest.start(authCallback);
            }
            return;
        }

        lastPasswordlessEmailOrNumber = event.getEmailOrNumber();
        ParameterizableRequest<Void> codeRequest = event.getCodeRequest(apiClient);
        if (codeRequest != null) {
            codeRequest.getParameterBuilder().setConnection(configuration.getPasswordlessStrategy().getConnections().get(0).getName());
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
