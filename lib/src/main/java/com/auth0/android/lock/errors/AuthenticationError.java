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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class AuthenticationError {

    @StringRes
    private final int message;
    private final String customMessage;

    public AuthenticationError(@StringRes int message) {
        this(message, null);
    }

    AuthenticationError(@StringRes int message, @Nullable String description) {
        this.message = message;
        this.customMessage = description;
    }

    /**
     * Getter for the error message.
     *
     * @param context a valid context.
     * @return the user friendly message
     */
    @NonNull
    public String getMessage(@NonNull Context context) {
        if (customMessage != null) {
            return customMessage;
        }
        return context.getResources().getString(message);
    }

    int getMessageRes() {
        return message;
    }

    String getCustomMessage() {
        return customMessage;
    }
}