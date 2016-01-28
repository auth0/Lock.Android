
/*
 *
 *  * WebIdentityProvider.java
 *  *
 *  * Copyright (c) 2016 Auth0 (http://auth0.com)
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.auth0.android.lock.provider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.auth0.Auth0;
import com.auth0.android.lock.R;
import com.auth0.authentication.result.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link IdentityProvider} that handles authentication
 * using an external browser, sending {@link android.content.Intent#ACTION_VIEW} intent, or with {@link WebViewActivity}.
 * This behaviour is changed using {@link #setUseBrowser(boolean)}, and defaults to send {@link android.content.Intent#ACTION_VIEW} intent.
 */
public class WebIdentityProvider implements IdentityProvider {

    private static final String TAG = WebIdentityProvider.class.getName();

    private static final String KEY_ERROR = "error";
    private static final String KEY_ID_TOKEN = "id_token";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_AUTH0_CLIENT = "auth0Client";
    private static final String KEY_RESPONSE_TYPE = "response_type";
    private static final String KEY_STATE = "state";
    private static final String KEY_CONNECTION = "connection";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";

    private static final String ERROR_VALUE_ACCESS_DENIED = "access_denied";
    private static final String RESPONSE_TYPE_TOKEN = "token";
    private static final String SCOPE_TYPE_OPENID = "openid";


    private boolean useBrowser;
    private IdentityProviderCallback callback;
    private CallbackHelper helper;
    private final Auth0 account;
    private Map<String, Object> parameters;
    private String clientInfo;
    private String lastState;

    public WebIdentityProvider(CallbackHelper helper, Auth0 account, IdentityProviderCallback idpCallback) {
        this.helper = helper;
        this.account = account;
        this.callback = idpCallback;
        this.useBrowser = true;
        this.parameters = new HashMap<>();
    }

    /**
     * If the class authenticates with an external browser or not.
     *
     * @param useBrowser if the authentication is handled in a Browser.
     */
    public void setUseBrowser(boolean useBrowser) {
        this.useBrowser = useBrowser;
    }

    public void setCallback(IdentityProviderCallback callback) {
        this.callback = callback;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<String, Object>();
    }

    @Override
    public void start(Activity activity, String serviceName) {
        if (account.getAuthorizeUrl() == null) {
            if (callback != null) {
                callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_invalid_authorize_url, null);
            } else {
                Log.w(TAG, "No callback set for web IdP authenticator");
            }
            return;
        }

        //Generate random state
        lastState = UUID.randomUUID().toString();

        startAuthorization(activity, buildAuthorizeUri(account.getAuthorizeUrl(), serviceName, lastState, parameters), serviceName);
    }

    private void startAuthorization(Activity activity, Uri authorizeUri, String connectionName) {
        final Intent intent;
        if (this.useBrowser) {
            intent = new Intent(Intent.ACTION_VIEW, authorizeUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivity(intent);
        } else {
            intent = new Intent(activity, WebViewActivity.class);
            intent.setData(authorizeUri);
            intent.putExtra(WebViewActivity.CONNECTION_NAME_EXTRA, connectionName);
            //Improvement: let LockActivity set requestCode
            activity.startActivityForResult(intent, WEBVIEW_AUTH_REQUEST_CODE);
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean authorize(Activity activity, AuthorizeResult data) {
        if (!data.isValid(WEBVIEW_AUTH_REQUEST_CODE)) {
            return false;
        }

        final Map<String, String> values = helper.getValuesFromUri(data.getIntent().getData());
        if (values.containsKey(KEY_ERROR)) {
            Log.e(TAG, "Error, access denied.");
            final int message = ERROR_VALUE_ACCESS_DENIED.equalsIgnoreCase(values.get(KEY_ERROR)) ? R.string.com_auth0_social_access_denied_message : R.string.com_auth0_social_error_message;
            callback.onFailure(R.string.com_auth0_social_error_title, message, null);
        } else if (values.containsKey(KEY_STATE) && !values.get(KEY_STATE).equals(lastState)) {
            Log.e(TAG, "Received state doesn't match");
            Log.d(TAG, "Expected: " + lastState + " / Received: " + values.get(KEY_STATE));
            callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_invalid_state, null);
        } else if (values.size() > 0) {
            Log.d(TAG, "Authenticated using web flow");
            callback.onSuccess(new Token(values.get(KEY_ID_TOKEN), values.get(KEY_ACCESS_TOKEN), values.get(KEY_TOKEN_TYPE), values.get(KEY_REFRESH_TOKEN)));
        }
        return true;
    }

    @Override
    public void clearSession() {
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    private Map<String, Object> buildParameters() {
        Map<String, Object> parameters = new HashMap<>(this.parameters);
        if (clientInfo != null) {
            parameters.put(KEY_AUTH0_CLIENT, clientInfo);
        }
        return parameters;
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