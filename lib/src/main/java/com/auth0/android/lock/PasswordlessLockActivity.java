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


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.auth0.Auth0Exception;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.errors.AuthenticationError;
import com.auth0.android.lock.errors.LoginAuthenticationErrorBuilder;
import com.auth0.android.lock.events.CountryCodeChangeEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.provider.AuthCallback;
import com.auth0.android.lock.provider.AuthProvider;
import com.auth0.android.lock.provider.AuthorizeResult;
import com.auth0.android.lock.provider.CallbackHelper;
import com.auth0.android.lock.provider.OAuth2WebAuthProvider;
import com.auth0.android.lock.provider.ProviderResolverManager;
import com.auth0.android.lock.utils.ActivityUIHelper;
import com.auth0.android.lock.utils.json.Application;
import com.auth0.android.lock.utils.json.ApplicationFetcher;
import com.auth0.android.lock.views.PasswordlessLockView;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Authentication;
import com.auth0.authentication.result.Credentials;
import com.auth0.authentication.result.UserProfile;
import com.auth0.callback.BaseCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class PasswordlessLockActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = PasswordlessLockActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 202;
    private static final int COUNTRY_CODE_REQUEST = 120;
    private static final long RESULT_MESSAGE_DURATION = 3000;
    private static final double KEYBOARD_OPENED_DELTA = 0.15;
    private static final long RESEND_TIMEOUT = 20 * 1000;
    private static final long CODE_TTL = 2 * 60 * 1000;

    private static final String LAST_PASSWORDLESS_TIME_KEY = "last_passwordless_time";
    private static final String LAST_PASSWORDLESS_EMAIL_NUMBER_KEY = "last_passwordless_email_number";
    private static final String LAST_PASSWORDLESS_COUNTRY_KEY = "last_passwordless_country";
    private static final String LAST_PASSWORDLESS_MODE_KEY = "last_passwordless_mode";
    private static final String LOCK_PREFERENCES_NAME = "Lock";
    private static final String COUNTRY_DATA_DIV = "@";

    private ApplicationFetcher applicationFetcher;
    private Configuration configuration;
    private Options options;
    private Handler handler;

    private PasswordlessLockView lockView;
    private LinearLayout passwordlessSuccessCover;
    private TextView resultMessage;

    private String lastPasswordlessEmailOrNumber;
    private Country lastPasswordlessCountry;
    private Bus lockBus;
    private ScrollView rootView;
    private TextView resendButton;

    private ProgressDialog progressDialog;

    private boolean keyboardIsShown;
    private ViewGroup contentView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardListener;

    private AuthProvider currentProvider;

    private LoginAuthenticationErrorBuilder loginErrorBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLaunchConfigValid()) {
            Log.d(TAG, "Configuration is not valid and the Activity will finish.");
            finish();
            return;
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        lockBus = new Bus();
        lockBus.register(this);
        handler = new Handler(getMainLooper());

        setContentView(R.layout.com_auth0_lock_activity_lock_passwordless);
        int paddingTop = ActivityUIHelper.getStatusBarHeight(this, options.isFullscreen());
        contentView = (ViewGroup) findViewById(R.id.com_auth0_lock_container);
        passwordlessSuccessCover = (LinearLayout) findViewById(R.id.com_auth0_lock_link_sent_cover);
        rootView = (ScrollView) findViewById(R.id.com_auth0_lock_content);
        resultMessage = (TextView) findViewById(R.id.com_auth0_lock_result_message);
        lockView = new PasswordlessLockView(this, lockBus);
        RelativeLayout.LayoutParams lockViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lockView.setLayoutParams(lockViewParams);
        lockView.setHeaderPadding(paddingTop);
        rootView.addView(lockView);

        resultMessage.setPadding(0, paddingTop, 0, resultMessage.getPaddingBottom());
        ActivityUIHelper.useStatusBarSpace(this, options.isFullscreen());

        if (options.useCodePasswordless()) {
            loginErrorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_lock_passwordless_code_request_error_message, R.string.com_auth0_lock_passwordless_login_error_invalid_credentials_message);
        } else {
            loginErrorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_lock_passwordless_link_request_error_message, R.string.com_auth0_lock_passwordless_login_error_invalid_credentials_message);
        }
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
            Log.e(TAG, "Lock Options are missing in the received Intent and PasswordlessLockActivity will not launch. " +
                    "Use the PasswordlessLock.Builder to generate a valid Intent.");
            return false;
        }

        boolean launchedForResult = getCallingActivity() != null;
        if (launchedForResult) {
            Log.e(TAG, "You're not allowed to start Lock with startActivityForResult.");
            return false;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //TODO: Document this case for users on <= KITKAT, as they will not receive this warning.
            boolean launchedAsSingleTask = (getIntent().getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0;
            if (options.useBrowser() && !launchedAsSingleTask) {
                Log.e(TAG, "Please, check that you have specified launchMode 'singleTask' in the AndroidManifest.");
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        boolean showingSuccessLayout = passwordlessSuccessCover.getVisibility() == View.VISIBLE;
        if (!showingSuccessLayout && lockView.onBackPressed()) {
            reloadRecentPasswordlessData();
            return;
        }
        if (!options.isClosable()) {
            return;
        }

        Log.v(TAG, "User has just closed the activity.");
        Intent intent = new Intent(Constants.CANCELED_ACTION);
        LocalBroadcastManager.getInstance(PasswordlessLockActivity.this).sendBroadcast(intent);
        super.onBackPressed();
    }

    private void deliverAuthenticationResult(Authentication result) {
        Intent intent = new Intent(Constants.AUTHENTICATION_ACTION);
        intent.putExtra(Constants.ID_TOKEN_EXTRA, result.getCredentials().getIdToken());
        intent.putExtra(Constants.ACCESS_TOKEN_EXTRA, result.getCredentials().getAccessToken());
        intent.putExtra(Constants.REFRESH_TOKEN_EXTRA, result.getCredentials().getRefreshToken());
        intent.putExtra(Constants.TOKEN_TYPE_EXTRA, result.getCredentials().getType());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
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

    private void showLinkSentLayout() {
        TextView successMessage = (TextView) passwordlessSuccessCover.findViewById(R.id.com_auth0_lock_passwordless_message);
        successMessage.setText(String.format(getString(R.string.com_auth0_lock_title_passwordless_link_sent), lastPasswordlessEmailOrNumber));
        TextView gotCodeButton = (TextView) passwordlessSuccessCover.findViewById(R.id.com_auth0_lock_got_code);
        gotCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordlessSuccessCover.setVisibility(View.GONE);
            }
        });
        resendButton = (TextView) passwordlessSuccessCover.findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendButton.setVisibility(View.GONE);
                rootView.removeView(lockView);
                lockView = new PasswordlessLockView(PasswordlessLockActivity.this, lockBus);
                if (configuration != null) {
                    lockView.configure(configuration);
                    reloadRecentPasswordlessData();
                } else {
                    lockBus.post(new FetchApplicationEvent());
                }
                rootView.addView(lockView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                passwordlessSuccessCover.setVisibility(View.GONE);
            }
        });
        passwordlessSuccessCover.setVisibility(View.VISIBLE);
        handler.removeCallbacks(resendTimeoutShower);
        handler.postDelayed(resendTimeoutShower, RESEND_TIMEOUT);
    }

    final Runnable resendTimeoutShower = new Runnable() {
        @Override
        public void run() {
            if (resendButton != null) {
                resendButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private void reloadRecentPasswordlessData() {
        int choosenMode = configuration.getPasswordlessMode();
        if (choosenMode == PasswordlessMode.DISABLED) {
            return;
        }
        SharedPreferences sp = getSharedPreferences(LOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        int savedMode = sp.getInt(LAST_PASSWORDLESS_MODE_KEY, PasswordlessMode.DISABLED);
        if (sp.getLong(LAST_PASSWORDLESS_TIME_KEY, 0) + CODE_TTL < System.currentTimeMillis() || choosenMode != savedMode) {
            Log.d(TAG, "Previous Passwordless data is too old to reload.");
            return;
        }

        String text = sp.getString(LAST_PASSWORDLESS_EMAIL_NUMBER_KEY, "");
        lastPasswordlessEmailOrNumber = text;
        String countryInfo = sp.getString(LAST_PASSWORDLESS_COUNTRY_KEY, null);
        if (countryInfo != null) {
            String isoCode = countryInfo.split(COUNTRY_DATA_DIV)[0];
            String dialCode = countryInfo.split(COUNTRY_DATA_DIV)[1];
            if (text.startsWith(dialCode)) {
                text = text.substring(dialCode.length());
            }
            lastPasswordlessCountry = new Country(isoCode, dialCode);
        }
        lockView.loadPasswordlessData(text, lastPasswordlessCountry);
    }

    private void persistRecentPasswordlessData(@NonNull String emailOrNumber, @Nullable Country country) {
        Log.v(TAG, "Saving recently used Passwordless data for the next time.");
        SharedPreferences sp = getSharedPreferences(LOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String countryData = country != null ? country.getIsoCode() + COUNTRY_DATA_DIV + country.getDialCode() : null;
        sp.edit()
                .putLong(LAST_PASSWORDLESS_TIME_KEY, System.currentTimeMillis())
                .putString(LAST_PASSWORDLESS_EMAIL_NUMBER_KEY, emailOrNumber)
                .putString(LAST_PASSWORDLESS_COUNTRY_KEY, countryData)
                .putInt(LAST_PASSWORDLESS_MODE_KEY, configuration.getPasswordlessMode())
                .apply();
    }

    public void clearRecentPasswordlessData() {
        Log.v(TAG, "Deleting recent Passwordless data.");
        SharedPreferences sp = getSharedPreferences(LOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sp.edit()
                .putLong(LAST_PASSWORDLESS_TIME_KEY, 0)
                .putString(LAST_PASSWORDLESS_EMAIL_NUMBER_KEY, "")
                .putString(LAST_PASSWORDLESS_COUNTRY_KEY, null)
                .putInt(LAST_PASSWORDLESS_MODE_KEY, PasswordlessMode.DISABLED)
                .apply();
    }

    private void showProgressDialog(final boolean show) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    progressDialog = ProgressDialog.show(PasswordlessLockActivity.this, getString(R.string.com_auth0_lock_title_social_progress_dialog), getString(R.string.com_auth0_lock_message_social_progress_dialog), true, false);
                } else if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    private void showMissingConnectionsDialog() {
        new AlertDialog.Builder(PasswordlessLockActivity.this)
                .setCancelable(false)
                .setMessage(R.string.com_auth0_lock_missing_connections_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PasswordlessLockActivity.this.finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COUNTRY_CODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String country = data.getStringExtra(CountryCodeActivity.COUNTRY_CODE_EXTRA);
                String dialCode = data.getStringExtra(CountryCodeActivity.COUNTRY_DIAL_CODE_EXTRA);
                lockView.onCountryCodeSelected(country, dialCode);
            }
            return;
        }
        processIncomingIntent(data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIncomingIntent(intent);

        super.onNewIntent(intent);
    }

    private void processIncomingIntent(Intent intent) {
        lockView.showProgress(false);
        if (intent == null) {
            return;
        }
        if (configuration == null) {
            Log.w(TAG, String.format("Intent arrived with data %s but is going to be discarded as the Activity lacks of Configuration", intent.getData()));
            return;
        }

        if (currentProvider != null) {
            AuthorizeResult result = new AuthorizeResult(intent);
            currentProvider.authorize(PasswordlessLockActivity.this, result);
            return;
        }

        boolean useMagicLink = configuration.getPasswordlessMode() == PasswordlessMode.EMAIL_LINK || configuration.getPasswordlessMode() == PasswordlessMode.SMS_LINK;
        if (lastPasswordlessEmailOrNumber != null && useMagicLink) {
            String code = intent.getData().getQueryParameter("code");
            if (code == null || code.isEmpty()) {
                Log.w(TAG, "Passwordless Code is missing or could not be parsed");
                showErrorMessage(getString(R.string.com_auth0_lock_db_login_error_message));
                return;
            }
            PasswordlessLoginEvent event = PasswordlessLoginEvent.submitCode(configuration.getPasswordlessMode(), code);
            onPasswordlessAuthenticationRequest(event);
        } else {
            Log.w(TAG, "Invalid Activity state");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (currentProvider != null) {
            currentProvider.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
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
    public void onCountryCodeChangeRequest(CountryCodeChangeEvent event) {
        Intent intent = new Intent(this, CountryCodeActivity.class);
        intent.putExtra(CountryCodeActivity.FULLSCREEN_EXTRA, options.isFullscreen());
        startActivityForResult(intent, COUNTRY_CODE_REQUEST);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPasswordlessAuthenticationRequest(PasswordlessLoginEvent event) {
        if (configuration.getDefaultPasswordlessStrategy() == null) {
            Log.w(TAG, "There is no default Passwordless strategy to authenticate with");
            return;
        }

        lockView.showProgress(true);
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(options.getAccount());
        String connectionName = configuration.getFirstConnectionOfStrategy(configuration.getDefaultPasswordlessStrategy());
        if (event.getCode() != null) {
            event.getLoginRequest(apiClient, lastPasswordlessEmailOrNumber)
                    .addParameters(options.getAuthenticationParameters())
                    .setConnection(connectionName)
                    .start(authCallback);
            return;
        }

        lastPasswordlessEmailOrNumber = event.getEmailOrNumber();
        lastPasswordlessCountry = event.getCountry();
        event.getCodeRequest(apiClient, connectionName)
                .start(passwordlessCodeCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSocialAuthenticationRequest(SocialConnectionEvent event) {
        lastPasswordlessEmailOrNumber = null;
        lastPasswordlessCountry = null;
        Log.v(TAG, "Looking for a provider to use with the connection " + event.getConnectionName());
        currentProvider = ProviderResolverManager.get().onAuthProviderRequest(this, authProviderCallback, event.getConnectionName());
        if (currentProvider == null) {
            Log.d(TAG, "Couldn't find an specific provider, using the default: " + OAuth2WebAuthProvider.class.getSimpleName());
            String pkgName = getApplicationContext().getPackageName();
            OAuth2WebAuthProvider oauth2 = new OAuth2WebAuthProvider(new CallbackHelper(pkgName), options.getAccount(), authProviderCallback, options.usePKCE());
            oauth2.setUseBrowser(options.useBrowser());
            oauth2.setIsFullscreen(options.isFullscreen());
            oauth2.setParameters(options.getAuthenticationParameters());
            currentProvider = oauth2;
        }
        currentProvider.start(this, event.getConnectionName(), PERMISSION_REQUEST_CODE);
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
                    reloadRecentPasswordlessData();
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

    private BaseCallback<Void> passwordlessCodeCallback = new BaseCallback<Void>() {
        @Override
        public void onSuccess(Void payload) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lockView.showProgress(false);
                    lockView.onPasswordlessCodeSent(lastPasswordlessEmailOrNumber);
                    if (!options.useCodePasswordless()) {
                        showLinkSentLayout();
                    }
                    persistRecentPasswordlessData(lastPasswordlessEmailOrNumber, lastPasswordlessCountry);
                }
            });
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "Failed to request a passwordless Code/Link: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String message = loginErrorBuilder.buildFrom(error).getMessage(PasswordlessLockActivity.this);
                    showErrorMessage(message);
                }
            });
        }
    };

    private BaseCallback<Authentication> authCallback = new BaseCallback<Authentication>() {
        @Override
        public void onSuccess(Authentication authentication) {
            clearRecentPasswordlessData();
            deliverAuthenticationResult(authentication);
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.e(TAG, "Failed to authenticate the user: " + error.getMessage(), error);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showErrorMessage(loginErrorBuilder.buildFrom(error).getMessage(PasswordlessLockActivity.this));
                }
            });
        }
    };

    private AuthCallback authProviderCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            Log.e(TAG, "Failed to authenticate the user. A dialog is going to be shown with more information.");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(@StringRes int titleResource, @StringRes final int messageResource, final Throwable cause) {
            final String message = new AuthenticationError(messageResource, cause).getMessage(PasswordlessLockActivity.this);
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
            showProgressDialog(true);
            options.getAuthenticationAPIClient().tokenInfo(credentials.getIdToken())
                    .start(new BaseCallback<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile profile) {
                            showProgressDialog(false);
                            Authentication authentication = new Authentication(profile, credentials);
                            deliverAuthenticationResult(authentication);
                        }

                        @Override
                        public void onFailure(final Auth0Exception error) {
                            Log.e(TAG, "Failed to fetch the user profile: " + error.getMessage(), error);
                            showProgressDialog(false);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String message = loginErrorBuilder.buildFrom(error).getMessage(PasswordlessLockActivity.this);
                                    showErrorMessage(message);
                                }
                            });
                        }
                    });
        }
    };
}
