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

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.R;


public class LoginErrorMessageBuilder implements ErrorMessageBuilder<AuthenticationException> {

    private static final String USER_EXISTS_ERROR = "user_exists";
    private static final String USERNAME_EXISTS_ERROR = "username_exists";
    private static final String USER_IS_BLOCKED_DESCRIPTION = "user is blocked";
    private static final String TOO_MANY_ATTEMPTS_ERROR = "too_many_attempts";
    private static final String WRONG_CLIENT_TYPE_ERROR = "Unauthorized";

    private static final int userExistsResource = R.string.com_auth0_lock_db_signup_user_already_exists_error_message;
    private static final int unauthorizedResource = R.string.com_auth0_lock_db_login_error_unauthorized_message;
    private static final int invalidMFACodeResource = R.string.com_auth0_lock_db_login_error_invalid_mfa_code_message;
    private static final int tooManyAttemptsResource = R.string.com_auth0_lock_db_too_many_attempts_error_message;
    private static final int wrongClientTypeResource = R.string.com_auth0_lock_db_login_error_wrong_client_type_message;

    private int invalidCredentialsResource;
    private int defaultMessage;

    public LoginErrorMessageBuilder(@StringRes int defaultMessage, @StringRes int invalidCredentialsMessage) {
        this.defaultMessage = defaultMessage;
        this.invalidCredentialsResource = invalidCredentialsMessage;
    }

    public LoginErrorMessageBuilder() {
        this(R.string.com_auth0_lock_db_login_error_message, R.string.com_auth0_lock_db_login_error_invalid_credentials_message);
    }

    @Override
    public AuthenticationError buildFrom(AuthenticationException exception) {
        int messageRes;
        String description = null;

        if (exception.isInvalidCredentials()) {
            messageRes = invalidCredentialsResource;
        } else if (exception.isMultifactorCodeInvalid()) {
            messageRes = invalidMFACodeResource;
        } else if (USER_EXISTS_ERROR.equals(exception.getCode()) || USERNAME_EXISTS_ERROR.equals(exception.getCode())) {
            messageRes = userExistsResource;
        } else if (exception.isRuleError()) {
            messageRes = unauthorizedResource;
            if (!USER_IS_BLOCKED_DESCRIPTION.equals(exception.getDescription())) {
                description = exception.getDescription();
            }
        } else if (WRONG_CLIENT_TYPE_ERROR.equals(exception.getDescription())) {
            messageRes = wrongClientTypeResource;
        } else if (TOO_MANY_ATTEMPTS_ERROR.equals(exception.getCode())) {
            messageRes = tooManyAttemptsResource;
        } else {
            messageRes = defaultMessage;
        }
        return new AuthenticationError(messageRes, description);
    }
}
