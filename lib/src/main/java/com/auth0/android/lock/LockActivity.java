/*
 * LockActivity.java
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


import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.ParameterBuilder;
import com.auth0.android.authentication.request.SignUpRequest;
import com.auth0.android.callback.AuthenticationCallback;
import com.auth0.android.lock.errors.AuthenticationError;
import com.auth0.android.lock.errors.LoginErrorMessageBuilder;
import com.auth0.android.lock.errors.SignUpErrorMessageBuilder;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.LockMessageEvent;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.ApplicationFetcher;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.Connection;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.lock.provider.AuthResolver;
import com.auth0.android.lock.views.ClassicLockView;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.AuthProvider;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.request.AuthenticationRequest;
import com.auth0.android.request.internal.OkHttpClientFactory;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.DatabaseUser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = LockActivity.class.getSimpleName();
    private static final String KEY_VERIFICATION_CODE = "mfa_code";
    private static final String KEY_LOGIN_HINT = "login_hint";
    private static final String KEY_SCREEN_HINT = "screen_hint";
    private static final String KEY_MFA_TOKEN = "mfa_token";
    private static final long RESULT_MESSAGE_DURATION = 3000;
    private static final int WEB_AUTH_REQUEST_CODE = 200;
    private static final int CUSTOM_AUTH_REQUEST_CODE = 201;
    private static final int PERMISSION_REQUEST_CODE = 202;

    private ApplicationFetcher applicationFetcher;
    private Configuration configuration;
    private Options options;
    private Handler handler;

    private ClassicLockView lockView;
    private TextView resultMessage;

    private AuthProvider currentProvider;
    private WebProvider webProvider;

    private LoginErrorMessageBuilder loginErrorBuilder;
    private SignUpErrorMessageBuilder signUpErrorBuilder;
    private DatabaseLoginEvent lastDatabaseLogin;
    private DatabaseSignUpEvent lastDatabaseSignUp;

    @SuppressWarnings("unused")
    public LockActivity() {
    }

    @VisibleForTesting
    LockActivity(Configuration configuration, Options options, ClassicLockView lockView, WebProvider webProvider) {
        this.configuration = configuration;
        this.options = options;
        this.lockView = lockView;
        this.webProvider = webProvider;
        this.handler = new Handler();
        this.loginErrorBuilder = new LoginErrorMessageBuilder();
        this.signUpErrorBuilder = new SignUpErrorMessageBuilder();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!hasValidLaunchConfig()) {
            return;
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Bus lockBus = new Bus();
        lockBus.register(this);
        handler = new Handler(getMainLooper());
        webProvider = new WebProvider(options);

        setContentView(R.layout.com_auth0_lock_activity_lock);
        resultMessage = findViewById(R.id.com_auth0_lock_result_message);
        ScrollView rootView = findViewById(R.id.com_auth0_lock_content);
        lockView = new ClassicLockView(this, lockBus, options.getTheme());
        RelativeLayout.LayoutParams lockViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lockView.setLayoutParams(lockViewParams);
        rootView.addView(lockView);

        loginErrorBuilder = new LoginErrorMessageBuilder(R.string.com_auth0_lock_db_login_error_message, R.string.com_auth0_lock_db_login_error_invalid_credentials_message);
        signUpErrorBuilder = new SignUpErrorMessageBuilder();

        lockBus.post(new FetchApplicationEvent());
    }

    private boolean hasValidLaunchConfig() {
        String errorDescription = null;
        if (!hasValidOptions()) {
            errorDescription = "Configuration is not valid and the Activity will finish.";
        }
        if (!hasValidTheme()) {
            errorDescription = "You need to use a Lock.Theme theme (or descendant) with this Activity.";
        }
        if (errorDescription == null) {
            return true;
        }
        Intent intent = new Intent(Constants.INVALID_CONFIGURATION_ACTION);
        intent.putExtra(Constants.ERROR_EXTRA, errorDescription);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
        return false;
    }

    private boolean hasValidTheme() {
        TypedArray a = getTheme().obtainStyledAttributes(R.styleable.Lock_Theme);
        boolean validTheme = a.hasValue(R.styleable.Lock_Theme_Auth0_HeaderLogo);
        a.recycle();
        return validTheme;
    }

    private boolean hasValidOptions() {
        options = getIntent().getParcelableExtra(Constants.OPTIONS_EXTRA);
        if (options == null) {
            Log.e(TAG, "Lock Options are missing in the received Intent and LockActivity will not launch. " +
                    "Use the PasswordlessLock.Builder to generate a valid Intent.");
            return false;
        }

        boolean launchedForResult = getCallingActivity() != null;
        if (launchedForResult) {
            Log.e(TAG, "You're not allowed to start Lock with startActivityForResult.");
            return false;
        }
        boolean launchedAsSingleTask = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //TODO: Document this case for users on <= KITKAT, as they will not receive this warning.
            if (options.useBrowser() && !launchedAsSingleTask) {
                Log.e(TAG, "Please, check that you have specified launchMode 'singleTask' in the AndroidManifest.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (lockView.onBackPressed() || !options.isClosable()) {
            return;
        }

        Log.v(TAG, "User had just closed the activity.");
        Intent intent = new Intent(Constants.CANCELED_ACTION);
        LocalBroadcastManager.getInstance(LockActivity.this).sendBroadcast(intent);
        super.onBackPressed();
    }

    private void deliverAuthenticationResult(Credentials credentials) {
        Intent intent = new Intent(Constants.AUTHENTICATION_ACTION);
        intent.putExtra(Constants.ID_TOKEN_EXTRA, credentials.getIdToken());
        intent.putExtra(Constants.ACCESS_TOKEN_EXTRA, credentials.getAccessToken());
        intent.putExtra(Constants.REFRESH_TOKEN_EXTRA, credentials.getRefreshToken());
        intent.putExtra(Constants.TOKEN_TYPE_EXTRA, credentials.getType());
        intent.putExtra(Constants.EXPIRES_IN_EXTRA, credentials.getExpiresIn());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    private void deliverSignUpResult(DatabaseUser result) {
        Intent intent = new Intent(Constants.SIGN_UP_ACTION);
        intent.putExtra(Constants.EMAIL_EXTRA, result.getEmail());
        intent.putExtra(Constants.USERNAME_EXTRA, result.getEmail());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    private void showSuccessMessage(String message) {
        resultMessage.setBackgroundColor(ContextCompat.getColor(this, R.color.com_auth0_lock_result_message_success_background));
        resultMessage.setVisibility(View.VISIBLE);
        resultMessage.setText(message);
        lockView.showProgress(false);
        handler.removeCallbacks(resultMessageHider);
        handler.postDelayed(resultMessageHider, RESULT_MESSAGE_DURATION);
    }

    private void showErrorMessage(String message) {
        resultMessage.setBackgroundColor(ContextCompat.getColor(this, R.color.com_auth0_lock_result_message_error_background));
        resultMessage.setVisibility(View.VISIBLE);
        resultMessage.setText(message);
        lockView.showProgress(false);
        handler.removeCallbacks(resultMessageHider);
        handler.postDelayed(resultMessageHider, RESULT_MESSAGE_DURATION);
    }

    private Runnable resultMessageHider = new Runnable() {
        @Override
        public void run() {
            resultMessage.setVisibility(View.GONE);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (currentProvider != null) {
            currentProvider.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WEB_AUTH_REQUEST_CODE:
                lockView.showProgress(false);
                webProvider.resume(requestCode, resultCode, data);
                break;
            case CUSTOM_AUTH_REQUEST_CODE:
                lockView.showProgress(false);
                if (currentProvider != null) {
                    currentProvider.authorize(requestCode, resultCode, data);
                    currentProvider = null;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        lockView.showProgress(false);
        if (webProvider.resume(intent)) {
            return;
        } else if (currentProvider != null) {
            currentProvider.authorize(intent);
            currentProvider = null;
            return;
        }
        super.onNewIntent(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onFetchApplicationRequest(FetchApplicationEvent event) {
        if (applicationFetcher == null) {
            Auth0 account = options.getAccount();
            OkHttpClient client = new OkHttpClientFactory().createClient(account.isLoggingEnabled(), account.isTLS12Enforced(),
                    account.getConnectTimeoutInSeconds(), account.getReadTimeoutInSeconds(), account.getWriteTimeoutInSeconds());
            applicationFetcher = new ApplicationFetcher(account, client);
            applicationFetcher.fetch(applicationCallback);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onLockMessage(final LockMessageEvent event) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showErrorMessage(getString(event.getMessageRes()));
            }
        });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onOAuthAuthenticationRequest(OAuthLoginEvent event) {
        final String connection = event.getConnection();

        if (event.useActiveFlow()) {
            lockView.showProgress(true);
            Log.d(TAG, "Using the /ro endpoint for this OAuth Login Request");
            AuthenticationRequest request = options.getAuthenticationAPIClient()
                    .login(event.getUsername(), event.getPassword(), connection)
                    .addAuthenticationParameters(options.getAuthenticationParameters());
            if (options.getScope() != null) {
                request.setScope(options.getScope());
            }
            if (options.getAudience() != null && options.getAccount().isOIDCConformant()) {
                request.setAudience(options.getAudience());
            }
            request.start(authCallback);
            return;
        }

        Log.v(TAG, "Looking for a provider to use /authorize with the connection " + connection);
        currentProvider = AuthResolver.providerFor(event.getStrategy(), connection);
        if (currentProvider != null) {
            HashMap<String, Object> authParameters = new HashMap<>(options.getAuthenticationParameters());
            final String connectionScope = options.getConnectionsScope().get(connection);
            if (connectionScope != null) {
                authParameters.put(Constants.CONNECTION_SCOPE_KEY, connectionScope);
            }
            final String scope = options.getScope();
            if (scope != null) {
                authParameters.put(ParameterBuilder.SCOPE_KEY, scope);
            }
            final String audience = options.getAudience();
            if (audience != null && options.getAccount().isOIDCConformant()) {
                authParameters.put(ParameterBuilder.AUDIENCE_KEY, audience);
            }
            if (!TextUtils.isEmpty(event.getUsername())) {
                authParameters.put(KEY_LOGIN_HINT, event.getUsername());
            }
            currentProvider.setParameters(authParameters);
            currentProvider.start(this, authProviderCallback, PERMISSION_REQUEST_CODE, CUSTOM_AUTH_REQUEST_CODE);
            return;
        }

        Map<String, Object> extraAuthParameters = null;
        if (!TextUtils.isEmpty(event.getUsername())) {
            extraAuthParameters = Collections.singletonMap(KEY_LOGIN_HINT, (Object) event.getUsername());
        }
        Log.d(TAG, "Couldn't find an specific provider, using the default: " + WebAuthProvider.class.getSimpleName());
        webProvider.start(this, connection, extraAuthParameters, authProviderCallback, WEB_AUTH_REQUEST_CODE);
    }

    private void completeDatabaseAuthenticationOnBrowser() {
        //DBConnection checked for nullability before the API call
        @SuppressWarnings("ConstantConditions")
        String connection = configuration.getDatabaseConnection().getName();

        String loginHint = null;
        String screenHint = null;
        if (lastDatabaseSignUp != null) {
            loginHint = lastDatabaseSignUp.getEmail();
            screenHint = "signup";
        } else if (lastDatabaseLogin != null) {
            loginHint = lastDatabaseLogin.getUsernameOrEmail();
            screenHint = "login";
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put(KEY_LOGIN_HINT, loginHint);
        params.put(KEY_SCREEN_HINT, screenHint);

        webProvider.start(this, connection, params, authProviderCallback, WEB_AUTH_REQUEST_CODE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseLoginEvent event) {
        if (configuration.getDatabaseConnection() == null) {
            Log.w(TAG, "There is no default Database connection to authenticate with");
            return;
        }

        lockView.showProgress(true);
        lastDatabaseLogin = event;
        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        AuthenticationRequest request;
        HashMap<String, Object> parameters = new HashMap<>(options.getAuthenticationParameters());
        if (TextUtils.isEmpty(event.getMFAToken()) || TextUtils.isEmpty(event.getVerificationCode())) {
            String connection = configuration.getDatabaseConnection().getName();
            request = apiClient.login(event.getUsernameOrEmail(), event.getPassword(), connection);
            if (!TextUtils.isEmpty(event.getVerificationCode())) {
                parameters.put(KEY_VERIFICATION_CODE, event.getVerificationCode());
            }
        } else {
            request = apiClient.loginWithOTP(event.getMFAToken(), event.getVerificationCode());
        }

        request.addAuthenticationParameters(parameters);
        if (options.getScope() != null) {
            request.setScope(options.getScope());
        }
        if (options.getAudience() != null && options.getAccount().isOIDCConformant()) {
            request.setAudience(options.getAudience());
        }
        request.start(authCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseSignUpEvent event) {
        if (configuration.getDatabaseConnection() == null) {
            Log.w(TAG, "There is no default Database connection to authenticate with");
            return;
        }

        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        final String connection = configuration.getDatabaseConnection().getName();
        lockView.showProgress(true);
        lastDatabaseSignUp = event;

        if (configuration.loginAfterSignUp()) {
            Map<String, Object> authParameters = new HashMap<>(options.getAuthenticationParameters());
            SignUpRequest request = event.getSignUpRequest(apiClient, connection)
                    .addAuthenticationParameters(authParameters);
            if (options.getScope() != null) {
                request.setScope(options.getScope());
            }
            if (options.getAudience() != null && options.getAccount().isOIDCConformant()) {
                request.setAudience(options.getAudience());
            }
            request.start(authCallback);
        } else {
            event.getCreateUserRequest(apiClient, connection)
                    .start(createCallback);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseChangePasswordEvent event) {
        if (configuration.getDatabaseConnection() == null) {
            Log.w(TAG, "There is no default Database connection to authenticate with");
            return;
        }

        lockView.showProgress(true);
        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        final String connection = configuration.getDatabaseConnection().getName();
        apiClient.resetPassword(event.getEmail(), connection)
                .start(changePwdCallback);
    }

    //Callbacks
    private com.auth0.android.callback.AuthenticationCallback<List<Connection>> applicationCallback = new AuthenticationCallback<List<Connection>>() {
        @Override
        public void onSuccess(final List<Connection> connections) {
            configuration = new Configuration(connections, options);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lockView.configure(configuration);
                }
            });
            applicationFetcher = null;
        }

        @Override
        public void onFailure(final AuthenticationException error) {
            Log.e(TAG, "Failed to fetch the application: " + error.getMessage(), error);
            applicationFetcher = null;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lockView.configure(null);
                }
            });
        }
    };

    private AuthCallback authProviderCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            Log.e(TAG, "Failed to authenticate the user. A dialog is going to be shown with more information.");
            dialog.show();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(final AuthenticationException exception) {
            final AuthenticationError authError = loginErrorBuilder.buildFrom(exception);
            final String message = authError.getMessage(LockActivity.this);
            Log.e(TAG, "Failed to authenticate the user: " + message, exception);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showErrorMessage(message);
                }
            });
        }

        @Override
        public void onSuccess(@NonNull final Credentials credentials) {
            deliverAuthenticationResult(credentials);
        }
    };

    private AuthenticationCallback<Credentials> authCallback = new AuthenticationCallback<Credentials>() {
        @Override
        public void onSuccess(Credentials credentials) {
            deliverAuthenticationResult(credentials);
            lastDatabaseLogin = null;
            lastDatabaseSignUp = null;
        }

        @Override
        public void onFailure(final AuthenticationException error) {
            Log.e(TAG, "Failed to authenticate the user: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lockView.showProgress(false);

                    final AuthenticationError authError = loginErrorBuilder.buildFrom(error);
                    if (error.isVerificationRequired()) {
                        completeDatabaseAuthenticationOnBrowser();
                        return;
                    }
                    if (error.isMultifactorRequired()) {
                        String mfaToken = (String) error.getValue(KEY_MFA_TOKEN);
                        if (!TextUtils.isEmpty(mfaToken)) {
                            lastDatabaseLogin.setMFAToken(mfaToken);
                        }
                        lockView.showMFACodeForm(lastDatabaseLogin);
                        return;
                    }
                    String message = authError.getMessage(LockActivity.this);
                    showErrorMessage(message);
                    if (error.isMultifactorTokenInvalid()) {
                        //The MFA Token has expired. The user needs to log in again. Show the username/password form
                        onBackPressed();
                    }
                }
            });
        }
    };

    private AuthenticationCallback<DatabaseUser> createCallback = new AuthenticationCallback<DatabaseUser>() {
        @Override
        public void onSuccess(final DatabaseUser user) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    deliverSignUpResult(user);
                }
            });
            lastDatabaseSignUp = null;
        }

        @Override
        public void onFailure(final AuthenticationException error) {
            Log.e(TAG, "Failed to create the user: " + error.getMessage(), error);
            if (error.isVerificationRequired()) {
                completeDatabaseAuthenticationOnBrowser();
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = signUpErrorBuilder.buildFrom(error).getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };

    private AuthenticationCallback<Void> changePwdCallback = new AuthenticationCallback<Void>() {
        @Override
        public void onSuccess(Void payload) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showSuccessMessage(getString(R.string.com_auth0_lock_db_change_password_message_success));
                    if (options.allowLogIn() || options.allowSignUp()) {
                        lockView.showChangePasswordForm(false);
                    }
                }
            });

        }

        @Override
        public void onFailure(AuthenticationException error) {
            Log.e(TAG, "Failed to reset the user password: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = new AuthenticationError(R.string.com_auth0_lock_db_message_change_password_error).getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };


}
