/*
 * ResponseCallbackTask.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.api.internal;

import android.os.Handler;

import com.auth0.api.callback.BaseCallback;

class CallbackHandler<T> {

    private final Handler handler;
    private BaseCallback<T> callback;

    public CallbackHandler(Handler handler) {
        this.handler = handler;
    }

    public CallbackHandler(Handler handler, BaseCallback<T> callback) {
        this.handler = handler;
        this.callback = callback;
    }

    protected void setCallback(BaseCallback<T> callback) {
        this.callback = callback;
    }

    protected void postOnSuccess(final T payload) {
        handler.post(new CallbackTask<T>(callback) {
            @Override
            public void run() {
                this.callback.onSuccess(payload);
            }
        });
    }

    protected void postOnFailure(final Throwable error) {
        handler.post(new CallbackTask<T>(callback) {
            @Override
            public void run() {
                this.callback.onFailure(error);
            }
        });
    }

    private static abstract class CallbackTask<T> implements Runnable {
        protected final BaseCallback<T> callback;

        protected CallbackTask(BaseCallback<T> callback) {
            this.callback = callback;
        }
    }
}
