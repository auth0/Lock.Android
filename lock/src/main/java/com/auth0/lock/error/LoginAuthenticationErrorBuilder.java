/*
 * LoginAuthenticationErrorBuilder.java
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

package com.auth0.lock.error;

import com.auth0.api.APIClientException;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;

import java.util.Map;

/**
 * Created by hernan on 12/11/14.
 */
public class LoginAuthenticationErrorBuilder implements  AuthenticationErrorBuilder {

    private static final String INVALID_USER_PASSWORD_ERROR = "invalid_user_password";

    @Override
    public AuthenticationError buildFrom(Throwable throwable) {
        int messageResource = R.string.db_login_error_message;
        if (throwable instanceof APIClientException) {
            APIClientException exception = (APIClientException) throwable;
            Map errorResponse = exception.getResponseError();
            if (INVALID_USER_PASSWORD_ERROR.equalsIgnoreCase((String) errorResponse.get(ERROR_KEY))) {
                messageResource = R.string.db_login_invalid_credentials_error_message;
            }
        }
        return new AuthenticationError(R.string.db_login_error_title, messageResource, throwable);
    }
}
