/*
 * FetchTokenAsyncTask.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.googleplus;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.auth0.core.Strategies;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.event.SystemErrorEvent;
import com.auth0.lock.identity.IdentityProvider;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.squareup.otto.Bus;

import java.io.IOException;

public class FetchTokenAsyncTask extends AsyncTask<String, Void, Object> {

    public static final String TAG = FetchTokenAsyncTask.class.getName();
    private final GoogleApiClient apiClient;
    private final Activity context;
    private final Bus bus;

    public FetchTokenAsyncTask(GoogleApiClient apiClient, Activity context, Bus bus) {
        this.apiClient = apiClient;
        this.context = context;
        this.bus = bus;
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            String accessToken = GoogleAuthUtil.getToken(
                    context,
                    Plus.AccountApi.getAccountName(apiClient),
                    "oauth2:" + TextUtils.join(" ", params));

            return new SocialCredentialEvent(Strategies.GooglePlus.getName(), accessToken);
        } catch (IOException transientEx) {
            Log.e(TAG, "Failed to fetch G+ token", transientEx);
            return new AuthenticationError(R.string.social_error_title, R.string.social_access_denied_message, transientEx);
        } catch (GooglePlayServicesAvailabilityException e) {
            Log.w(TAG, "Google Play services not found or invalid", e);
            return new SystemErrorEvent(GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), context, 0), e);
        } catch (UserRecoverableAuthException e) {
            Log.d(TAG, "User permission from the user required in order to fetch token", e);
            context.startActivityForResult(e.getIntent(), IdentityProvider.GOOGLE_PLUS_TOKEN_REQUEST_CODE);
            return null;
        } catch (GoogleAuthException authEx) {
            return new AuthenticationError(R.string.social_error_title, R.string.social_error_message, authEx);
        } catch (Exception e) {
            return new AuthenticationError(R.string.social_error_title, R.string.social_error_message, e);
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            bus.post(result);
        }
    }
}
