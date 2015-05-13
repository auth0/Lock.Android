/*
 * GooglePlusIdentityProvider.java
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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.IdentityProviderCallback;
import com.auth0.identity.IdentityProviderRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class GooglePlusIdentityProvider implements IdentityProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = GooglePlusIdentityProvider.class.getName();
    private final GoogleApiClient apiClient;
    private boolean authenticating;
    private Activity activity;
    private IdentityProviderCallback callback;

    public GooglePlusIdentityProvider(Context context) {
        this.apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void setCallback(IdentityProviderCallback callback) {
        this.callback = callback;
    }

    @Override
    public void start(Activity activity, IdentityProviderRequest event, Application application) {
        this.activity = activity;
        Log.v(TAG, "Starting G+ connection");
        final int availabilityStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (availabilityStatus != ConnectionResult.SUCCESS) {
            Log.w(TAG, "Google services availability failed with status " + availabilityStatus);
            callback.onFailure(GooglePlayServicesUtil.getErrorDialog(availabilityStatus, activity, 0));
            stop();
            return;
        }
        apiClient.connect();
        authenticating = true;
    }

    @Override
    public void stop() {
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
        activity = null;
    }

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        this.activity = activity;
        if (requestCode == GOOGLE_PLUS_REQUEST_CODE) {
            Log.v(TAG, "Received request activity result " + resultCode);
            if (!apiClient.isConnecting()) {
                apiClient.connect();
            }
            return true;
        } else if(requestCode == GOOGLE_PLUS_TOKEN_REQUEST_CODE) {
            Log.v(TAG, "Received token request activity result " + resultCode);
            apiClient.connect();
        }
        return false;
    }

    @Override
    public void clearSession() {
        try {
            Plus.AccountApi.clearDefaultAccount(apiClient);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to clear G+ Session", e);
        } finally {
            stop();
            activity = null;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        authenticating = false;
        new FetchTokenAsyncTask(apiClient, activity, callback).execute("email", "profile");
    }

    @Override
    public void onConnectionSuspended(int code) {
        Log.v(TAG, "Connection suspended with code: " + code);
        authenticating = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG, "Connection failed with code " + result.getErrorCode());
        if (result.getErrorCode() == ConnectionResult.SERVICE_MISSING) {
            Log.e(TAG, "service not available");
            callback.onFailure(GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity, 0));
        } else if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED && authenticating) {
            authenticating = false;
            Log.v(TAG, "G+ Sign in required");
            final PendingIntent mSignInIntent = result.getResolution();
            try {
                activity.startIntentSenderForResult(mSignInIntent.getIntentSender(), GOOGLE_PLUS_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException ignore) {
                apiClient.connect();
                Log.w(TAG, "G+ pending intent cancelled", ignore);
            }
        } else {
            Log.e(TAG, "Connection failed with unrecoverable error");
            callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_error_message, null);
        }
    }
}
