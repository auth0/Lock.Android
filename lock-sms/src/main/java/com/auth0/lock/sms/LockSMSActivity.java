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

import com.auth0.lock.Lock;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.passwordless.LockPasswordlessActivity;
import com.auth0.lock.passwordless.event.CodeManualEntryRequestedEvent;
import com.auth0.lock.passwordless.event.LoginRequestEvent;
import com.auth0.lock.passwordless.event.PasscodeRequestedEvent;
import com.auth0.lock.passwordless.event.PasscodeSentEvent;
import com.auth0.lock.passwordless.event.SelectCountryCodeEvent;
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
 * <activity android:name="com.auth0.lock.passwordless.CountryCodeActivity" android:theme="@style/Lock.Theme"/>
 * }</pre>
 *
 * Then just start it with the helper method:
 * <pre>{@code
 * LockSMSActivity.showFrom(this, useMagicLink);
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
@Deprecated
public class LockSMSActivity extends LockPasswordlessActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static void showFrom(Activity activity, boolean useMagicLink) {
        Intent intent = new Intent(activity, LockSMSActivity.class);
        intent.putExtra(LockPasswordlessActivity.PASSWORDLESS_TYPE_PARAMETER,
                useMagicLink
                        ? LockPasswordlessActivity.MODE_SMS_MAGIC_LINK
                        : LockPasswordlessActivity.MODE_SMS_CODE);
        activity.startActivity(intent);
    }

    @Override
    @Subscribe
    public void onSelectCountryCodeEvent(SelectCountryCodeEvent event) {
        super.onSelectCountryCodeEvent(event);
    }

    @Override
    @Subscribe public void onPasscodeRequestedEvent(PasscodeRequestedEvent event) {
        super.onPasscodeRequestedEvent(event);
    }

    @Override
    @Subscribe public void onPasscodeSentEvent(PasscodeSentEvent event) {
        super.onPasscodeSentEvent(event);
    }

    @Override
    @Subscribe public void onCodeManualEntryRequested(CodeManualEntryRequestedEvent event) {
        super.onCodeManualEntryRequested(event);
    }

    @Override
    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        super.onNavigationEvent(event);
    }

    @Override
    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        super.onAuthenticationError(error);
    }

    @Override
    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        super.onAuthentication(event);
    }

    @Override
    @Subscribe public void onLoginRequest(LoginRequestEvent event) {
        super.onLoginRequest(event);
    }
}
