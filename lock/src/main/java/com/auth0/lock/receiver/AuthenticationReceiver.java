/*
 * AuthenticationReceiver.java
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

package com.auth0.lock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;

public abstract class AuthenticationReceiver extends BroadcastReceiver {

    private static final String TAG = AuthenticationReceiver.class.getName();

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (Lock.CANCEL_ACTION.equals(intent.getAction())) {
            onCancel();
        }
        if (Lock.CHANGE_PASSWORD_ACTION.equals(intent.getAction())) {
            onChangePassword();
        }
        if (Lock.AUTHENTICATION_ACTION.equals(intent.getAction())) {
            UserProfile profile = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER);
            Token token = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER);
            if (token == null && profile == null) {
                onSignUp();
            } else {
                onAuthentication(profile, token);
            }
        }
    }

    protected abstract void onAuthentication(UserProfile profile, Token token);

    protected void onSignUp() {
        Log.v(TAG, "AUTHENTICATION action received from LockActivity without credentials. User only signed up");
    }

    protected void onCancel() {
        Log.v(TAG, "CANCEL action received from LockActivity");
    }

    protected void onChangePassword() {
        Log.v(TAG, "CHANGE_PASSWORD action received from LockActivity");
    }
}
