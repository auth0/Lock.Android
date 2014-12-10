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

/**
 * Created by hernan on 12/10/14.
 */
public class AuthenticationError {

    private int title;
    private int message;
    private Throwable throwable;

    public AuthenticationError(int title, int message) {
        this.title = title;
        this.message = message;
    }

    public AuthenticationError(int title, int message, Throwable throwable) {
        this.title = title;
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage(Context context) {
        return context.getString(this.title);
    }

    public String getTitle(Context context) {
        return context.getString(this.message);
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
