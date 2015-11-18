/*
 * LockPasswordlessActivity.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.lock.passwordless;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.authentication.AuthenticationRequest;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.web.LinkParser;
import com.auth0.lock.Lock;
import com.auth0.lock.error.ErrorDialogBuilder;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.passwordless.event.CodeManualEntryRequestedEvent;
import com.auth0.lock.passwordless.event.CountryCodeSelectedEvent;
import com.auth0.lock.passwordless.event.LoginRequestEvent;
import com.auth0.lock.passwordless.event.PasscodeRequestedEvent;
import com.auth0.lock.passwordless.event.PasscodeSentEvent;
import com.auth0.lock.passwordless.event.SelectCountryCodeEvent;
import com.auth0.lock.passwordless.fragment.InProgressFragment;
import com.auth0.lock.passwordless.fragment.InvalidLinkFragment;
import com.auth0.lock.passwordless.fragment.MagicLinkLoginFragment;
import com.auth0.lock.passwordless.fragment.PasscodeLoginFragment;
import com.auth0.lock.passwordless.fragment.RequestCodeEmailFragment;
import com.auth0.lock.passwordless.fragment.RequestCodeSmsFragment;
import com.auth0.lock.util.ActivityUIHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class LockPasswordlessActivity extends FragmentActivity {

    private static final String TAG = LockPasswordlessActivity.class.getName();

    public static final int TYPE_EMAIL = 0;
    public static final int TYPE_SMS   = 1;

    private static final int REQUEST_CODE = 1234;

    private static final String PASSWORDLESS_TYPE_PARAMETER = "PASSWORDLESS_TYPE_PARAMETER";
    private static final String USERNAME_PARAMETER = "USERNAME_PARAMETER";
    private static final String USE_MAGIC_LINK_PARAMETER = "USE_MAGIC_LINK_PARAMETER";
    private static final String IS_IN_PROGRESS_PARAMETER = "IS_IN_PROGRESS_PARAMETER";

    protected AuthenticationAPIClient client;
    protected Bus bus;
    Lock lock;
    private int passwordlessType;
    private boolean isInProgress;
    private boolean useMagicLink;
    private String username;
    private LoginAuthenticationErrorBuilder errorBuilder;

    public static void showFrom(Activity activity, int passwordlessType, boolean useMagicLink) {
        Intent intent = new Intent(activity, LockPasswordlessActivity.class);
        if (passwordlessType != TYPE_EMAIL && passwordlessType != TYPE_SMS) {
            Log.e(TAG, "Invalid passwordless type, it must be either TYPE_EMAIL or TYPE_SMS");
            return;
        }
        intent.putExtra(PASSWORDLESS_TYPE_PARAMETER, passwordlessType);
        intent.putExtra(USE_MAGIC_LINK_PARAMETER, useMagicLink);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_auth0_passwordless_activity_lock_passwordless);

        lock = getLock();
        client = lock.getAuthenticationAPIClient();
        bus = lock.getBus();

        if (savedInstanceState == null) {
            isInProgress = false;
            Intent intent = getIntent();
            passwordlessType = intent.getIntExtra(PASSWORDLESS_TYPE_PARAMETER, TYPE_EMAIL);
            boolean invalidMagicLink = Intent.ACTION_VIEW.equals(intent.getAction());
            if (invalidMagicLink) {
                String dataString = intent.getDataString();
                if (dataString.contains("/sms?code")) {
                    passwordlessType = TYPE_SMS;
                }
            }
            useMagicLink = invalidMagicLink || intent.getBooleanExtra(USE_MAGIC_LINK_PARAMETER, false);
            if (passwordlessType == TYPE_EMAIL) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.com_auth0_container, RequestCodeEmailFragment.newInstance(useMagicLink))
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.com_auth0_container, RequestCodeSmsFragment.newInstance(useMagicLink))
                        .commit();
            }
            if (invalidMagicLink) {
                Fragment fragment = new InvalidLinkFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.com_auth0_container, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            }
        } else {
            passwordlessType = savedInstanceState.getInt(PASSWORDLESS_TYPE_PARAMETER);
            isInProgress = savedInstanceState.getBoolean(IS_IN_PROGRESS_PARAMETER);
            useMagicLink = savedInstanceState.getBoolean(USE_MAGIC_LINK_PARAMETER);
            username = savedInstanceState.getString(USERNAME_PARAMETER);
        }

        if (passwordlessType == TYPE_EMAIL) {
            errorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_passwordless_login_error_title, R.string.com_auth0_passwordless_login_error_message, R.string.com_auth0_passwordless_login_invalid_credentials_message_email);
        } else {
            errorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_passwordless_login_error_title, R.string.com_auth0_passwordless_login_error_message, R.string.com_auth0_passwordless_login_invalid_credentials_message_sms);
        }

        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(PASSWORDLESS_TYPE_PARAMETER, passwordlessType);
        savedInstanceState.putBoolean(IS_IN_PROGRESS_PARAMETER, isInProgress);
        savedInstanceState.putBoolean(USE_MAGIC_LINK_PARAMETER, useMagicLink);
        savedInstanceState.putString(USERNAME_PARAMETER, username);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lock.getBus().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent username: " + username + " intent: " + intent);

        if (username != null) {
            LinkParser linkParser = new LinkParser();
            String passcode = linkParser.getCodeFromAppLinkIntent(intent);
            performLogin(new LoginRequestEvent(username, passcode));
        } else {
            Fragment fragment = new InvalidLinkFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.com_auth0_container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lock.getBus().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (!isInProgress) {
            // the backstack is managed automatically
            super.onBackPressed();
        }
    }

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        return Lock.getLock(this);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onSelectCountryCodeEvent(SelectCountryCodeEvent event) {
        Intent intent = new Intent(this, CountryCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String country = data.getStringExtra(CountryCodeActivity.COUNTRY_CODE);
            String dialCode = data.getStringExtra(CountryCodeActivity.COUNTRY_DIAL_CODE);
            Log.d(TAG, "Picked country " + country);
            lock.getBus().post(new CountryCodeSelectedEvent(country, dialCode));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeRequestedEvent(PasscodeRequestedEvent event) {
        if (passwordlessType == TYPE_EMAIL) {
            sendEmail(event);
        } else {
            sendRequestCode(event);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(PasscodeSentEvent event) {
        username = event.getUsername();
        Fragment fragment;

        if (passwordlessType == TYPE_EMAIL) {
            if (!useMagicLink) {
                fragment = PasscodeLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_email, username);
            } else {
                fragment = MagicLinkLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_magic_link_email, username);
            }
        } else {
            if (!useMagicLink) {
                fragment = PasscodeLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_sms, username);
            } else {
                fragment = MagicLinkLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_magic_link_sms, username);
            }
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onCodeManualEntryRequested(CodeManualEntryRequestedEvent event) {
        Fragment fragment = PasscodeLoginFragment.newInstance(
                passwordlessType == TYPE_EMAIL
                        ? R.string.com_auth0_passwordless_login_message_email
                        : R.string.com_auth0_passwordless_login_message_sms
                , username);

        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        switch (event) {
            case BACK:
                onBackPressed();
                break;
            default:
                Log.v(TAG, "Invalid navigation event " + event);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(TAG, "Failed to authenticate user", error.getThrowable());
        ErrorDialogBuilder.showAlertDialog(this, error);

        if (isInProgress) {
            isInProgress = false;
            getSupportFragmentManager().popBackStack();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Token token = event.getToken();
        Log.i(TAG, "Authenticated user " + profile.getName());
        Intent result = new Intent(Lock.AUTHENTICATION_ACTION)
                .putExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER, profile)
                .putExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(result);
        finish();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onLoginRequest(LoginRequestEvent event) {
        performLogin(event);
    }

    private void performLogin(LoginRequestEvent event) {
        Fragment fragment = InProgressFragment.newInstance(
                useMagicLink ? R.string.com_auth0_passwordless_title_in_progress_magic_link : R.string.com_auth0_passwordless_title_in_progress_code,
                passwordlessType == TYPE_EMAIL ? R.string.com_auth0_passwordless_login_message_in_progress_email : R.string.com_auth0_passwordless_login_message_in_progress_sms,
                event.getUsername());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
        isInProgress = true;

        AuthenticationCallback authCallback = new AuthenticationCallback() {
            @Override
            public void onSuccess(UserProfile userProfile, Token token) {
                bus.post(new AuthenticationEvent(userProfile, token));
            }

            @Override
            public void onFailure(Throwable throwable) {
                bus.post(errorBuilder.buildFrom(throwable));
            }
        };

        AuthenticationRequest request;
        if (passwordlessType == TYPE_EMAIL) {
            request = client.loginWithEmail(event.getUsername(), event.getPasscode());
        } else {
            request = client.loginWithPhoneNumber(event.getUsername(), event.getPasscode());
        }
        request
                .addParameters(lock.getAuthenticationParameters())
                .start(authCallback);
    }

    private void sendRequestCode(final PasscodeRequestedEvent event) {
        username = event.getUsername();
        client.passwordlessWithSMS(username, useMagicLink).start(new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                Log.d(TAG, "SMS code sent to " + username);
                if (!event.isRetry()) {
                    bus.post(new PasscodeSentEvent(username));
                }
            }

            @Override
            public void onFailure(Throwable error) {
                bus.post(new AuthenticationError(R.string.com_auth0_passwordless_send_code_error_tile_sms, R.string.com_auth0_passwordless_send_code_error_message_sms, error));
            }
        });
    }

    private void sendEmail(final PasscodeRequestedEvent event) {
        username = event.getUsername();
        client.passwordlessWithEmail(username, useMagicLink).start(new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                Log.d(TAG, "Email code sent to " + username);
                if (!event.isRetry()) {
                    bus.post(new PasscodeSentEvent(username));
                }
            }

            @Override
            public void onFailure(Throwable error) {
                bus.post(new AuthenticationError(R.string.com_auth0_passwordless_send_code_error_tile_email, R.string.com_auth0_passwordless_send_code_error_message_email, error));
            }
        });
    }
}
