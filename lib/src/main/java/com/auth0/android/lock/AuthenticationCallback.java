/*
 * LockAuthenticationCallback.java
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

import android.content.Intent;
import android.util.Log;

import com.auth0.android.lock.utils.LockException;
import com.auth0.authentication.result.Credentials;


/**
 * Simple implementation of the Callback used by Lock to notify the user of execution results.
 * It can handle and notify of Authentication and Canceled events.
 */
public abstract class AuthenticationCallback implements LockCallback {

    private static final String TAG = AuthenticationCallback.class.getSimpleName();

    /**
     * Called when the authentication flow finished successfully.
     *
     * @param credentials with the tokens.
     */
    public abstract void onAuthentication(Credentials credentials);

    /**
     * Called when the user goes back and closes the activity, without using an Authentication flow.
     */
    public abstract void onCanceled();

    @Override
    public void onEvent(@LockEvent int event, Intent data) {
        switch (event) {
            case LockEvent.CANCELED:
                onCanceled();
                break;
            case LockEvent.AUTHENTICATION:
                parseAuthentication(data);
                break;
            case LockEvent.RESET_PASSWORD:
            case LockEvent.SIGN_UP:
                break;
        }
    }

    /**
     * Extracts the Authentication data from the intent data.
     *
     * @param data the intent received at the end of the login process.
     */
    private void parseAuthentication(Intent data) {
        String idToken = data.getStringExtra(Constants.ID_TOKEN_EXTRA);
        String accessToken = data.getStringExtra(Constants.ACCESS_TOKEN_EXTRA);
        String tokenType = data.getStringExtra(Constants.TOKEN_TYPE_EXTRA);
        String refreshToken = data.getStringExtra(Constants.REFRESH_TOKEN_EXTRA);
        Credentials credentials = new Credentials(idToken, accessToken, tokenType, refreshToken);

        if (idToken != null && accessToken != null) {
            Log.d(TAG, "User authenticated!");
            onAuthentication(credentials);
        } else {
            Log.e(TAG, "Error parsing authentication data: id_token or access_token are missing.");
            LockException up = new LockException(R.string.com_auth0_lock_social_error_authentication);
            onError(up);
            //throw up. haha
        }
    }
}
