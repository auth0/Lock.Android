/*
 * AuthenticationCallback.java
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

package com.auth0.android.lock;

import android.content.Intent;
import android.support.annotation.IntDef;

import com.auth0.android.lock.utils.LockException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.LockCallback.LockEvent.AUTHENTICATION;
import static com.auth0.android.lock.LockCallback.LockEvent.CANCELED;
import static com.auth0.android.lock.LockCallback.LockEvent.RESET_PASSWORD;
import static com.auth0.android.lock.LockCallback.LockEvent.SIGN_UP;

//TODO: Java doc
public interface LockCallback {
    @IntDef({CANCELED, AUTHENTICATION, SIGN_UP, RESET_PASSWORD})
    @Retention(RetentionPolicy.SOURCE)
    @interface LockEvent {
        int CANCELED = 0;
        int AUTHENTICATION = 1;
        int SIGN_UP = 2;
        int RESET_PASSWORD = 3;
    }

    void onEvent(@LockEvent int event, Intent data);

    void onError(LockException error);
}
