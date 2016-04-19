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
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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
import com.auth0.android.lock.utils.Application;
import com.auth0.android.lock.utils.ApplicationFetcher;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.views.ClassicPanelHolder;
import com.auth0.android.lock.views.HeaderView;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Authentication;
import com.auth0.authentication.result.Credentials;
import com.auth0.authentication.result.DatabaseUser;
import com.auth0.authentication.result.UserProfile;
import com.auth0.callback.BaseCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class LockActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = LockActivity.class.getSimpleName();
    private static final long RESULT_MESSAGE_DURATION = 3000;
    private static final double KEYBOARD_OPENED_DELTA = 0.15;
    private static final int PERMISSION_REQUEST_CODE = 201;

    private ApplicationFetcher applicationFetcher;
    private Configuration configuration;
    private Options options;
    private Handler handler;

    private ClassicPanelHolder panelHolder;
    private TextView resultMessage;

    private ProgressDialog progressDialog;
    private HeaderView headerView;
    private AuthProvider currentProvider;

    private boolean keyboardIsShown;
    private ViewGroup contentView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardListener;
    private LoginAuthenticationErrorBuilder loginErrorBuilder;
    private SignUpAuthenticationErrorBuilder signUpErrorBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLaunchConfigValid()) {
            finish();
            return;
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Bus lockBus = new Bus();
        lockBus.register(this);
        handler = new Handler(getMainLooper());

        setContentView(R.layout.com_auth0_lock_activity_lock);
        contentView = (ViewGroup) findViewById(R.id.com_auth0_lock_container);
        headerView = (HeaderView) findViewById(R.id.com_auth0_lock_header);
        resultMessage = (TextView) findViewById(R.id.com_auth0_lock_result_message);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.com_auth0_lock_content);
        panelHolder = new ClassicPanelHolder(this, lockBus);
        rootView.addView(panelHolder, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
    protected void onDestroy() {
        removeKeyboardListener();
        super.onDestroy();
    }


    private void onKeyboardStateChanged(boolean isOpen) {
        if (isOpen == keyboardIsShown || configuration == null) {
            return;
        }
        keyboardIsShown = isOpen;
        headerView.onKeyboardStateChanged(isOpen);
        panelHolder.onKeyboardStateChanged(isOpen);
    }

    private boolean isLaunchConfigValid() {
        options = getIntent().getParcelableExtra(Lock.OPTIONS_EXTRA);
        if (options == null) {
            Log.e(TAG, "Lock Options are missing in the received Intent and LockActivity will not launch. " +
                    "Use the PasswordlessLock.Builder to generate a valid Intent.");
            return false;
        }

        boolean launchedForResult = getCallingActivity() != null;
        if (options.useBrowser() && launchedForResult) {
            Log.e(TAG, "You're not able to useBrowser and startActivityForResult at the same time.");
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
        if (panelHolder.onBackPressed()) {
            return;
        }

        if (options.isClosable()) {
            Intent intent = new Intent(Lock.CANCELED_ACTION);
            LocalBroadcastManager.getInstance(LockActivity.this).sendBroadcast(intent);
            return;
        }
        super.onBackPressed();
    }

    private void deliverResult(Authentication result) {
        Intent intent = new Intent(Lock.AUTHENTICATION_ACTION);
        intent.putExtra(Lock.ID_TOKEN_EXTRA, result.getCredentials().getIdToken());
        intent.putExtra(Lock.ACCESS_TOKEN_EXTRA, result.getCredentials().getAccessToken());
        intent.putExtra(Lock.REFRESH_TOKEN_EXTRA, result.getCredentials().getRefreshToken());
        intent.putExtra(Lock.TOKEN_TYPE_EXTRA, result.getCredentials().getType());
        intent.putExtra(Lock.PROFILE_EXTRA, result.getProfile());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    private void showSuccessMessage(String message) {
        resultMessage.setBackgroundColor(getResources().getColor(R.color.com_auth0_lock_result_message_success_background));
        resultMessage.setVisibility(View.VISIBLE);
        resultMessage.setText(message);
        panelHolder.showProgress(false);
        handler.removeCallbacks(resultMessageHider);
        handler.postDelayed(resultMessageHider, RESULT_MESSAGE_DURATION);
    }

    private void showErrorMessage(String message) {
        resultMessage.setBackgroundColor(getResources().getColor(R.color.com_auth0_lock_result_message_error_background));
        resultMessage.setVisibility(View.VISIBLE);
        resultMessage.setText(message);
        panelHolder.showProgress(false);
        handler.removeCallbacks(resultMessageHider);
        handler.postDelayed(resultMessageHider, RESULT_MESSAGE_DURATION);
    }


    private Runnable resultMessageHider = new Runnable() {
        @Override
        public void run() {
            resultMessage.setVisibility(View.GONE);
        }
    };

    private void showProgressDialog(final boolean show) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    progressDialog = ProgressDialog.show(LockActivity.this, getString(R.string.com_auth0_lock_title_social_progress_dialog), getString(R.string.com_auth0_lock_message_social_progress_dialog), true, false);
                } else if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    private void fetchProviderAndBeginAuthentication(String connectionName) {
        currentProvider = ProviderResolverManager.get().onAuthProviderRequest(this, authProviderCallback, connectionName);
        if (currentProvider == null) {
            String pkgName = getApplicationContext().getPackageName();
            OAuth2WebAuthProvider oauth2 = new OAuth2WebAuthProvider(new CallbackHelper(pkgName), options.getAccount(), authProviderCallback);
            oauth2.setUseBrowser(options.useBrowser());
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
        Log.d(TAG, "OnActivityResult called with intent: " + data);
        if (currentProvider != null) {
            //Deliver result to the IDP
            panelHolder.showProgress(false);
            AuthorizeResult result = new AuthorizeResult(requestCode, resultCode, data);
            currentProvider.authorize(LockActivity.this, result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "OnNewIntent called with intent: " + intent);
        if (currentProvider != null) {
            //Deliver result to the IDP
            panelHolder.showProgress(false);
            AuthorizeResult result = new AuthorizeResult(intent);
            currentProvider.authorize(LockActivity.this, result);
        }
        super.onNewIntent(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onFetchApplicationRequest(FetchApplicationEvent event) {
        if (configuration == null && applicationFetcher == null) {
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
            return;
        }

        panelHolder.showProgress(true);
        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        apiClient.getProfileAfter(apiClient.login(event.getUsernameOrEmail(), event.getPassword()))
                .setConnection(configuration.getDefaultDatabaseConnection().getName())
                .addParameters(options.getAuthenticationParameters())
                .start(authCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseSignUpEvent event) {
        if (configuration.getDefaultDatabaseConnection() == null) {
            return;
        }

        AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
        apiClient.setDefaultDatabaseConnection(configuration.getDefaultDatabaseConnection().getName());

        panelHolder.showProgress(true);
        if (event.loginAfterSignUp()) {
            apiClient.getProfileAfter(apiClient.signUp(event.getEmail(), event.getPassword(), event.getUsername()))
                    .addParameters(options.getAuthenticationParameters())
                    .start(authCallback);
        } else {
            apiClient.createUser(event.getEmail(), event.getPassword(), event.getUsername())
                    .addParameters(options.getAuthenticationParameters())
                    .start(createCallback);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onDatabaseAuthenticationRequest(DatabaseChangePasswordEvent event) {
        if (configuration.getDefaultDatabaseConnection() == null) {
            return;
        }

        panelHolder.showProgress(true);
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(options.getAccount());
        apiClient.setDefaultDatabaseConnection(configuration.getDefaultDatabaseConnection().getName());
        apiClient.requestChangePassword(event.getUsernameOrEmail())
                .addParameters(options.getAuthenticationParameters())
                .start(changePwdCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEnterpriseAuthenticationRequest(EnterpriseLoginEvent event) {
        if (event.getConnectionName() == null) {
            Log.w(TAG, "No enterprise matched connection");
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
                return;
            }
        }

        panelHolder.showProgress(true);
        if (event.useRO()) {
            AuthenticationAPIClient apiClient = options.getAuthenticationAPIClient();
            apiClient.getProfileAfter(apiClient.login(event.getUsername(), event.getPassword()))
                    .setConnection(event.getConnectionName())
                    .addParameters(options.getAuthenticationParameters())
                    .start(authCallback);
            return;
        }

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
                    panelHolder.configurePanel(configuration);
                }
            });
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            applicationFetcher = null;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    panelHolder.configurePanel(null);
                }
            });
        }
    };

    private AuthCallback authProviderCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            Log.w(TAG, "OnFailure called");
            dialog.show();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(int titleResource, final int messageResource, final Throwable cause) {
            Log.w(TAG, "OnFailure called");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = new AuthenticationError(messageResource, cause).getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }

        @Override
        public void onSuccess(@NonNull final Credentials credentials) {
            Log.d(TAG, "Fetching user profile..");
            showProgressDialog(true);
            options.getAuthenticationAPIClient().tokenInfo(credentials.getIdToken())
                    .start(new BaseCallback<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile profile) {
                            Log.d(TAG, "OnSuccess called for user " + profile.getName());
                            showProgressDialog(false);
                            Authentication authentication = new Authentication(profile, credentials);
                            deliverResult(authentication);
                        }

                        @Override
                        public void onFailure(final Auth0Exception error) {
                            Log.w(TAG, "OnFailure called");
                            showProgressDialog(false);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String message = loginErrorBuilder.buildFrom(error).getMessage(LockActivity.this);
                                    showErrorMessage(message);
                                }
                            });
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
                    String message = loginErrorBuilder.buildFrom(error).getMessage(LockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };

    private BaseCallback<DatabaseUser> createCallback = new BaseCallback<DatabaseUser>() {
        @Override
        public void onSuccess(DatabaseUser payload) {
            Log.d(TAG, "User created, now login");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showSuccessMessage(getString(R.string.com_auth0_lock_db_sign_up_success_message));
                }
            });
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "User creation failed");
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
            Log.d(TAG, "Change password accepted");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showSuccessMessage(getString(R.string.com_auth0_lock_db_change_password_message_success));
                }
            });

        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.d(TAG, "Change password failed");
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
