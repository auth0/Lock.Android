/*
 * SocialAuthenticationEvent.java
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

/**
 * Created by hernan on 12/17/14.
 */
public class SocialAuthenticationRequestEvent {

    private static final String REDIRECT_URI_FORMAT = "a0%s://%s.auth0.com/authorize";

    private final String serviceName;

    public SocialAuthenticationRequestEvent(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Uri getAuthenticationUri(Application application) {
        return Uri.parse(application.getAuthorizeURL()).buildUpon()
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("connection", serviceName)
                .appendQueryParameter("client_id", application.getId())
                .appendQueryParameter("scope", "openid")
                .appendQueryParameter("redirect_uri", String.format(REDIRECT_URI_FORMAT, application.getId().toLowerCase(), application.getTenant()))
                .build();
    }
}
