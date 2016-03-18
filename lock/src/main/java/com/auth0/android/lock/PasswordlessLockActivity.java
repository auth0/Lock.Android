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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.Auth0Exception;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.CountryCodeChangeEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.provider.AuthorizeResult;
import com.auth0.android.lock.provider.CallbackHelper;
import com.auth0.android.lock.provider.IdentityProviderCallback;
import com.auth0.android.lock.provider.WebIdentityProvider;
import com.auth0.android.lock.utils.Application;
import com.auth0.android.lock.utils.ApplicationFetcher;
import com.auth0.android.lock.views.PasswordlessPanelHolder;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.Authentication;
import com.auth0.authentication.result.Credentials;
import com.auth0.authentication.result.UserProfile;
import com.auth0.callback.BaseCallback;
import com.auth0.request.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class PasswordlessLockActivity extends AppCompatActivity {

    private static final String TAG = PasswordlessLockActivity.class.getSimpleName();
    private static final int COUNTRY_CODE_REQUEST = 120;
    private static final long RESULT_MESSAGE_DURATION = 3000;

    private ApplicationFetcher applicationFetcher;
    private Configuration configuration;
    private Options options;
    private Handler handler;

    private PasswordlessPanelHolder panelHolder;
    private LinearLayout passwordlessSuccessCover;
    private TextView resultMessage;

    private String lastPasswordlessEmailOrNumber;
    private WebIdentityProvider lastIdp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLaunchConfigValid()) {
            finish();
            return;
        }

        Bus lockBus = new Bus();
        lockBus.register(this);
        handler = new Handler(getMainLooper());

        setContentView(R.layout.com_auth0_lock_activity_lock_passwordless);
        passwordlessSuccessCover = (LinearLayout) findViewById(R.id.com_auth0_lock_link_sent_cover);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.com_auth0_lock_content);
        resultMessage = (TextView) findViewById(R.id.com_auth0_lock_result_message);
        panelHolder = new PasswordlessPanelHolder(this, lockBus);
        rootView.addView(panelHolder, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        fetchApplication();
    }

    private void fetchApplication() {
        if (configuration == null && applicationFetcher == null) {
            applicationFetcher = new ApplicationFetcher(options.getAccount(), new OkHttpClient());
            applicationFetcher.fetch(applicationCallback);
        }
    }

    private boolean isLaunchConfigValid() {
        options = getIntent().getParcelableExtra(Lock.OPTIONS_EXTRA);
        if (options == null) {
            Log.e(TAG, "Lock Options are missing in the received Intent and PasswordlessLockActivity will not launch. " +
                    "Use the PasswordlessLock.Builder to generate a valid Intent.");
            finish();
            return false;
        }

        boolean launchedForResult = getCallingActivity() != null;
        if (options.useBrowser() && launchedForResult) {
            Log.e(TAG, "You're not able to useBrowser and startActivityForResult at the same time.");
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
        boolean showingSuccessLayout = passwordlessSuccessCover != null && passwordlessSuccessCover.getVisibility() == View.VISIBLE;
        if (!showingSuccessLayout && panelHolder.onBackPressed()) {
            return;
        }
        if (options.isClosable()) {
            Intent intent = new Intent(Lock.CANCELED_ACTION);
            LocalBroadcastManager.getInstance(PasswordlessLockActivity.this).sendBroadcast(intent);
            return;
        }
        super.onBackPressed();
    }

    private void setResultMessage(@StringRes int stringId, boolean isSuccess) {
        //TODO: This can be extracted to a custom view (reusable in the 2 activities)
        int backgroundColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            backgroundColor = getResources().getColor(isSuccess ? R.color.com_auth0_lock_result_message_success_background : R.color.com_auth0_lock_result_message_error_background, getTheme());
        } else {
            //noinspection deprecation
            backgroundColor = getResources().getColor(isSuccess ? R.color.com_auth0_lock_result_message_success_background : R.color.com_auth0_lock_result_message_error_background);
        }
        String text = getResources().getString(stringId);
        resultMessage.setBackgroundColor(backgroundColor);
        resultMessage.setVisibility(View.VISIBLE);
        resultMessage.setText(text);
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

    private void showLinkSentLayout() {
        TextView successMessage = (TextView) passwordlessSuccessCover.findViewById(R.id.com_auth0_lock_passwordless_message);
        successMessage.setText(String.format(getString(R.string.com_auth0_lock_title_passwordless_link_email_sent), lastPasswordlessEmailOrNumber));
        TextView resendButton = (TextView) passwordlessSuccessCover.findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordlessSuccessCover.setVisibility(View.GONE);
            }
        });
        passwordlessSuccessCover.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OnActivityResult called with intent: " + data);
        if (requestCode == COUNTRY_CODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String country = data.getStringExtra(CountryCodeActivity.COUNTRY_CODE);
                String dialCode = data.getStringExtra(CountryCodeActivity.COUNTRY_DIAL_CODE);
                Log.d(TAG, "Picked country " + country);
                panelHolder.onCountryCodeSelected(country, dialCode);
            }
            return;
        }
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
        panelHolder.showProgress(false);
        if (intent == null) {
            setResultMessage(R.string.com_auth0_lock_result_message_social_authentication_error, false);
            return;
        }

        if (lastIdp != null) {
            AuthorizeResult result = new AuthorizeResult(intent);
            if (!lastIdp.authorize(PasswordlessLockActivity.this, result)) {
                setResultMessage(R.string.com_auth0_lock_result_message_social_authentication_error, false);
            }
            return;
        }

        boolean useMagicLink = configuration.getPasswordlessMode() == PasswordlessMode.EMAIL_LINK || configuration.getPasswordlessMode() == PasswordlessMode.SMS_LINK;
        if (lastPasswordlessEmailOrNumber != null && useMagicLink) {
            String code = intent.getData().getQueryParameter("code");
            if (code == null || code.isEmpty()) {
                Log.w(TAG, "Passwordless Code is missing or could not be parsed");
                setResultMessage(R.string.com_auth0_lock_result_message_error_parsing_passwordless_code, false);
                return;
            }
            PasswordlessLoginEvent event = new PasswordlessLoginEvent(configuration.getPasswordlessMode(), lastPasswordlessEmailOrNumber, code);
            onPasswordlessAuthenticationRequest(event);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onFetchApplicationRequest(FetchApplicationEvent event) {
        fetchApplication();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onCountryCodeChangeRequest(CountryCodeChangeEvent event) {
        Intent intent = new Intent(this, CountryCodeActivity.class);
        startActivityForResult(intent, COUNTRY_CODE_REQUEST);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPasswordlessAuthenticationRequest(PasswordlessLoginEvent event) {
        if (configuration.getDefaultPasswordlessStrategy() == null || event.getEmailOrNumber().isEmpty()) {
            return;
        }

        panelHolder.showProgress(true);
        AuthenticationAPIClient apiClient = new AuthenticationAPIClient(options.getAccount());
        String connectionName = configuration.getFirstConnectionOfStrategy(configuration.getDefaultPasswordlessStrategy());
        if (event.getCode() != null) {
            event.getLoginRequest(apiClient)
                    .addParameters(options.getAuthenticationParameters())
                    .setConnection(connectionName)
                    .start(authCallback);
            return;
        }

        lastPasswordlessEmailOrNumber = event.getEmailOrNumber();
        event.getCodeRequest(apiClient, connectionName)
                .start(passwordlessCodeCallback);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSocialAuthenticationRequest(SocialConnectionEvent event) {
        panelHolder.showProgress(true);
        lastPasswordlessEmailOrNumber = null;
        String pkgName = getApplicationContext().getPackageName();
        CallbackHelper helper = new CallbackHelper(pkgName);
        lastIdp = new WebIdentityProvider(helper, options.getAccount(), idpCallback);
        lastIdp.setUseBrowser(options.useBrowser());
        lastIdp.setParameters(options.getAuthenticationParameters());
        lastIdp.start(PasswordlessLockActivity.this, event.getConnectionName());
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
                    setResultMessage(R.string.com_auth0_lock_result_message_generic_error, false);
                }
            });
        }
    };

    private BaseCallback<Void> passwordlessCodeCallback = new BaseCallback<Void>() {
        @Override
        public void onSuccess(Void payload) {
            Log.d(TAG, "Passwordless authentication succeeded");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    panelHolder.showProgress(false);
                    if (options.useCodePasswordless()) {
                        panelHolder.codeSent();
                    } else {
                        showLinkSentLayout();
                    }
                }
            });
        }

        @Override
        public void onFailure(final Auth0Exception error) {
            Log.d(TAG, "Passwordless authentication failed");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setResultMessage(R.string.com_auth0_lock_result_message_generic_error, false);
                    panelHolder.showProgress(false);
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
                    setResultMessage(R.string.com_auth0_lock_result_message_generic_error, false);
                }
            });
        }
    };

    private IdentityProviderCallback idpCallback = new IdentityProviderCallback() {
        @Override
        public void onFailure(@NonNull Dialog dialog) {
            Log.w(TAG, "OnFailure called");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setResultMessage(R.string.com_auth0_lock_result_message_social_authentication_error, false);
                }
            });
        }

        @Override
        public void onFailure(int titleResource, int messageResource, Throwable cause) {
            Log.w(TAG, "OnFailure called");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setResultMessage(R.string.com_auth0_lock_result_message_social_authentication_error, false);
                }
            });
        }

        @Override
        public void onSuccess(@NonNull final Credentials credentials) {
            Log.d(TAG, "Fetching user profile..");
            Request<UserProfile> request = options.getAuthenticationAPIClient().tokenInfo(credentials.getIdToken());
            request.start(new BaseCallback<UserProfile>() {
                @Override
                public void onSuccess(UserProfile profile) {
                    Log.d(TAG, "OnSuccess called for user " + profile.getName());
                    Authentication authentication = new Authentication(profile, credentials);
                    deliverResult(authentication);
                }

                @Override
                public void onFailure(final Auth0Exception error) {
                    Log.w(TAG, "OnFailure called");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setResultMessage(R.string.com_auth0_lock_result_message_social_authentication_error, false);
                        }
                    });
                }
            });
        }
    };
}
