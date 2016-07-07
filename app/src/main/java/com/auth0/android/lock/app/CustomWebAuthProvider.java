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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.provider.AuthProvider;
import com.auth0.android.result.Credentials;

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

    private final Context context;
    private final String connectionName;
    private final Auth0 account;
    private String lastState;

    public CustomWebAuthProvider(Context context, String connectionName) {
        this.context = context;
        this.connectionName = connectionName;
        this.account = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);
    }

    @Override
    public String[] getRequiredAndroidPermissions() {
        return new String[]{Manifest.permission.GET_ACCOUNTS};
    }

    private void startAuthorization(Activity activity, Uri authorizeUri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, authorizeUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
    }

    @Override
    protected void requestAuth(final Activity activity, int requestCode) {
        createDialog(R.string.native_provider_message_start)
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
                        startAuthorization(activity, buildAuthorizeUri(account.getAuthorizeUrl()));
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean authorize(int requestCode, int resultCode, @Nullable Intent intent) {
        Uri uri = intent != null ? intent.getData() : null;
        if (uri == null || requestCode != WEBVIEW_AUTH_REQUEST_CODE || resultCode != Activity.RESULT_OK) {
            return false;
        }
        return parseValidResult(intent);
    }

    @Override
    public boolean authorize(@Nullable Intent intent) {
        Uri uri = intent != null ? intent.getData() : null;
        return uri != null && parseValidResult(intent);
    }

    private boolean parseValidResult(Intent data) {
        final Map<String, String> values = getValuesFromUri(data.getData());
        if (values.containsKey(KEY_ERROR)) {
            createDialog(R.string.native_provider_message_failed)
                    .show();
            Log.e(TAG, "Error, access denied.");
            final int message = ERROR_VALUE_ACCESS_DENIED.equalsIgnoreCase(values.get(KEY_ERROR)) ? com.auth0.android.lock.R.string.com_auth0_lock_social_access_denied_message : com.auth0.android.lock.R.string.com_auth0_lock_social_error_message;
            callback.onFailure(com.auth0.android.lock.R.string.com_auth0_lock_social_error_title, message, null);
        } else if (values.containsKey(KEY_STATE) && !values.get(KEY_STATE).equals(lastState)) {
            createDialog(R.string.native_provider_message_failed)
                    .show();
            Log.e(TAG, "Received state doesn't match");
            Log.d(TAG, "Expected: " + lastState + " / Received: " + values.get(KEY_STATE));
            callback.onFailure(com.auth0.android.lock.R.string.com_auth0_lock_social_error_title, com.auth0.android.lock.R.string.com_auth0_lock_social_invalid_state, null);
        } else if (values.size() > 0) {
            createDialog(R.string.native_provider_message_succeeded)
                    .show();
            Log.d(TAG, "Authenticated using web flow");
            callback.onSuccess(new Credentials(values.get(KEY_ID_TOKEN), values.get(KEY_ACCESS_TOKEN), values.get(KEY_TOKEN_TYPE), values.get(KEY_REFRESH_TOKEN)));
        }
        return true;
    }

    private AlertDialog.Builder createDialog(@StringRes int messageRes) {
        return new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(R.string.native_provider_title)
                .setMessage(messageRes);
    }

    private Uri buildAuthorizeUri(String url) {
        final Uri authorizeUri = Uri.parse(url);

        final Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(KEY_SCOPE, SCOPE_TYPE_OPENID);
        queryParameters.put(KEY_RESPONSE_TYPE, RESPONSE_TYPE_TOKEN);
        queryParameters.put(KEY_STATE, lastState);
        queryParameters.put(KEY_CONNECTION, connectionName);
        queryParameters.put(KEY_CLIENT_ID, account.getClientId());
        String callbackURI = getCallbackURI(account.getDomainUrl(), "com.auth0.android.lock.app");
        queryParameters.put(KEY_REDIRECT_URI, callbackURI);
        final Uri.Builder builder = authorizeUri.buildUpon();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private String getCallbackURI(String domain, String packageName) {
        return String.format("%s/android/%s/callback", domain, packageName);
    }

    public Map<String, String> getValuesFromUri(@NonNull Uri uri) {
        return asMap(uri.getQuery() != null ? uri.getQuery() : uri.getFragment());
    }

    private Map<String, String> asMap(@NonNull String valueString) {
        final String[] entries = valueString.length() > 0 ? valueString.split("&") : new String[]{};
        Map<String, String> values = new HashMap<>(entries.length);
        for (String entry : entries) {
            final String[] value = entry.split("=");
            if (value.length == 2) {
                values.put(value[0], value[1]);
            }
        }
        return values;
    }


}