/*
 * SignUpAuthenticationErrorBuilder.java
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

package com.auth0.android.lock.errors;

import android.support.annotation.StringRes;

import com.auth0.APIException;
import com.auth0.android.lock.R;
import com.auth0.android.lock.errors.AuthenticationError.ErrorType;

import java.util.Map;

public class SignUpAuthenticationErrorBuilder implements AuthenticationErrorBuilder {

    private static final String USER_EXISTS_ERROR = "user_exists";
    private static final String USERNAME_EXISTS_ERROR = "username_exists";
    private static final String UNAUTHORIZED_ERROR = "unauthorized";

    @StringRes
    private final int defaultMessage;

    private static final int userExistsResource = R.string.com_auth0_lock_db_signup_user_already_exists_error_message;

    public SignUpAuthenticationErrorBuilder(@StringRes int defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public SignUpAuthenticationErrorBuilder() {
        this(R.string.com_auth0_lock_db_sign_up_error_message);
    }

    @Override
    public AuthenticationError buildFrom(Throwable throwable) {
        if (throwable instanceof APIException) {
            APIException exception = (APIException) throwable;
            Map errorResponse = exception.getResponseError();
            final String errorCode = errorResponse.containsKey(ERROR_KEY) ? (String) errorResponse.get(ERROR_KEY) : (String) errorResponse.get(CODE_KEY);
            final String errorDescription = (String) errorResponse.get(ERROR_DESCRIPTION_KEY);
            if (UNAUTHORIZED_ERROR.equalsIgnoreCase(errorCode) && errorDescription != null) {
                return new AuthenticationError(errorDescription, ErrorType.UNAUTHORIZED, throwable);
            } else if (USER_EXISTS_ERROR.equalsIgnoreCase(errorCode) || USERNAME_EXISTS_ERROR.equalsIgnoreCase(errorCode)) {
                return new AuthenticationError(userExistsResource, ErrorType.INVALID_CREDENTIALS, throwable);
            } else if (errorDescription != null) {
                return new AuthenticationError(errorDescription, ErrorType.UNKNOWN, throwable);
            }
        }
        return new AuthenticationError(defaultMessage, throwable);
    }
}
