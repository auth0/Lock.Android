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

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.auth0.android.authentication.AuthenticationException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.LockCallback.LockEvent.AUTHENTICATION;
import static com.auth0.android.lock.LockCallback.LockEvent.CANCELED;
import static com.auth0.android.lock.LockCallback.LockEvent.RESET_PASSWORD;
import static com.auth0.android.lock.LockCallback.LockEvent.SIGN_UP;

/**
 * Callback used by Lock to notify the user of execution results.
 */
public interface LockCallback {
    /**
     * Type of Events that Lock can notified of.
     */
    @SuppressWarnings("UnnecessaryInterfaceModifier")
    @IntDef({CANCELED, AUTHENTICATION, SIGN_UP, RESET_PASSWORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LockEvent {
        int CANCELED = 0;
        int AUTHENTICATION = 1;
        int SIGN_UP = 2;
        int RESET_PASSWORD = 3;
    }

    /**
     * Called when a known event is fired by Lock.
     *
     * @param event to notify of
     * @param data  related to the event.
     */
    void onEvent(@LockEvent int event, @NonNull Intent data);

    /**
     * Called when an error is raised by Lock.
     *
     * @param error describing what happened.
     */
    void onError(@NonNull AuthenticationException error);
}
