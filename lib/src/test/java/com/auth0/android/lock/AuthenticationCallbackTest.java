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

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.LockCallback.LockEvent;
import com.auth0.android.lock.utils.MockLockCallback;
import com.auth0.android.result.Credentials;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasAuthentication;
import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasError;
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
    public void setUp() {
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
        assertThat(callback.getCredentials().getExpiresAt(), equalTo(credentials.getExpiresAt()));
        assertThat(callback.getCredentials().getScope(), equalTo(credentials.getScope()));
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldReturnAuthenticationError() {
        Intent data = new Intent();
        AuthenticationException error = new AuthenticationException("err_code", "err description");
        data.putExtra(Constants.EXCEPTION_EXTRA, error);
        callback.onEvent(LockEvent.AUTHENTICATION, data);

        assertThat(callback, hasError());
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
        i.putExtra(Constants.EXPIRES_AT_EXTRA, new Date());
        i.putExtra(Constants.SCOPE_EXTRA, "openid profile");
        return i;
    }

    public Credentials credentialsFromData(Intent data) {
        String idToken = data.getStringExtra(Constants.ID_TOKEN_EXTRA);
        String accessToken = data.getStringExtra(Constants.ACCESS_TOKEN_EXTRA);
        String tokenType = data.getStringExtra(Constants.TOKEN_TYPE_EXTRA);
        String refreshToken = data.getStringExtra(Constants.REFRESH_TOKEN_EXTRA);
        Date expiresAt = (Date) data.getSerializableExtra(Constants.EXPIRES_AT_EXTRA);
        String scope = data.getStringExtra(Constants.SCOPE_EXTRA);

        return new Credentials(idToken, accessToken, tokenType, refreshToken, expiresAt, scope);
    }

}