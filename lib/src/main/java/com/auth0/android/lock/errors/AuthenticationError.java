/*
 * AuthenticationError.java
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

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.INVALID_CREDENTIALS;
import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.MFA_INVALID;
import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.MFA_NOT_ENROLLED;
import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.MFA_REQUIRED;
import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.UNAUTHORIZED;
import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.UNKNOWN;
import static com.auth0.android.lock.errors.AuthenticationError.ErrorType.USER_EXISTS;

public class AuthenticationError {

    @IntDef({UNKNOWN, UNAUTHORIZED, USER_EXISTS, INVALID_CREDENTIALS, MFA_INVALID, MFA_REQUIRED, MFA_NOT_ENROLLED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorType {
        int UNKNOWN = 0;
        int UNAUTHORIZED = 1;
        int USER_EXISTS = 2;
        int INVALID_CREDENTIALS = 3;
        int MFA_INVALID = 4;
        int MFA_REQUIRED = 5;
        int MFA_NOT_ENROLLED = 6;
    }

    @ErrorType
    private int errorType;
    @StringRes
    private int message;
    private String customMessage;
    private Throwable throwable;


    public AuthenticationError(@StringRes int message, Throwable throwable) {
        this(message, null, UNKNOWN, throwable);
    }

    public AuthenticationError(@StringRes int message, @ErrorType int type, Throwable throwable) {
        this(message, null, type, throwable);
    }

    public AuthenticationError(@NonNull String message, @ErrorType int type, Throwable throwable) {
        this(0, message, type, throwable);
    }

    AuthenticationError(@StringRes int message, @Nullable String description, @ErrorType int type, Throwable throwable) {
        this.message = message;
        this.customMessage = description;
        this.errorType = type;
        this.throwable = throwable;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * Getter for the message associated with this throwable.
     *
     * @param context a valid context.
     * @return the user friendly message
     */
    public String getMessage(Context context) {
        if (customMessage != null) {
            return customMessage;
        }
        return context.getResources().getString(message);
    }

    @ErrorType
    public int getErrorType() {
        return errorType;
    }
}