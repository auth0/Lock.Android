/*
 * SignUpAuthenticationErrorBuilder.java
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

package com.auth0.lock.error;

import com.auth0.api.APIClientException;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationError.ErrorType;

import java.util.Map;

public class SignUpAuthenticationErrorBuilder implements AuthenticationErrorBuilder {

    private static final String USERNAME_EXISTS_ERROR = "username_exists";
    private static final String UNAUTHORIZED_ERROR = "unauthorized";

    private final int titleResource;
    private final int defaultMessageResource;
    private final int userExistsResource;

    public SignUpAuthenticationErrorBuilder() {
        this(R.string.com_auth0_db_signup_error_title, R.string.com_auth0_db_signup_error_message, R.string.com_auth0_db_signup_user_already_exists_error_message);
    }

    public SignUpAuthenticationErrorBuilder(int titleResource, int defaultMessageResource, int userExistsResource) {
        this.titleResource = titleResource;
        this.defaultMessageResource = defaultMessageResource;
        this.userExistsResource = userExistsResource;
    }

    @Override
    public AuthenticationError buildFrom(Throwable throwable) {
        int messageResource = defaultMessageResource;
        if (throwable instanceof APIClientException) {
            APIClientException exception = (APIClientException) throwable;
            Map errorResponse = exception.getResponseError();
            final String errorCode = (String) errorResponse.get(ERROR_KEY);
            final String errorDescription = (String) errorResponse.get(ERROR_DESCRIPTION_KEY);
            if (UNAUTHORIZED_ERROR.equalsIgnoreCase(errorCode) && errorDescription != null) {
                return new AuthenticationError(titleResource, errorDescription, ErrorType.UNAUTHORIZED, throwable);
            }
            if (USERNAME_EXISTS_ERROR.equalsIgnoreCase((String) errorResponse.get(CODE_KEY))) {
                return new AuthenticationError(titleResource, userExistsResource, ErrorType.USER_EXISTS, throwable);
            }
        }
        return new AuthenticationError(titleResource, messageResource, ErrorType.UNKNOWN, throwable);
    }

}
