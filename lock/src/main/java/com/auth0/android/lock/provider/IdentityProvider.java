/*
 * IdentityProvider.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.provider;

import android.app.Activity;
import android.content.Intent;


/**
 * Interface for the object that can handle authentication against an Identity Provider.
 */
public interface IdentityProvider {

    static final int WEBVIEW_AUTH_REQUEST_CODE = 500;
    static final int GOOGLE_PLUS_REQUEST_CODE = 501;
    static final int GOOGLE_PLUS_TOKEN_REQUEST_CODE = 502;

    /**
     * Sets the callback that will report the result of the authentication
     *
     * @param callback a callback
     */
    void setCallback(IdentityProviderCallback callback);

    /**
     * Starts the authentication process for an identity provider.
     *
     * @param activity    activity that starts the process (and will receive its response)
     * @param serviceName of the IdP to authenticate with
     */
    void start(Activity activity, String serviceName);

    /**
     * Stops the authentication process (even if it's in progress).
     */
    void stop();

    /**
     * Called with the result of the authorization.
     *
     * @param activity activity that will receive the result.
     * @param result   authorization result data.
     * @return if the result is valid or not.
     */
    boolean authorize(Activity activity, AuthorizeResult result);

    /**
     * Removes any session information stored in the object.
     */
    void clearSession();

}