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
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.auth0.Auth0Exception;
import com.auth0.android.lock.errors.AuthenticationError;
import com.auth0.android.lock.errors.LoginAuthenticationErrorBuilder;
import com.auth0.android.lock.errors.SignUpAuthenticationErrorBuilder;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.EnterpriseLoginEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.provider.AuthCallback;
import com.auth0.android.lock.provider.AuthProvider;
import com.auth0.android.lock.provider.AuthorizeResult;
import com.auth0.android.lock.provider.CallbackHelper;
import com.auth0.android.lock.provider.OAuth2WebAuthProvider;
import com.auth0.android.lock.provider.ProviderResolverManager;
import com.auth0.android.lock.utils.ActivityUIHelper;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.json.Application;
import com.auth0.android.lock.utils.json.ApplicationFetcher;
import com.auth0.android.lock.views.ClassicLockView;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Credentials;
import com.auth0.authentication.result.DatabaseUser;
import com.auth0.callback.BaseCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.android.lock.errors.AuthenticationError.ErrorType;

public class LockActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = LockActivity.class.getSimpleName();
    private static final String KEY_USER_METADATA = "user_metadata";
    private static final String KEY_VERIFICATION_CODE = "mfa_code";
    private static final long RESULT_MESSAGE_DURATION = 3000;
    private static final double KEYBOARD_OPENED_DELTA = 0.15;
    private static final int PERMISSION_REQUEST_CODE = 201;

    private ApplicationFetcher applicationFetcher;
    private Configuration configuration;
    private Options options;
    private Handler handler;

    private ClassicLockView lockView;
    private TextView resultMessage;

    private AuthProvider currentProvider;

    private boolean keyboardIsShown;
    private ViewGroup contentView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardListener;
    private LoginAuthenticationErrorBuilder loginErrorBuilder;
    private SignUpAuthenticationErrorBuilder signUpErrorBuilder;
    private DatabaseLoginEvent lastDatabaseLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLaunchConfigValid()) {
            Log.d(TAG, "Configuration is not valid and the Activity will finish.");
            finish();
            return;
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Bus lockBus = new Bus();
        lockBus.register(this);
        handler = new Handler(getMainLooper());

        setContentView(R.layout.com_auth0_lock_activity_lock);
        int paddingTop = ActivityUIHelper.getStatusBarHeight(this, options.isFullscreen());
        contentView = (ViewGroup) findViewById(R.id.com_auth0_lock_container);
        resultMessage = (TextView) findViewById(R.id.com_auth0_lock_result_message);
        ScrollView rootView = (ScrollView) findViewById(R.id.com_auth0_lock_content);
        lockView = new ClassicLockView(this, lockBus, options.getTheme());
        RelativeLayout.LayoutParams lockViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lockView.setLayoutParams(lockViewParams);
        lockView.setHeaderPadding(paddingTop);
        rootView.addView(lockView);

        resultMessage.setPadding(0, resultMessage.getPaddingTop() + paddingTop, 0, resultMessage.getPaddingBottom());
        ActivityUIHelper.useStatusBarSpace(this, options.isFullscreen());

        loginErrorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_lock_db_login_error_message, R.string.com_auth0_lock_db_login_error_invalid_credentials_message);
        signUpErrorBuilder = new SignUpAuthenticationErrorBuilder();

        lockBus.post(new FetchApplicationEvent());
        setupKeyboardListener();
    }

    private void setupKeyboardListener() {
        keyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                onKeyboardStateChanged(keypadHeight > screenHeight * KEYBOARD_OPENED_DELTA);
            }
        };
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);
    }

    private void removeKeyboardListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardListener);
        }
        keyboardListener = null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ActivityUIHelper.useStatusBarSpace(this, options.isFullscreen());
    }

    @Override
    protected void onDestroy() {
        removeKeyboardListener();
        super.onDestroy();
    }


    private void onKeyboardStateChanged(boolean isOpen) {
        if (isOpen == keyboardIsShown || configuration == null) {
            return;
        }
        Log.d(TAG, String.format("Keyboard state changed to %s", isOpen ? "opened" : "closed"));
        keyboardIsShown = isOpen;
        lockView.onKeyboardStateChanged(isOpen);
    }

    private boolean isLaunchConfigValid() {
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

    private void fetchProviderAndBeginAuthentication(String connectionName) {
        Log.v(TAG, "Looking for a provider to use with the connection " + connectionName);
        currentProvider = ProviderResolverManager.get().onAuthProviderRequest(this, authProviderCallback, connectionName);
        if (currentProvider == null) {
            Log.d(TAG, "Couldn't find an specific provider, using the default: " + OAuth2WebAuthProvider.class.getSimpleName());
            String pkgName = getApplicationContext().getPackageName();
            OAuth2WebAuthProvider oauth2 = new OAuth2WebAuthProvider(new CallbackHelper(pkgName), options.getAccount(), authProviderCallback, options.usePKCE());
            oauth2.setUseBrowser(options.useBrowser());
            oauth2.setIsFullscreen(options.isFullscreen());
            oauth2.setParameters(options.getAuthenticationParameters());
            currentProvider = oauth2;
        }
        currentProvider.start(this, connectionName, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (currentProvider != null) {
            currentProvider.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentProvider != null) {
            //Deliver result to the IDP
            lockView.showProgress(false);
            AuthorizeResult result = new AuthorizeResult(requestCode, resultCode, data);
            currentProvider.authorize(LockActivity.this, result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (currentProvider != null) {
            //Deliver result to the IDP
            lockView.showProgress(false);
            AuthorizeResult result = new AuthorizeResult(intent);
            currentProvider.authorize(LockActivity.this, result);
        }
        super.onNewIntent(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onFetchApplicationRequest(FetchApplicationEvent event) {
        if (applicationFetcher == null) {
            applicationFetcher = new ApplicationFetcher(options.getAccount(), new OkHttpClient());
            applicationFetcher.fetch(applicationCallback);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSocialAuthenticationRequest(SocialConnectionEvent event) {
        fetchProviderAndBeginAuthentication(event.getConnectionName());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseLoginEvent event) {
        if (configuration.getDefaultDatabaseConnection() == null) {
            Log.w(TAG, "There is no default Database connection to authenticate with");
            return;
        }

        lockView.showProgress(true);
        lastDatabaseLogin = event;
        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        final HashMap<String, Object> parameters = new HashMap<>(options.getAuthenticationParameters());
        if (event.getVerificationCode() != null) {
            parameters.put(KEY_VERIFICATION_CODE, event.getVerificationCode());
        }
        apiClient.login(event.getUsernameOrEmail(), event.getPassword())
                .setConnection(configuration.getDefaultDatabaseConnection().getName())
                .addAuthenticationParameters(parameters)
                .start(authCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseSignUpEvent event) {
        if (configuration.getDefaultDatabaseConnection() == null) {
            Log.w(TAG, "There is no default Database connection to authenticate with");
            return;
        }

        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        apiClient.setDefaultDatabaseConnection(configuration.getDefaultDatabaseConnection().getName());

        lockView.showProgress(true);

        if (configuration.loginAfterSignUp()) {
            Map<String, Object> authParameters = new HashMap<>(options.getAuthenticationParameters());
            if (event.extraFields() != null) {
                authParameters.put(KEY_USER_METADATA, event.extraFields());
            }
            event.getSignUpRequest(apiClient)
                    .addAuthenticationParameters(authParameters)
                    .start(authCallback);
        } else {
            Map<String, Object> parameters = new HashMap<>();
            if (event.extraFields() != null) {
                parameters.put(KEY_USER_METADATA, event.extraFields());
            }
            event.getCreateUserRequest(apiClient)
                    .addParameters(parameters)
                    .start(createCallback);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseChangePasswordEvent event) {
        if (configuration.getDefaultDatabaseConnection() == null) {
            Log.w(TAG, "There is no default Database connection to authenticate with");
            return;
        }

        lockView.showProgress(true);
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(options.getAccount());
        apiClient.setDefaultDatabaseConnection(configuration.getDefaultDatabaseConnection().getName());
        apiClient.requestChangePassword(event.getEmail())
                .addParameters(options.getAuthenticationParameters())
                .start(changePwdCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEnterpriseAuthenticationRequest(EnterpriseLoginEvent event) {
        if (event.getConnectionName() == null) {
            Log.w(TAG, "There is no matching enterprise connection to authenticate with");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showErrorMessage(getString(R.string.com_auth0_lock_enterprise_no_connection_message));
                }
            });
            return;
        } else if (event.useRO()) {
            boolean missingADConfiguration = event.getConnectionName().equals(Strategies.ActiveDirectory.getName()) && configuration.getDefaultActiveDirectoryConnection() == null;
            boolean missingEnterpriseConfiguration = configuration.getEnterpriseStrategies().isEmpty();
            if (missingADConfiguration || missingEnterpriseConfiguration) {
                Log.w(TAG, "There is no matching enterprise connection to authenticate with");
                return;
            }
        }

        lockView.showProgress(true);
        if (event.useRO()) {
            Log.d(TAG, "Using the /ro endpoint for this Enterprise Login Request");
            AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
            apiClient.login(event.getUsername(), event.getPassword())
                    .setConnection(event.getConnectionName())
                    .addAuthenticationParameters(options.getAuthenticationParameters())
                    .start(authCallback);
            return;
        }

        Log.d(TAG, "Using the /authorize endpoint for this Enterprise Login Request");
        fetchProviderAndBeginAuthentication(event.getConnectionName());
    }

    //Callbacks
    private BaseCallback<Application> applicationCallback = new BaseCallback<Application>() {
        @Override
        public void onSuccess(Application app) {
            configuration = new Configuration(app, options);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lockView.configure(configuration);
                }
            });
            applicationFetcher = null;
        }

        @Override
        public void onFailure(final Auth0Exception error) {
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
        public void onFailure(@StringRes int titleResource, @StringRes final int messageResource, final Throwable cause) {
            final String message = new AuthenticationError(messageResource, cause).getMessage(LockActivity.this);
            Log.e(TAG, "Failed to authenticate the user: " + message, cause);
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

    private BaseCallback<Credentials> authCallback = new BaseCallback<Credentials>() {
        @Override
        public void onSuccess(Credentials credentials) {
            deliverAuthenticationResult(credentials);
            lastDatabaseLogin = null;
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "Failed to authenticate the user: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lockView.showProgress(false);
                    final AuthenticationError authError = loginErrorBuilder.buildFrom(error);
                    if (authError.getErrorType() == ErrorType.MFA_REQUIRED || authError.getErrorType() == ErrorType.MFA_NOT_ENROLLED) {
                        lockView.showMFACodeForm(lastDatabaseLogin);
                        return;
                    }
                    String message = authError.getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };

    private BaseCallback<DatabaseUser> createCallback = new BaseCallback<DatabaseUser>() {
        @Override
        public void onSuccess(final DatabaseUser user) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    deliverSignUpResult(user);
                }
            });
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "Failed to create the user: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = signUpErrorBuilder.buildFrom(error).getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };

    private BaseCallback<Void> changePwdCallback = new BaseCallback<Void>() {
        @Override
        public void onSuccess(Void payload) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showSuccessMessage(getString(R.string.com_auth0_lock_db_change_password_message_success));
                    lockView.showChangePasswordForm(false);
                }
            });

        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "Failed to reset the user password: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = new AuthenticationError(R.string.com_auth0_lock_db_message_change_password_error, error).getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };


}
