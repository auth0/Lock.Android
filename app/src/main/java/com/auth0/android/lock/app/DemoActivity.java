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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.auth0.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.PasswordlessLock;
import com.auth0.android.lock.utils.LockException;
import com.auth0.authentication.ParameterBuilder;
import com.auth0.authentication.result.Authentication;

import java.util.Map;

public class DemoActivity extends AppCompatActivity implements AuthenticationCallback, View.OnClickListener {
    private static final String AUTH0_CLIENT_ID = "1RUsvK047TGUp8trp9R2T6ubQy7c3Mrr";
    private static final String AUTH0_DOMAIN = "overmind.auth0.com";
    private static final String SCOPE_OPENID_OFFLINE_ACCESS = "openid offline_access";
    private static final int AUTH_REQUEST = 333;

    private Lock lock;
    private PasswordlessLock passwordlessLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_activity);
        Button btnWebView = (Button) findViewById(R.id.btn_normal_webview);
        Button btnBrowser = (Button) findViewById(R.id.btn_normal_browser);
        Button btnPasswordlessEmailCode = (Button) findViewById(R.id.btn_passwordless_code);
        Button btnPasswordlessEmailLink = (Button) findViewById(R.id.btn_passwordless_link);

        btnWebView.setOnClickListener(this);
        btnBrowser.setOnClickListener(this);
        btnPasswordlessEmailCode.setOnClickListener(this);
        btnPasswordlessEmailLink.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lock != null) {
            lock.onDestroy(this);
        }
        if (passwordlessLock != null) {
            passwordlessLock.onDestroy(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //should we ask for null lock?
        if (lock != null && requestCode == AUTH_REQUEST) {
            lock.onActivityResult(this, resultCode, data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthentication(Authentication authentication) {
        showResult("OK > " + authentication.getProfile().getName() + " > " + authentication.getCredentials().getIdToken());
    }

    @Override
    public void onCanceled() {
        showResult("User pressed back.");
    }

    @Override
    public void onError(LockException error) {
        showResult(error.getMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_normal_webview:
                normalLogin(false);
                break;
            case R.id.btn_normal_browser:
                normalLogin(true);
                break;
            case R.id.btn_passwordless_code:
                passwordlessLogin(true);
                break;
            case R.id.btn_passwordless_link:
                passwordlessLogin(false);
                break;
        }
    }

    /**
     * Launches the login flow showing only the Passwordless widget.
     *
     * @param useCode on Passwordless Authentication.
     */
    private void passwordlessLogin(boolean useCode) {
        Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);

        if (useCode) {
            passwordlessLock = PasswordlessLock.newBuilder(auth0, this)
                    .useCode()
                    .build();
        } else {
            passwordlessLock = PasswordlessLock.newBuilder(auth0, this)
                    .useLink()
                    .build();
        }

        passwordlessLock.onCreate(this);

        startActivity(passwordlessLock.newIntent(this));
    }

    /**
     * Launches the login flow showing only the Social widget.
     *
     * @param useBrowser whether to use the webview (default) or the browser.
     */
    private void normalLogin(boolean useBrowser) {
        // create account
        Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);

        Map<String, Object> params = ParameterBuilder.newAuthenticationBuilder()
                .setDevice(Build.MODEL)
                .setScope(SCOPE_OPENID_OFFLINE_ACCESS)
                .asDictionary();
        // create/configure lock
        lock = Lock.newBuilder(auth0, this)
                .useBrowser(useBrowser)
                .withAuthenticationParameters(params)
                .loginAfterSignUp(false)
                .build();
        lock.onCreate(this);

        // launch, the results will be received in the callback
        if (useBrowser) {
            startActivity(lock.newIntent(this));
        } else {
            startActivityForResult(lock.newIntent(this), AUTH_REQUEST);
        }
    }

    /**
     * Shows a Snackbar on the bottom of the layout
     *
     * @param message the text to show.
     */
    private void showResult(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
