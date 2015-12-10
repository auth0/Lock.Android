/*
 * IdentityProviderAuthenticationRequestEvent.java
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

package com.auth0.lock.event;

import android.net.Uri;

import com.auth0.core.Application;
import com.auth0.identity.IdentityProviderRequest;

import java.util.HashMap;
import java.util.Map;

public class IdentityProviderAuthenticationRequestEvent implements IdentityProviderRequest {

    private static final String SCOPE_KEY = "scope";
    private static final String RESPONSE_TYPE_KEY = "response_type";
    private static final String CONNECTION_KEY = "connection";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String REDIRECT_URI_KEY = "redirect_uri";
    private static final String LOGIN_HINT_KEY = "login_hint";

    private static final String SCOPE_OPENID = "openid";
    private static final String RESPONSE_TYPE_TOKEN = "token";
    private static final String REDIRECT_URI_FORMAT = "a0%s://%s/authorize";

    private final String serviceName;
    private final String username;

    public IdentityProviderAuthenticationRequestEvent(String serviceName) {
        this(serviceName, null);
    }

    public IdentityProviderAuthenticationRequestEvent(String serviceName, String username) {
        this.serviceName = serviceName;
        this.username = username;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Uri getAuthenticationUri(Application application, Map<String, Object> parameters) {
        final Uri authorizeUri = Uri.parse(application.getAuthorizeURL());
        final Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(SCOPE_KEY, SCOPE_OPENID);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    queryParameters.put(entry.getKey(), value.toString());
                }
            }
        }
        queryParameters.put(RESPONSE_TYPE_KEY, RESPONSE_TYPE_TOKEN);
        queryParameters.put(CONNECTION_KEY, serviceName);
        queryParameters.put(CLIENT_ID_KEY, application.getId());
        queryParameters.put(REDIRECT_URI_KEY, String.format(REDIRECT_URI_FORMAT, application.getId().toLowerCase(), authorizeUri.getHost()));
        if (username != null) {
            int arrobaIndex = username.indexOf("@");
            String loginHint;
            if (arrobaIndex < 0) {
                loginHint = username;
            } else {
                loginHint = username.substring(0, arrobaIndex);
            }
            queryParameters.put(LOGIN_HINT_KEY, loginHint);
        }
        final Uri.Builder builder = authorizeUri.buildUpon();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
