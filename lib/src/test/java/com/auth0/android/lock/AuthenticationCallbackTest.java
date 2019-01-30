/*
 * AuthenticationCallbackTest.java
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

import com.auth0.android.lock.LockCallback.LockEvent;
import com.auth0.android.lock.utils.MockLockCallback;
import com.auth0.android.result.Credentials;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasAuthentication;
import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasNoError;
import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.isCanceled;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AuthenticationCallbackTest {

    private MockLockCallback callback;

    @Before
    public void setUp() throws Exception {
        callback = new MockLockCallback();
    }

    @Test
    public void shouldCallOnAuthentication() {
        Intent data = getAuthenticationData();
        callback.onEvent(LockEvent.AUTHENTICATION, data);

        assertThat(callback, hasAuthentication());
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldReturnAuthentication() {
        Intent data = getAuthenticationData();
        callback.onEvent(LockEvent.AUTHENTICATION, data);
        Credentials credentials = credentialsFromData(data);

        assertThat(callback.getCredentials().getAccessToken(), equalTo(credentials.getAccessToken()));
        assertThat(callback.getCredentials().getIdToken(), equalTo(credentials.getIdToken()));
        assertThat(callback.getCredentials().getRefreshToken(), equalTo(credentials.getRefreshToken()));
        assertThat(callback.getCredentials().getType(), equalTo(credentials.getType()));
        assertThat(callback.getCredentials().getExpiresIn(), equalTo(credentials.getExpiresIn()));
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldCallOnCanceled() {
        Intent data = new Intent();
        callback.onEvent(LockEvent.CANCELED, data);

        assertThat(callback, isCanceled());
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldNotAuthenticateOrCancelWhenSignUp() {
        Intent data = new Intent();
        callback.onEvent(LockEvent.SIGN_UP, data);

        assertThat(callback, not(hasAuthentication()));
        assertThat(callback, not(isCanceled()));
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldNotAuthenticateOrCancelWhenResetPassword() {
        Intent data = new Intent();
        callback.onEvent(LockEvent.RESET_PASSWORD, data);

        assertThat(callback, not(hasAuthentication()));
        assertThat(callback, not(isCanceled()));
        assertThat(callback, hasNoError());
    }

    public Intent getAuthenticationData() {
        Intent i = new Intent(Constants.AUTHENTICATION_ACTION);
        i.putExtra(Constants.ID_TOKEN_EXTRA, "idToken");
        i.putExtra(Constants.ACCESS_TOKEN_EXTRA, "accessToken");
        i.putExtra(Constants.TOKEN_TYPE_EXTRA, "tokenType");
        i.putExtra(Constants.REFRESH_TOKEN_EXTRA, "refreshToken");
        i.putExtra(Constants.EXPIRES_IN_EXTRA, 86000);
        return i;
    }

    public Credentials credentialsFromData(Intent data) {
        String idToken = data.getStringExtra(Constants.ID_TOKEN_EXTRA);
        String accessToken = data.getStringExtra(Constants.ACCESS_TOKEN_EXTRA);
        String tokenType = data.getStringExtra(Constants.TOKEN_TYPE_EXTRA);
        String refreshToken = data.getStringExtra(Constants.REFRESH_TOKEN_EXTRA);
        long expiresIn = data.getLongExtra(Constants.EXPIRES_IN_EXTRA, 0);

        return new Credentials(idToken, accessToken, tokenType, refreshToken, expiresIn);
    }

}