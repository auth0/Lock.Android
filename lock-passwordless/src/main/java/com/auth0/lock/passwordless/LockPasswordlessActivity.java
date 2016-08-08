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

import com.auth0.api.ParameterizableRequest;
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.authentication.AuthenticationRequest;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.LockContext;
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
import com.auth0.lock.passwordless.util.AppLinkIntentParser;
import com.auth0.lock.util.ActivityUIHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class LockPasswordlessActivity extends FragmentActivity {

    private static final String TAG = LockPasswordlessActivity.class.getName();

    public static final int MODE_UNKNOWN          = 0x100;
    public static final int MODE_SMS_CODE         = 0x000;
    public static final int MODE_EMAIL_CODE       = 0x001;
    public static final int MODE_SMS_MAGIC_LINK   = 0x010;
    public static final int MODE_EMAIL_MAGIC_LINK = 0x011;

    private static final int USE_MAGIC_LINK_MASK  = 0x010;
    private static final int IS_EMAIL_MASK        = 0x001;

    private static final int REQUEST_CODE = 1234;

    public static final String PASSWORDLESS_TYPE_PARAMETER = "PASSWORDLESS_TYPE_PARAMETER";
    private static final String USERNAME_PARAMETER = "USERNAME_PARAMETER";
    private static final String IS_IN_PROGRESS_PARAMETER = "IS_IN_PROGRESS_PARAMETER";

    protected AuthenticationAPIClient client;
    protected Bus bus;
    Lock lock;
    private int passwordlessType;
    private boolean isInProgress;
    private String username;
    private LoginAuthenticationErrorBuilder errorBuilder;

    public static void showFrom(Activity activity, int passwordlessType) {
        Intent intent = new Intent(activity, LockPasswordlessActivity.class);
        if (passwordlessType != MODE_EMAIL_CODE
                && passwordlessType != MODE_EMAIL_MAGIC_LINK
                && passwordlessType != MODE_SMS_CODE
                && passwordlessType != MODE_SMS_MAGIC_LINK) {
            Log.e(TAG, "Invalid passwordless type, it must be one of {MODE_EMAIL_CODE, MODE_EMAIL_MAGIC_LINK, MODE_SMS_CODE, MODE_SMS_MAGIC_LINK}");
            return;
        }
        intent.putExtra(PASSWORDLESS_TYPE_PARAMETER, passwordlessType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_auth0_passwordless_activity_lock_passwordless);

        passwordlessType = MODE_UNKNOWN;
        lock = getLock();
        client = lock.getAuthenticationAPIClient();
        bus = lock.getBus();
        bus.register(this);

        if (savedInstanceState == null) {
            isInProgress = false;
            Intent intent = getIntent();
            boolean invalidMagicLink = false;
            int mode = intent.getIntExtra(PASSWORDLESS_TYPE_PARAMETER, MODE_UNKNOWN);
            if (mode != MODE_UNKNOWN) {
                setPasswordlessType(mode);
            } else {
                AppLinkIntentParser linkParser = new AppLinkIntentParser(intent);
                invalidMagicLink = linkParser.isAppLinkIntent();
                if (invalidMagicLink) {
                    setPasswordlessType(linkParser.getModeFromAppLink());
                }
            }

            if (passwordlessType == MODE_UNKNOWN) {
                Log.e(TAG, "Passwordless type is unknown, the intent that started the activity is "+intent);
                finish();
            }

            Fragment initialFragment = isEmailType()
                    ? RequestCodeEmailFragment.newInstance(useMagicLink())
                    : RequestCodeSmsFragment.newInstance(useMagicLink());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.com_auth0_container, initialFragment)
                    .commit();

            if (invalidMagicLink) {
                Fragment fragment = new InvalidLinkFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.com_auth0_container, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            }
        } else {
            setPasswordlessType(savedInstanceState.getInt(PASSWORDLESS_TYPE_PARAMETER));
            isInProgress = savedInstanceState.getBoolean(IS_IN_PROGRESS_PARAMETER);
            username = savedInstanceState.getString(USERNAME_PARAMETER);
        }

        errorBuilder = new LoginAuthenticationErrorBuilder(
                R.string.com_auth0_passwordless_login_error_title,
                R.string.com_auth0_passwordless_login_error_message,
                isEmailType()
                        ? R.string.com_auth0_passwordless_login_invalid_credentials_message_email
                        : R.string.com_auth0_passwordless_login_invalid_credentials_message_sms,
                R.string.com_auth0_db_login_unauthorized_error_message);

        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(PASSWORDLESS_TYPE_PARAMETER, passwordlessType);
        savedInstanceState.putBoolean(IS_IN_PROGRESS_PARAMETER, isInProgress);
        savedInstanceState.putString(USERNAME_PARAMETER, username);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent username: " + username + " intent: " + intent);

        AppLinkIntentParser linkParser = new AppLinkIntentParser(intent);
        if (username != null && linkParser.isValidAppLinkIntent()) {
            String passcode = linkParser.getCodeFromAppLinkIntent();
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
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (!isInProgress) {
            final double count = getSupportFragmentManager().getBackStackEntryCount();
            if ((!lock.isClosable() && count >= 1) || lock.isClosable()) {
                if (count == 0) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Lock.CANCEL_ACTION));
                }
                // the backstack is managed automatically
                super.onBackPressed();
            }
        }
    }

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        return LockContext.getLock(this);
    }

    protected void setPasswordlessType(int passwordlessType) {
        if (passwordlessType != MODE_UNKNOWN
                && passwordlessType != MODE_EMAIL_CODE
                && passwordlessType != MODE_EMAIL_MAGIC_LINK
                && passwordlessType != MODE_SMS_CODE
                && passwordlessType != MODE_SMS_MAGIC_LINK) {
            Log.e(TAG, "Invalid passwordless type, it must be one of {MODE_EMAIL_CODE, MODE_EMAIL_MAGIC_LINK, MODE_SMS_CODE, MODE_SMS_MAGIC_LINK}");
            return;
        }

        this.passwordlessType = passwordlessType;
    }

    protected boolean useMagicLink() {
        return 0 != (passwordlessType & USE_MAGIC_LINK_MASK);
    }

    protected boolean isEmailType() {
        return 0 != (passwordlessType & IS_EMAIL_MASK);
    }

    protected Class getCountryCodeActivityClass() {
        return CountryCodeActivity.class;
    }

    @SuppressWarnings("unused")
    @Subscribe public void onSelectCountryCodeEvent(SelectCountryCodeEvent event) {
        Intent intent = new Intent(this, getCountryCodeActivityClass());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String country = data.getStringExtra(CountryCodeActivity.COUNTRY_CODE);
            String dialCode = data.getStringExtra(CountryCodeActivity.COUNTRY_DIAL_CODE);
            Log.d(TAG, "Picked country " + country);
            bus.post(new CountryCodeSelectedEvent(country, dialCode));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeRequestedEvent(PasscodeRequestedEvent event) {
        sendRequestCode(event);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(PasscodeSentEvent event) {
        username = event.getUsername();
        Fragment fragment;

        switch (passwordlessType) {
            case MODE_EMAIL_CODE:
                fragment = PasscodeLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_email, username);
                break;
            case MODE_EMAIL_MAGIC_LINK:
                fragment = MagicLinkLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_magic_link_email, username);
                break;
            case MODE_SMS_CODE:
                fragment = PasscodeLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_sms, username);
                break;
            case MODE_SMS_MAGIC_LINK:
                fragment = MagicLinkLoginFragment.newInstance(R.string.com_auth0_passwordless_login_message_magic_link_sms, username);
                break;
            default:
                Log.e(TAG, "Can't continue. Unknown passwordless type: "+passwordlessType);
                return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commitAllowingStateLoss();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onCodeManualEntryRequested(CodeManualEntryRequestedEvent event) {
        Fragment fragment = PasscodeLoginFragment.newInstance(
                isEmailType()
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
                useMagicLink()
                        ? R.string.com_auth0_passwordless_title_in_progress_magic_link
                        : R.string.com_auth0_passwordless_title_in_progress_code,
                isEmailType()
                        ? R.string.com_auth0_passwordless_login_message_in_progress_email
                        : R.string.com_auth0_passwordless_login_message_in_progress_sms,
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
        if (isEmailType()) {
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

        final int title;
        final int message;

        ParameterizableRequest<Void> request;
        if (isEmailType()) {
            request = client.passwordlessWithEmail(username, useMagicLink());
            title = R.string.com_auth0_passwordless_send_code_error_tile_email;
            message = R.string.com_auth0_passwordless_send_code_error_message_email;
        } else {
            request = client.passwordlessWithSMS(username, useMagicLink());
            title = R.string.com_auth0_passwordless_send_code_error_tile_sms;
            message = R.string.com_auth0_passwordless_send_code_error_message_sms;
        }

        BaseCallback<Void> callback = new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                Log.d(TAG, "Passcode sent to " + username);
                if (!event.isRetry()) {
                    bus.post(new PasscodeSentEvent(username));
                }
            }

            @Override
            public void onFailure(Throwable error) {
                bus.post(new AuthenticationError(title, message, AuthenticationError.ErrorType.UNKNOWN, error));
            }
        };

        request.start(callback);
    }
}
