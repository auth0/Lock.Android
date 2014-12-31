/*
 * IdentityProvider.java
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

package com.auth0.lock.identity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.auth0.core.Application;
import com.auth0.lock.Lock;
import com.auth0.lock.event.SocialAuthenticationRequestEvent;
import com.auth0.lock.provider.BusProvider;

/**
 * Created by hernan on 12/22/14.
 */
public interface IdentityProvider {

    static final int WEBVIEW_AUTH_REQUEST_CODE = 500;
    static final int GOOGLE_PLUS_REQUEST_CODE = 501;
    static final int GOOGLE_PLUS_TOKEN_REQUEST_CODE = 502;

    void start(Activity activity, SocialAuthenticationRequestEvent event, Application application);

    void stop();

    boolean authorize(Activity activity, int requestCode, int resultCode, Intent data);

    void clearSession();
}
