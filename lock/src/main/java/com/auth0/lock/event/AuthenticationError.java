/*
 * Error.java
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

package com.auth0.lock.event;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.lock.event.AuthenticationError.ErrorType.INVALID_CREDENTIALS;
import static com.auth0.lock.event.AuthenticationError.ErrorType.MFA_INVALID;
import static com.auth0.lock.event.AuthenticationError.ErrorType.MFA_NOT_ENROLLED;
import static com.auth0.lock.event.AuthenticationError.ErrorType.MFA_REQUIRED;
import static com.auth0.lock.event.AuthenticationError.ErrorType.UNAUTHORIZED;
import static com.auth0.lock.event.AuthenticationError.ErrorType.UNKNOWN;
import static com.auth0.lock.event.AuthenticationError.ErrorType.USER_EXISTS;

public class AuthenticationError extends AlertDialogEvent {

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

    private Throwable throwable;

    private String customMesssage;

    public AuthenticationError(int title, int message) {
        this(title, message, UNKNOWN, null);
    }

    public AuthenticationError(int title, String message) {
        this(title, message, UNKNOWN, null);
    }

    public AuthenticationError(int title, int message, @ErrorType int type, Throwable throwable) {
        super(title, message);
        this.errorType = type;
        this.throwable = throwable;
    }

    public AuthenticationError(int title, String message, @ErrorType int type, Throwable throwable) {
        super(title, -1);
        this.customMesssage = message;
        this.errorType = type;
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }


    @Override
    public String getMessage(Context context) {
        if (customMesssage != null) {
            return customMesssage;
        }
        return super.getMessage(context);
    }

    @ErrorType
    public int getErrorType() {
        return errorType;
    }
}
