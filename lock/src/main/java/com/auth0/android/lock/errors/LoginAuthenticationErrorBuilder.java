/*
 * LoginAuthenticationError.java
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

import java.util.Map;

public class LoginAuthenticationErrorBuilder implements AuthenticationErrorBuilder {

    private static final String INVALID_USER_PASSWORD_ERROR = "invalid_user_password";
    private static final String UNAUTHORIZED_ERROR = "unauthorized";

    private static final String USER_IS_BLOCKED_DESCRIPTION = "user is blocked";

    @StringRes
    private int defaultMessage;

    private static final int unauthorizedResource = R.string.com_auth0_lock_db_login_error_unauthorized_message;
    private int invalidCredentialsResource = R.string.com_auth0_lock_db_login_error_invalid_credentials_message;

    public LoginAuthenticationErrorBuilder(@StringRes int defaultMessage, @StringRes int invalidCredentialsMessage) {
        this.defaultMessage = defaultMessage;
        this.invalidCredentialsResource = invalidCredentialsMessage;
    }

    public LoginAuthenticationErrorBuilder() {
        this(R.string.com_auth0_lock_db_login_error_message, R.string.com_auth0_lock_db_login_error_invalid_credentials_message);
    }

    @Override
    public AuthenticationError buildFrom(Throwable throwable) {
        int messageResource = defaultMessage;

        if (throwable instanceof APIException) {
            APIException exception = (APIException) throwable;
            Map errorResponse = exception.getResponseError();
            final String errorCode = (String) errorResponse.get(ERROR_KEY);
            final String errorDescription = (String) errorResponse.get(ERROR_DESCRIPTION_KEY);
            if (INVALID_USER_PASSWORD_ERROR.equalsIgnoreCase(errorCode)) {
                messageResource = invalidCredentialsResource;
            } else if (UNAUTHORIZED_ERROR.equalsIgnoreCase(errorCode) && USER_IS_BLOCKED_DESCRIPTION.equalsIgnoreCase(errorDescription)) {
                messageResource = unauthorizedResource;
            } else if (errorDescription != null) {
                return new AuthenticationError(errorDescription, throwable);
            }
        }
        return new AuthenticationError(messageResource, throwable);
    }
}
