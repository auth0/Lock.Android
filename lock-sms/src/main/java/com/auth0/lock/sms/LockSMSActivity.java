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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.error.ErrorDialogBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.sms.event.CountryCodeSelectedEvent;
import com.auth0.lock.sms.event.SelectCountryCodeEvent;
import com.auth0.lock.sms.event.SmsPasscodeSentEvent;
import com.auth0.lock.sms.fragment.RequestCodeFragment;
import com.auth0.lock.sms.fragment.SmsLoginFragment;
import com.auth0.lock.util.ActivityUIHelper;
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

    Lock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_auth0_activity_lock_sms);
        lock = getLock();
        if (savedInstanceState == null) {
            final RequestCodeFragment fragment = new RequestCodeFragment();
            Bundle arguments = new Bundle();
            arguments.putString(RequestCodeFragment.REQUEST_CODE_JWT_ARGUMENT, getIntent().getStringExtra(REQUEST_SMS_CODE_JWT));
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.com_auth0_container, fragment)
                    .commit();
        }

        ActivityUIHelper.configureScreenModeForActivity(this, lock);
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
    protected void onStop() {
        super.onStop();
        lock.getBus().unregister(this);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onSelectCountryCodeEvent(SelectCountryCodeEvent event) {
        Intent intent = new Intent(this, CountryCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(SmsPasscodeSentEvent event) {
        final SmsLoginFragment fragment = new SmsLoginFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SmsLoginFragment.PHONE_NUMBER_ARGUMENT, event.getPhoneNumber());
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(SmsLoginFragment.class.getName())
                .commit();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        switch (event) {
            case BACK:
                getSupportFragmentManager().popBackStack();
                break;
            default:
                Log.v(TAG, "Invalid navigation event " + event);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(TAG, "Failed to authenticate user", error.getThrowable());
        ErrorDialogBuilder.showAlertDialog(this, error);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String country = data.getStringExtra(CountryCodeActivity.COUNTRY_CODE);
            String dialCode = data.getStringExtra(CountryCodeActivity.COUNTRY_DIAL_CODE);
            Log.d(TAG, "Picked country " + country);
            lock.getBus().post(new CountryCodeSelectedEvent(country, dialCode));
        }
    }

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        return Lock.getLock(this);
    }
}
