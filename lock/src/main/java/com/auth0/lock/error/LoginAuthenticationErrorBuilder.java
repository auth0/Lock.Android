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
import com.auth0.lock.event.AuthenticationError.ErrorType;

import java.util.Map;

public class LoginAuthenticationErrorBuilder implements AuthenticationErrorBuilder {

    private static final String INVALID_USER_PASSWORD_ERROR = "invalid_user_password";
    private static final String UNAUTHORIZED_ERROR = "unauthorized";
    private static final String MFA_REQUIRED_ERROR = "a0.mfa_required";
    private static final String MFA_INVALID_CODE_ERROR = "a0.mfa_invalid_code";
    private static final String MFA_NOT_ENROLLED_ERROR = "a0.mfa_registration_required";

    private static final String USER_IS_BLOCKED_DESCRIPTION = "user is blocked";

    private final int titleResource;
    private final int defaultMessageResource;
    private final int invalidCredentialsResource;
    private final int unauthorizedResource;
    private final int invalidMFACodeResource = R.string.com_auth0_db_login_invalid_mfa_code_message;

    public LoginAuthenticationErrorBuilder() {
        this(R.string.com_auth0_db_login_error_title, R.string.com_auth0_db_login_error_message,
                R.string.com_auth0_db_login_invalid_credentials_error_message, R.string.com_auth0_db_login_unauthorized_error_message);
    }

    public LoginAuthenticationErrorBuilder(int titleResource, int defaultMessageResource, int invalidCredentialsResource) {
        this(titleResource, defaultMessageResource, invalidCredentialsResource, R.string.com_auth0_db_login_unauthorized_error_message);
    }

    public LoginAuthenticationErrorBuilder(int titleResource, int defaultMessageResource, int invalidCredentialsResource, int unauthorizedResource) {
        this.titleResource = titleResource;
        this.defaultMessageResource = defaultMessageResource;
        this.invalidCredentialsResource = invalidCredentialsResource;
        this.unauthorizedResource = unauthorizedResource;
    }

    @Override
    public AuthenticationError buildFrom(Throwable throwable) {
        int messageResource = defaultMessageResource;
        if (throwable instanceof APIClientException) {
            APIClientException exception = (APIClientException) throwable;
            Map errorResponse = exception.getResponseError();
            final String errorCode = (String) errorResponse.get(ERROR_KEY);
            final String errorDescription = (String) errorResponse.get(ERROR_DESCRIPTION_KEY);
            if (INVALID_USER_PASSWORD_ERROR.equalsIgnoreCase(errorCode)) {
                return new AuthenticationError(titleResource, invalidCredentialsResource, ErrorType.INVALID_CREDENTIALS, throwable);
            } else if (UNAUTHORIZED_ERROR.equalsIgnoreCase(errorCode) && USER_IS_BLOCKED_DESCRIPTION.equalsIgnoreCase(errorDescription)) {
                return new AuthenticationError(titleResource, unauthorizedResource, ErrorType.UNAUTHORIZED, throwable);
            } else if (MFA_INVALID_CODE_ERROR.equalsIgnoreCase(errorCode)) {
                return new AuthenticationError(titleResource, invalidMFACodeResource, ErrorType.MFA_INVALID, throwable);
            } else if (MFA_REQUIRED_ERROR.equalsIgnoreCase(errorCode)) {
                return new AuthenticationError(titleResource, messageResource, ErrorType.MFA_REQUIRED, throwable);
            } else if (MFA_NOT_ENROLLED_ERROR.equalsIgnoreCase(errorCode)) {
                return new AuthenticationError(titleResource, messageResource, ErrorType.MFA_NOT_ENROLLED, throwable);
            } else if (errorDescription != null) {
                return new AuthenticationError(titleResource, errorDescription, ErrorType.UNKNOWN, throwable);
            }
        }
        return new AuthenticationError(titleResource, messageResource, ErrorType.UNKNOWN, throwable);
    }
}
