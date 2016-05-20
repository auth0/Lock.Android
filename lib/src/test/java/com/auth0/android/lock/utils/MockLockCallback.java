/*
 * MockLockCallback.java
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

package com.auth0.android.lock.utils;

import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.authentication.result.Authentication;

import java.util.concurrent.Callable;

public class MockLockCallback extends AuthenticationCallback {

    private Authentication authentication;
    private boolean canceled;
    private LockException error;

    public Callable<Authentication> authentication() {
        return new Callable<Authentication>() {
            @Override
            @LockEvent
            public Authentication call() throws Exception {
                return authentication;
            }
        };
    }

    public Callable<Boolean> canceled() {
        return new Callable<Boolean>() {
            @Override
            @LockEvent
            public Boolean call() throws Exception {
                return canceled;
            }
        };
    }

    public Callable<LockException> error() {
        return new Callable<LockException>() {
            @Override
            public LockException call() throws Exception {
                return error;
            }
        };
    }

    @Override
    public void onAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public void onCanceled() {
        this.canceled = true;
    }

    @Override
    public void onError(LockException error) {
        this.error = error;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }
}
