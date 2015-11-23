/*
 * LockSMSActivity.java
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

package com.auth0.lock.sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.web.AppLinkParser;
import com.auth0.lock.Lock;
import com.auth0.lock.error.ErrorDialogBuilder;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.sms.event.CodeManualEntryRequestedEvent;
import com.auth0.lock.sms.event.CountryCodeSelectedEvent;
import com.auth0.lock.sms.event.LoginRequestEvent;
import com.auth0.lock.sms.event.SelectCountryCodeEvent;
import com.auth0.lock.sms.event.SmsPasscodeRequestedEvent;
import com.auth0.lock.sms.event.SmsPasscodeSentEvent;
import com.auth0.lock.sms.fragment.InProgressFragment;
import com.auth0.lock.sms.fragment.InvalidLinkFragment;
import com.auth0.lock.sms.fragment.MagicLinkLoginFragment;
import com.auth0.lock.sms.fragment.RequestCodeFragment;
import com.auth0.lock.sms.fragment.SmsLoginFragment;
import com.auth0.lock.util.ActivityUIHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


/**
 * Activity that handles passwordless authentication using SMS.
 * You'll need to declare it in your app's {@code AndroidManifest.xml}:
 * <pre>{@code
 * <activity
 *  android:name="com.auth0.lock.sms.LockSMSActivity"
 *  android:theme="@style/Lock.Theme"
 *  android:label="@string/app_name"
 *  android:screenOrientation="portrait"
 *  android:launchMode="singleTask"/>
 * <activity android:name="com.auth0.lock.sms.CountryCodeActivity" android:theme="@style/Lock.Theme"/>
 * }</pre>
 *
 * Then just start it like any other Android activity:
 * <pre>{@code
 * Intent lockIntent = new Intent(this, LockSMSActivity.class);
 * startActivity(lockIntent);
 * }
 * </pre>
 *
 * And finally register listeners in {@link android.support.v4.content.LocalBroadcastManager} for these actions:
 * <ul>
 *     <li><b>Lock.Authentication</b>: Sent on a successful authentication with {@link com.auth0.core.UserProfile} and {@link com.auth0.core.Token}.
 *     Or both {@code null} when {@link Lock#loginAfterSignUp} is {@code false} </li>
 *     <li><b>Lock.Cancel</b>: Sent when the user's closes the activity by pressing the back button without authenticating. (Only if {@link Lock#closable} is {@code true}</li>
 * </ul>
 *
 * All these action names are defined in these constants: {@link Lock#AUTHENTICATION_ACTION} and {@link Lock#CANCEL_ACTION}.
 */
public class LockSMSActivity extends FragmentActivity {

    /**
     * @deprecated There is no need to provide a API v2 JWT, it will use a special enpoint for SMS Authentication
     */
    public static final String REQUEST_SMS_CODE_JWT = "REQUEST_SMS_CODE_JWT";

    private static final String TAG = LockSMSActivity.class.getName();

    private static final int REQUEST_CODE = 0;

    private static final String PHONE_NUMBER_PARAMETER = "PHONE_NUMBER_PARAMETER";
    private static final String USE_MAGIC_LINK_PARAMETER = "USE_MAGIC_LINK_PARAMETER";
    private static final String IS_IN_PROGRESS_PARAMETER = "IS_IN_PROGRESS_PARAMETER";

    Lock lock;

    private boolean isInProgress;
    private boolean useMagicLink;
    private String phoneNumber;
    private LoginAuthenticationErrorBuilder errorBuilder;

    protected AuthenticationAPIClient client;
    protected Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_auth0_activity_lock_sms);

        lock = getLock();
        client = lock.getAuthenticationAPIClient();
        bus = lock.getBus();
        errorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_sms_login_error_title, R.string.com_auth0_sms_login_error_message, R.string.com_auth0_sms_login_invalid_credentials_message);

        if (savedInstanceState == null) {
            isInProgress = false;
            boolean invalidMagicLink = Intent.ACTION_VIEW.equals(getIntent().getAction());
            useMagicLink = invalidMagicLink || getIntent().getBooleanExtra(USE_MAGIC_LINK_PARAMETER, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.com_auth0_container, RequestCodeFragment.newInstance(useMagicLink))
                    .commit();
            if (invalidMagicLink) {
                Fragment fragment = new InvalidLinkFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.com_auth0_container, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            }
        } else {
            isInProgress = savedInstanceState.getBoolean(IS_IN_PROGRESS_PARAMETER);
            useMagicLink = savedInstanceState.getBoolean(USE_MAGIC_LINK_PARAMETER);
            phoneNumber = savedInstanceState.getString(PHONE_NUMBER_PARAMETER);
        }

        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_IN_PROGRESS_PARAMETER, isInProgress);
        savedInstanceState.putBoolean(USE_MAGIC_LINK_PARAMETER, useMagicLink);
        savedInstanceState.putString(PHONE_NUMBER_PARAMETER, phoneNumber);

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

        Log.d(TAG, "onStart phoneNumber: " + phoneNumber + " intent: " + getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent phoneNumber: " + phoneNumber + " intent: " + intent);

        AppLinkParser linkParser = new AppLinkParser();
        String passcode = linkParser.getCodeFromAppLinkIntent(intent);
        performLogin(new LoginRequestEvent(phoneNumber, passcode));
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
    @Subscribe public void onPasscodeRequestedEvent(SmsPasscodeRequestedEvent event) {
        sendRequestCode(event);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(SmsPasscodeSentEvent event) {
        phoneNumber = event.getPhoneNumber();
        Fragment fragment;
        if (!useMagicLink) {
            fragment = SmsLoginFragment.newInstance(phoneNumber);
        } else {
            fragment = MagicLinkLoginFragment.newInstance(phoneNumber);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onCodeManualEntryRequested(CodeManualEntryRequestedEvent event) {
        Fragment fragment = SmsLoginFragment.newInstance(phoneNumber);
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

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        return Lock.getLock(this);
    }

    private void performLogin(LoginRequestEvent event) {
        Fragment fragment = InProgressFragment.newInstance(
                useMagicLink ? R.string.com_auth0_sms_title_in_progress_magic_link : R.string.com_auth0_sms_title_in_progress_code, phoneNumber);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
        isInProgress = true;

        client.loginWithPhoneNumber(event.getUsername(), event.getPasscode())
                .addParameters(lock.getAuthenticationParameters())
                .start(new AuthenticationCallback() {
                    @Override
                    public void onSuccess(UserProfile userProfile, Token token) {
                        bus.post(new AuthenticationEvent(userProfile, token));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        bus.post(errorBuilder.buildFrom(throwable));
                    }
                });
    }

    private void sendRequestCode(final SmsPasscodeRequestedEvent event) {
        phoneNumber = event.getPhoneNumber();
        client.passwordlessWithSMS(phoneNumber, useMagicLink).start(new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                Log.d(TAG, "SMS code sent to " + phoneNumber);
                if (!event.isRetry()) {
                    bus.post(new SmsPasscodeSentEvent(phoneNumber));
                }
            }

            @Override
            public void onFailure(Throwable error) {
                    bus.post(new AuthenticationError(R.string.com_auth0_sms_send_code_error_tile, R.string.com_auth0_sms_send_code_error_message, error));
            }
        });
    }

    public static void showFrom(Activity activity, boolean useMagicLink) {
        Intent intent = new Intent(activity, LockSMSActivity.class);
        intent.putExtra(LockSMSActivity.USE_MAGIC_LINK_PARAMETER, useMagicLink);
        activity.startActivity(intent);
    }
}
