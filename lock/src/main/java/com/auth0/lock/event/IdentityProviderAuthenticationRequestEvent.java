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

    private static final String REDIRECT_URI_FORMAT = "a0%s://%s/authorize";

    private final String serviceName;

    public IdentityProviderAuthenticationRequestEvent(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Uri getAuthenticationUri(Application application, Map<String, Object> parameters) {
        final Uri authorizeUri = Uri.parse(application.getAuthorizeURL());
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
        queryParameters.put("client_id", application.getId());
        queryParameters.put("redirect_uri", String.format(REDIRECT_URI_FORMAT, application.getId().toLowerCase(), authorizeUri.getHost()));
        final Uri.Builder builder = authorizeUri.buildUpon();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
