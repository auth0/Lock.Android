/*
 * Constants.java
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

package com.auth0.android.lock;

abstract class Constants {
    static final String LIBRARY_NAME = "Lock.Android";

    static final String OPTIONS_EXTRA = "com.auth0.android.lock.key.Options";

    static final String CONNECTION_SCOPE_KEY = "connection_scope";

    static final String AUTHENTICATION_ACTION = "com.auth0.android.lock.action.Authentication";
    static final String SIGN_UP_ACTION = "com.auth0.android.lock.action.SignUp";
    static final String CANCELED_ACTION = "com.auth0.android.lock.action.Canceled";
    static final String INVALID_CONFIGURATION_ACTION = "com.auth0.android.lock.action.InvalidConfiguration";

    static final String ERROR_EXTRA = "com.auth0.android.lock.extra.Error";
    static final String ID_TOKEN_EXTRA = "com.auth0.android.lock.extra.IdToken";
    static final String ACCESS_TOKEN_EXTRA = "com.auth0.android.lock.extra.AccessToken";
    static final String TOKEN_TYPE_EXTRA = "com.auth0.android.lock.extra.TokenType";
    static final String REFRESH_TOKEN_EXTRA = "com.auth0.android.lock.extra.RefreshToken";
    static final String EXPIRES_IN_EXTRA = "com.auth0.android.lock.extra.ExpiresIn";
    static final String EMAIL_EXTRA = "com.auth0.android.lock.extra.Email";
    static final String USERNAME_EXTRA = "com.auth0.android.lock.extra.Username";
}
