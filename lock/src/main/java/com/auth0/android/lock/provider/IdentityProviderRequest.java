/*
 * IdentityProviderRequest.java
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

import android.net.Uri;

import com.auth0.android.lock.utils.Application;

import java.util.Map;

/**
 * Interface for a authentication request to a Identity Provider, e.g. Facebook, Instagram, etc.
 */
public interface IdentityProviderRequest {

    /**
     * Returns the URL used to authenticate the user in the Identity Provider.
     *
     * @param application Auth0 application information
     * @param parameters
     * @return a {@link android.net.Uri}
     */
    Uri getAuthenticationUri(Application application, Map<String, Object> parameters);

    /**
     * Name of the Identity Provider
     *
     * @return a name
     */
    String getServiceName();

}