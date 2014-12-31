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
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.identity.IdentityProvider;
import com.auth0.lock.provider.BusProvider;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

/**
 * Created by hernan on 12/31/14.
 */
public class FetchTokenAsyncTask extends AsyncTask<String, Void, Object> {

    private final GoogleApiClient apiClient;
    private final Activity context;
    private final BusProvider provider;

    public FetchTokenAsyncTask(GoogleApiClient apiClient, Activity context, BusProvider provider) {
        this.apiClient = apiClient;
        this.context = context;
        this.provider = provider;
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            String accessToken = GoogleAuthUtil.getToken(
                    context,
                    Plus.AccountApi.getAccountName(apiClient),
                    "oauth2:" + TextUtils.join(" ", params));

            return new SocialCredentialEvent("google-oauth2", accessToken);
        } catch (IOException transientEx) {
            return new AuthenticationError(R.string.social_error_title, R.string.social_error_message, transientEx);
        } catch (UserRecoverableAuthException e) {
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
            provider.getBus().post(result);
        }
    }
}
