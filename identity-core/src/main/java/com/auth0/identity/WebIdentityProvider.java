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

import com.auth0.api.ParameterBuilder;
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.identity.util.PKCE;
import com.auth0.identity.web.CallbackParser;
import com.auth0.identity.web.WebViewActivity;
import com.auth0.util.Telemetry;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link com.auth0.identity.IdentityProvider} that handles authentication
 * using an external browser, sending {@link android.content.Intent#ACTION_VIEW} intent, or with {@link com.auth0.identity.web.WebViewActivity}.
 * This behaviour is changed using {@link #setUseWebView(boolean)}, and defaults to send {@link android.content.Intent#ACTION_VIEW} intent.
 */
public class WebIdentityProvider implements IdentityProvider {

    private static final String TAG = WebIdentityProvider.class.getName();
    private static final String REDIRECT_URI_FORMAT = "a0%s://%s/callback";

    private static final String RESPONSE_TYPE_KEY = "response_type";
    private static final String CODE_CHALLENGE_KEY = "code_challenge";
    private static final String CODE_CHALLENGE_METHOD_KEY = "code_challenge_method";
    private static final String SCOPE_KEY = "scope";
    private static final String CONNECTION_KEY = "connection";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String REDIRECT_URI_KEY = "redirect_uri";
    private static final String CODE_KEY = "code";
    private static final String AUTH0_CLIENT_KEY = "auth0Client";
    private static final String ERROR_KEY = "error";
    private static final String ID_TOKEN_KEY = "id_token";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String TOKEN_TYPE_KEY = "token_type";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String LOGIN_HINT_KEY = "login_hint";

    private static final String TYPE_CODE = "code";
    private static final String TYPE_TOKEN = "token";
    private static final String METHOD_SHA_256 = "S256";
    private static final String SCOPE_OPENID = "openid";

    private boolean useWebView;
    private IdentityProviderCallback callback;
    private CallbackParser parser;
    private final String clientId;
    private final String authorizeUrl;
    private Map<String, Object> parameters;
    private String clientInfo;
    private AuthenticationAPIClient apiClient;
    private PKCE pkce;

    public WebIdentityProvider(CallbackParser parser, String clientId, String authorizeUrl) {
        this(parser, clientId, authorizeUrl, null);
    }

    /**
     * Creates a new instance using Auth0 credentials and the possibility to use Proof Key for Code Exchange.
     * @param auth0 creadentials for your Account
     * @param pkce flag if PKCE should be used or not.
     */
    public WebIdentityProvider(Auth0 auth0, boolean pkce) {
        this(new CallbackParser(), auth0.getClientId(), auth0.getAuthorizeUrl(), pkce && PKCE.isAvailable() ? auth0.newAuthenticationAPIClient() : null);
    }

    WebIdentityProvider(CallbackParser parser, String clientId, String authorizeUrl, AuthenticationAPIClient client) {
        this.parser = parser;
        this.clientId = clientId;
        this.authorizeUrl = authorizeUrl;
        this.apiClient = client;
        this.useWebView = false;
        this.parameters = new HashMap<>();
        this.clientInfo = new Telemetry("Lock.Android", BuildConfig.VERSION_NAME).asBase64();
        this.apiClient = client;
    }

    /**
     * If the class authenticates with an external browser or not.
     *
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

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Setter for the AuthenticationAPIClient.
     * If the class is going to authenticate with the PKCE flow, the APIClients needs to be set.
     *
     * @param apiClient the AuthenticationAPIClient to use.
     */
    public void setAPIClient(AuthenticationAPIClient apiClient) {
        this.apiClient = apiClient;
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

    @Override
    public void start(Activity activity, IdentityProviderRequest request, Application application) {

        ParameterBuilder builder = ParameterBuilder.newBuilder(this.parameters);
        String username = request.getUsername();
        if (username != null) {
            int arrobaIndex = username.indexOf("@");
            String loginHint;
            if (arrobaIndex < 0) {
                loginHint = username;
            } else {
                loginHint = username.substring(0, arrobaIndex);
            }
            builder.set(LOGIN_HINT_KEY, loginHint);
        }

        final String serviceName = request.getServiceName();
        final Uri url = buildAuthorizeUri(authorizeUrl, serviceName, builder.asDictionary());
        startAuthorization(activity, url, serviceName);
    }

    /**
     * Starts web-based authentication without any connection name.
     * This will just show hosted Lock.js from Auth0
     * @param activity from where to start the authentication and where the results should be sent
     */
    public void start(Activity activity) {
        final Uri uri = buildAuthorizeUri(authorizeUrl, null, parameters);
        startAuthorization(activity, uri, null);
    }

    @Override
    public void stop() {
        pkce = null;
    }

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        Uri uri = data != null ? data.getData() : null;
        Log.v(TAG, "Authenticating with webflow with data " + uri);
        boolean isValid = requestCode == WEBVIEW_AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK && uri != null;
        if (isValid) {
            final Map<String, String> values = parser.getValuesFromUri(uri);
            if (values.containsKey(ERROR_KEY)) {
                final int message = "access_denied".equalsIgnoreCase(values.get(ERROR_KEY)) ? R.string.com_auth0_social_access_denied_message : R.string.com_auth0_social_error_message;
                callback.onFailure(R.string.com_auth0_social_error_title, message, null);
            } else if (values.size() > 0) {
                Log.d(TAG, "Authenticated using web flow");
                if (shouldUsePKCE()) {
                    pkce.getToken(values.get(CODE_KEY), callback);
                } else {
                    callback.onSuccess(new Token(values.get(ID_TOKEN_KEY), values.get(ACCESS_TOKEN_KEY), values.get(TOKEN_TYPE_KEY), values.get(REFRESH_TOKEN_KEY)));
                }
            }
        }
        return isValid;
    }

    @Override
    public void clearSession() {
        pkce = null;
    }

    private void startAuthorization(Activity activity, Uri authorizeUri, String serviceName) {
        final Intent intent;
        Log.i(TAG, "Start authorization called with uri: " + authorizeUri);
        if (this.useWebView) {
            intent = new Intent(activity, WebViewActivity.class);
            intent.setData(authorizeUri);
            if (serviceName != null) {
                intent.putExtra(WebViewActivity.SERVICE_NAME_EXTRA, serviceName);
            }
            activity.startActivityForResult(intent, WEBVIEW_AUTH_REQUEST_CODE);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, authorizeUri);
            activity.startActivity(intent);
        }
    }

    private Uri buildAuthorizeUri(String url, String serviceName, Map<String, Object> parameters) {
        final Uri authorizeUri = Uri.parse(url);
        String redirectUri = String.format(REDIRECT_URI_FORMAT, clientId.toLowerCase(), authorizeUri.getHost());
        final Map<String, String> queryParameters = new HashMap<>();
        if (clientInfo != null) {
            queryParameters.put(AUTH0_CLIENT_KEY, clientInfo);
        }
        queryParameters.put(SCOPE_KEY, SCOPE_OPENID);
        queryParameters.put(RESPONSE_TYPE_KEY, TYPE_TOKEN);

        if (shouldUsePKCE()) {
            try {
                pkce = new PKCE(apiClient, redirectUri);
                String codeChallenge = pkce.getCodeChallenge();
                queryParameters.put(RESPONSE_TYPE_KEY, TYPE_CODE);
                queryParameters.put(CODE_CHALLENGE_KEY, codeChallenge);
                queryParameters.put(CODE_CHALLENGE_METHOD_KEY, METHOD_SHA_256);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Cannot use PKCE. Defaulting to token response_type", e);
            }
        }

        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    queryParameters.put(entry.getKey(), value.toString());
                }
            }
        }

        if (serviceName != null) {
            queryParameters.put(CONNECTION_KEY, serviceName);
        }
        queryParameters.put(CLIENT_ID_KEY, clientId);
        Log.d(TAG, "Redirect Uri: " + redirectUri);
        queryParameters.put(REDIRECT_URI_KEY, redirectUri);

        final Uri.Builder builder = authorizeUri.buildUpon();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private boolean shouldUsePKCE() {
        return apiClient != null && PKCE.isAvailable();
    }
}
