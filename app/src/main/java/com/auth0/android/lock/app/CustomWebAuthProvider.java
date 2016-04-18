/*
 * CustomWebIdentityProvider.java
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

package com.auth0.android.lock.app;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.auth0.Auth0;
import com.auth0.android.lock.provider.AuthorizeResult;
import com.auth0.android.lock.provider.CallbackHelper;
import com.auth0.android.lock.provider.AuthProvider;
import com.auth0.android.lock.provider.AuthCallback;
import com.auth0.authentication.result.Credentials;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomWebAuthProvider extends AuthProvider {

    private static final String TAG = CustomWebAuthProvider.class.getName();

    private static final int WEBVIEW_AUTH_REQUEST_CODE = 600;

    private static final String AUTH0_CLIENT_ID = "Owu62gnGsRYhk1v9SfB3c6IUbIJcRIze";
    private static final String AUTH0_DOMAIN = "lbalmaceda.auth0.com";

    private static final String KEY_ERROR = "error";
    private static final String KEY_ID_TOKEN = "id_token";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_RESPONSE_TYPE = "response_type";
    private static final String KEY_STATE = "state";
    private static final String KEY_CONNECTION = "connection";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";

    private static final String ERROR_VALUE_ACCESS_DENIED = "access_denied";
    private static final String RESPONSE_TYPE_TOKEN = "token";
    private static final String SCOPE_TYPE_OPENID = "openid";

    private CallbackHelper helper;
    private final Auth0 account;
    private Map<String, Object> parameters;
    private String lastState;

    public CustomWebAuthProvider(@NonNull AuthCallback callback) {
        super(callback);
        this.helper = new CallbackHelper("com.auth0.android.lock.app");
        this.account = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);
        this.parameters = new HashMap<>();
    }

    @Override
    public String[] getRequiredAndroidPermissions() {
        return new String[]{Manifest.permission.GET_ACCOUNTS};
    }

    @Override
    protected void requestAuth(final Activity activity, final String connectionName) {
        new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle(R.string.native_provider_title)
                .setMessage(R.string.native_provider_message_start)
                .setNegativeButton(R.string.native_provider_action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onFailure(R.string.native_provider_title, R.string.native_provider_message_canceled, null);
                    }
                })
                .setPositiveButton(R.string.native_provider_action_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (account.getAuthorizeUrl() == null) {
                            callback.onFailure(com.auth0.android.lock.R.string.com_auth0_lock_social_error_title, com.auth0.android.lock.R.string.com_auth0_lock_social_invalid_authorize_url, null);
                            return;
                        }

                        //Generate random state
                        lastState = UUID.randomUUID().toString();
                        startAuthorization(activity, buildAuthorizeUri(account.getAuthorizeUrl(), connectionName, lastState, parameters));
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void startAuthorization(Activity activity, Uri authorizeUri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, authorizeUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
    }

    @Override
    public boolean authorize(Activity activity, @NonNull AuthorizeResult data) {
        if (!data.isValid(WEBVIEW_AUTH_REQUEST_CODE)) {
            return false;
        }

        final Map<String, String> values = helper.getValuesFromUri(data.getIntent().getData());
        if (values.containsKey(KEY_ERROR)) {
            new android.support.v7.app.AlertDialog.Builder(activity)
                    .setTitle(R.string.native_provider_title)
                    .setMessage(R.string.native_provider_message_failed)
                    .show();
            Log.e(TAG, "Error, access denied.");
            final int message = ERROR_VALUE_ACCESS_DENIED.equalsIgnoreCase(values.get(KEY_ERROR)) ? com.auth0.android.lock.R.string.com_auth0_lock_social_access_denied_message : com.auth0.android.lock.R.string.com_auth0_lock_social_error_message;
            callback.onFailure(com.auth0.android.lock.R.string.com_auth0_lock_social_error_title, message, null);
        } else if (values.containsKey(KEY_STATE) && !values.get(KEY_STATE).equals(lastState)) {
            new android.support.v7.app.AlertDialog.Builder(activity)
                    .setTitle(R.string.native_provider_title)
                    .setMessage(R.string.native_provider_message_failed)
                    .show();
            Log.e(TAG, "Received state doesn't match");
            Log.d(TAG, "Expected: " + lastState + " / Received: " + values.get(KEY_STATE));
            callback.onFailure(com.auth0.android.lock.R.string.com_auth0_lock_social_error_title, com.auth0.android.lock.R.string.com_auth0_lock_social_invalid_state, null);
        } else if (values.size() > 0) {
            new android.support.v7.app.AlertDialog.Builder(activity)
                    .setTitle(R.string.native_provider_title)
                    .setMessage(R.string.native_provider_message_succeeded)
                    .show();
            Log.d(TAG, "Authenticated using web flow");
            callback.onSuccess(new Credentials(values.get(KEY_ID_TOKEN), values.get(KEY_ACCESS_TOKEN), values.get(KEY_TOKEN_TYPE), values.get(KEY_REFRESH_TOKEN)));
        }
        return true;
    }

    @Override
    public void stop() {
    }

    @Override
    public void clearSession() {
    }

    private Uri buildAuthorizeUri(String url, String serviceName, String state, Map<String, Object> parameters) {
        final Uri authorizeUri = Uri.parse(url);

        //refactor >
        final Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(KEY_SCOPE, SCOPE_TYPE_OPENID);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    queryParameters.put(entry.getKey(), value.toString());
                }
            }
        }

        queryParameters.put(KEY_RESPONSE_TYPE, RESPONSE_TYPE_TOKEN);
        queryParameters.put(KEY_STATE, state);
        queryParameters.put(KEY_CONNECTION, serviceName);
        queryParameters.put(KEY_CLIENT_ID, account.getClientId());
        queryParameters.put(KEY_REDIRECT_URI, helper.getCallbackURI(account.getDomainUrl()));
        final Uri.Builder builder = authorizeUri.buildUpon();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}