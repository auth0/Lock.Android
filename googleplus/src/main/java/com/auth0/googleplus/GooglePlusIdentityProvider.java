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
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.TextUtils;

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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

import roboguice.RoboGuice;

/**
 * Created by hernan on 12/30/14.
 */
public class GooglePlusIdentityProvider implements IdentityProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private boolean authenticating;
    private GoogleApiClient apiClient;
    private Activity activity;
    private BusProvider provider;

    @Override
    public void start(Activity activity, SocialAuthenticationRequestEvent event, Application application) {
        this.activity = activity;
        this.provider = RoboGuice.getInjector(activity).getInstance(BusProvider.class);
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();

        }
        apiClient.connect();
    }

    @Override
    public void stop() {
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.disconnect();
        }
        apiClient = null;
        activity = null;
    }

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        this.activity = activity;
        if (requestCode == GOOGLE_PLUS_REQUEST_CODE) {
            authenticating = false;
            if (!apiClient.isConnecting()) {
                apiClient.connect();
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearSession() {
        stop();
        activity = null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            String accessToken = GoogleAuthUtil.getToken(
                    activity,
                    Plus.AccountApi.getAccountName(apiClient),
                    "oauth2:" + TextUtils.join(" " , new Object[] {"email", "profile"}));

            provider.getBus().post(new SocialCredentialEvent("google-oauth2", accessToken));
        } catch (IOException transientEx) {
            provider.getBus().post(new AuthenticationError(R.string.social_error_title, R.string.social_error_message, transientEx));
        } catch (UserRecoverableAuthException e) {
            provider.getBus().post(new AuthenticationError(R.string.social_error_title, R.string.social_error_message, e));
        } catch (GoogleAuthException authEx) {
            provider.getBus().post(new AuthenticationError(R.string.social_error_title, R.string.social_error_message, authEx));
        } catch (Exception e) {
            provider.getBus().post(new AuthenticationError(R.string.social_error_title, R.string.social_error_message, e));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), activity,
                    0).show();
            return;
        }
        if (!authenticating && result.hasResolution()) {
            try {
                authenticating = true;
                result.startResolutionForResult(activity, GOOGLE_PLUS_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                authenticating = false;
                apiClient.connect();
            }
        }
    }
}
