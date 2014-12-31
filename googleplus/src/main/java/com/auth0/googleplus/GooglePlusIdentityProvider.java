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
import android.text.TextUtils;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.SocialAuthenticationRequestEvent;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.identity.IdentityProvider;
import com.auth0.lock.provider.BusProvider;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

import roboguice.RoboGuice;

/**
 * Created by hernan on 12/30/14.
 */
public class GooglePlusIdentityProvider implements IdentityProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = GooglePlusIdentityProvider.class.getName();
    private final GoogleApiClient apiClient;
    private boolean authenticating;
    private Activity activity;
    private BusProvider provider;

    public GooglePlusIdentityProvider(Context context) {
        this.apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public void start(Activity activity, SocialAuthenticationRequestEvent event, Application application) {
        this.activity = activity;
        this.provider = RoboGuice.getInjector(activity).getInstance(BusProvider.class);
        Log.v(TAG, "Starting G+ connection");
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
            Log.v(TAG, "Received activity result " + resultCode);
            if (!apiClient.isConnecting()) {
                apiClient.connect();
            }
            return true;
        } else if(requestCode == GOOGLE_PLUS_TOKEN_REQUEST_CODE) {
            Log.v(TAG, "Received activity result " + resultCode);
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
        new FetchTokenAsyncTask(apiClient, activity, provider).execute("email", "profile");
    }

    @Override
    public void onConnectionSuspended(int code) {
        Log.v(TAG, "Connection suspended with code: " + code);
        authenticating = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG, "Connection failed with code " + result.getErrorCode());
        if (result.getErrorCode() == ConnectionResult.SERVICE_MISSING) { // e.g. emulator without play services installed
            Log.e(TAG, "service not available");
        } else if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED && authenticating) {
            authenticating = false;
            Log.v(TAG, "G+ Sign in required");
            final PendingIntent mSignInIntent = result.getResolution();
            try {
                activity.startIntentSenderForResult(mSignInIntent.getIntentSender(), GOOGLE_PLUS_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException ignore) {
                apiClient.connect();
            }
        } else {
            Log.e(TAG, "Invalid Token");
        }
    }
}
