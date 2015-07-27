/*
 * WebIdentityProvider.java
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

package com.auth0.identity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.core.Token;
import com.auth0.identity.web.CallbackParser;
import com.auth0.identity.web.WebViewActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link com.auth0.identity.IdentityProvider} that handles authentication
 * using an external browser, sending {@link android.content.Intent#ACTION_VIEW} intent, or with {@link com.auth0.identity.web.WebViewActivity}.
 * This behaviour is changed using {@link #setUseWebView(boolean)}, and defaults to send {@link android.content.Intent#ACTION_VIEW} intent.
 */
public class WebIdentityProvider implements IdentityProvider {

    private static final String REDIRECT_URI_FORMAT = "a0%s://%s/authorize";
    private static final String TAG = WebIdentityProvider.class.getName();

    private boolean useWebView;
    private IdentityProviderCallback callback;
    private CallbackParser parser;
    private final String clientId;
    private final String authorizeUrl;
    private Map<String, Object> parameters;
    private String clientInfo;

    public WebIdentityProvider(CallbackParser parser, String clientId, String authorizeUrl) {
        this.parser = parser;
        this.clientId = clientId;
        this.authorizeUrl = authorizeUrl;
        this.useWebView = false;
        this.parameters = new HashMap<>();
    }

    /**
     * If the class authenticates with an external browser or not.
     * @param useWebView if the authentication is handled in a WebView.
     */
    public void setUseWebView(boolean useWebView) {
        this.useWebView = useWebView;
    }

    public void setCallback(IdentityProviderCallback callback) {
        this.callback = callback;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<String, Object>();
    }

    @Override
    public void start(Activity activity, String serviceName) {
        if (authorizeUrl == null) {
            if (callback != null) {
                callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_invalid_authorize_url, null);
            } else {
                Log.w(TAG, "No callback set for web IdP authenticator");
            }
            return;
        }
        startAuthorization(activity, buildAuthorizeUri(authorizeUrl, serviceName, parameters), serviceName);
    }

    public void start(Activity activity, IdentityProviderRequest request, Application application) {
        final Uri url = request.getAuthenticationUri(application, buildParameters());
        final String serviceName = request.getServiceName();
        startAuthorization(activity, url, serviceName);
    }

    private void startAuthorization(Activity activity, Uri authorizeUri, String serviceName) {
        final Intent intent;
        if (this.useWebView) {
            intent = new Intent(activity, WebViewActivity.class);
            intent.setData(authorizeUri);
            intent.putExtra(WebViewActivity.SERVICE_NAME_EXTRA, serviceName);
            activity.startActivityForResult(intent, WEBVIEW_AUTH_REQUEST_CODE);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, authorizeUri);
            activity.startActivity(intent);
        }
    }

    @Override
    public void stop() {}

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        Uri uri = data != null ? data.getData() : null;
        Log.v(TAG, "Authenticating with webflow with data " + uri);
        boolean isValid = requestCode == WEBVIEW_AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK && uri != null;
        if (isValid) {
            final Map<String, String> values = parser.getValuesFromUri(uri);
            if (values.containsKey("error")) {
                final int message = "access_denied".equalsIgnoreCase(values.get("error")) ? R.string.com_auth0_social_access_denied_message : R.string.com_auth0_social_error_message;
                callback.onFailure(R.string.com_auth0_social_error_title, message, null);
            } else if(values.size() > 0) {
                Log.d(TAG, "Authenticated using web flow");
                callback.onSuccess(new Token(values.get("id_token"), values.get("access_token"), values.get("token_type"), values.get("refresh_token")));
            }
        }
        return isValid;
    }

    @Override
    public void clearSession() {}

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    private Map<String, Object> buildParameters() {
        Map<String, Object> parameters = new HashMap<>(this.parameters);
        if (clientInfo != null) {
            parameters.put("auth0Client", clientInfo);
        }
        return parameters;
    }

    private Uri buildAuthorizeUri(String url, String serviceName, Map<String, Object> parameters) {
        final Uri authorizeUri = Uri.parse(url);
        final Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("scope", "openid");
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    queryParameters.put(entry.getKey(), value.toString());
                }
            }
        }
        queryParameters.put("response_type", "token");
        queryParameters.put("connection", serviceName);
        queryParameters.put("client_id", clientId);
        queryParameters.put("redirect_uri", String.format(REDIRECT_URI_FORMAT, clientId.toLowerCase(), authorizeUri.getHost()));
        final Uri.Builder builder = authorizeUri.buildUpon();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
