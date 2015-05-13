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
import android.app.Dialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.auth0.core.Strategies;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.IdentityProviderCallback;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

public class FetchTokenAsyncTask extends AsyncTask<String, Void, Object> {

    public static final String TAG = FetchTokenAsyncTask.class.getName();
    private final GoogleApiClient apiClient;
    private final Activity context;
    private final IdentityProviderCallback callback;

    public FetchTokenAsyncTask(GoogleApiClient apiClient, Activity context, IdentityProviderCallback callback) {
        this.apiClient = apiClient;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(String... params) {
        FetchTokenResultHolder holder = null;
        try {
            String accessToken = GoogleAuthUtil.getToken(
                    context,
                    Plus.AccountApi.getAccountName(apiClient),
                    "oauth2:" + TextUtils.join(" ", params));

            holder = new FetchTokenResultHolder(accessToken);
        } catch (IOException transientEx) {
            Log.e(TAG, "Failed to fetch G+ token", transientEx);
            holder = new FetchTokenResultHolder(R.string.com_auth0_social_error_title, R.string.com_auth0_social_access_denied_message, transientEx);
        } catch (GooglePlayServicesAvailabilityException e) {
            Log.w(TAG, "Google Play services not found or invalid", e);
            holder = new FetchTokenResultHolder(GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), context, 0));
        } catch (UserRecoverableAuthException e) {
            Log.d(TAG, "User permission from the user required in order to fetch token", e);
            context.startActivityForResult(e.getIntent(), IdentityProvider.GOOGLE_PLUS_TOKEN_REQUEST_CODE);
        } catch (GoogleAuthException authEx) {
            holder = new FetchTokenResultHolder(R.string.com_auth0_social_error_title, R.string.com_auth0_social_error_message, authEx);
        } catch (Exception e) {
            holder = new FetchTokenResultHolder(R.string.com_auth0_social_error_title, R.string.com_auth0_social_error_message, e);
        }
        return holder;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            FetchTokenResultHolder holder = (FetchTokenResultHolder) result;
            if (holder.getToken() != null) {
                callback.onSuccess(Strategies.GooglePlus.getName(), holder.getToken());
            } else if (holder.getDialog() != null) {
                callback.onFailure(holder.getDialog());
            } else {
                callback.onFailure(holder.getTitleError(), holder.getMessageError(), holder.getCause());
            }
        }
    }

    private class FetchTokenResultHolder {
        private String token;

        private int titleError;
        private int messageError;
        private Throwable cause;

        private Dialog dialog;

        private FetchTokenResultHolder(String token) {
            this.token = token;
        }

        private FetchTokenResultHolder(int titleError, int messageError, Throwable cause) {
            this.titleError = titleError;
            this.messageError = messageError;
            this.cause = cause;
        }

        private FetchTokenResultHolder(Dialog dialog) {
            this.dialog = dialog;
        }

        public String getToken() {
            return token;
        }

        public int getTitleError() {
            return titleError;
        }

        public int getMessageError() {
            return messageError;
        }

        public Throwable getCause() {
            return cause;
        }

        public Dialog getDialog() {
            return dialog;
        }
    }
}
