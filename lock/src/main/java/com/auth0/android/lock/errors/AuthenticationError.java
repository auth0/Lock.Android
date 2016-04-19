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
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

public class AuthenticationError {

    private Throwable throwable;

    @StringRes
    private int message;

    private String customMessage;

    public AuthenticationError(@StringRes int message) {
        this(message, null);
    }

    public AuthenticationError(@StringRes int message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public AuthenticationError(String message, Throwable throwable) {
        this.customMessage = message;
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
}