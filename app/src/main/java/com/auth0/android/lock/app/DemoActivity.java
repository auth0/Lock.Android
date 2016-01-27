/*
 * DemoActivity.java
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

package com.auth0.android.lock.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.auth0.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.utils.LockException;
import com.auth0.authentication.result.Authentication;

public class DemoActivity extends AppCompatActivity implements AuthenticationCallback {
    private static final String AUTH0_CLIENT_ID = "Owu62gnGsRYhk1v9SfB3c6IUbIJcRIze";
    private static final String AUTH0_DOMAIN = "lbalmaceda.auth0.com";
    private static final int AUTH_REQUEST = 333;
    private Lock lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create account
        Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);

        // create/configure lock
        lock = Lock.newBuilder()
                .withAccount(auth0)
                .withCallback(this)
                .build();
        lock.onCreate(DemoActivity.this);

        // launch, the results will be received in the callback
        startActivity(lock.newIntent(this));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //should we ask for null lock?
        lock.onDestroy(DemoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //should we ask for null lock?
        if (requestCode == AUTH_REQUEST) {
            lock.onActivityResult(DemoActivity.this, resultCode, data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthentication(Authentication authentication) {

    }

    @Override
    public void onCanceled() {

    }

    @Override
    public void onError(LockException error) {

    }
}
