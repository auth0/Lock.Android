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
import com.auth0.authentication.result.Authentication;
import com.auth0.authentication.result.Credentials;
import com.auth0.authentication.result.UserProfile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasAuthentication;
import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasError;
import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.hasNoError;
import static com.auth0.android.lock.utils.AuthenticationCallbackMatcher.isCanceled;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class AuthenticationCallbackTest {

    private MockLockCallback callback;

    @Before
    public void setUp() throws Exception {
        callback = new MockLockCallback();
    }

    @Test
    public void shouldCallOnAuthentication() {
        Intent data = getValidAuthenticationData();
        callback.onEvent(LockEvent.AUTHENTICATION, data);

        assertThat(callback, hasAuthentication());
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldReturnValidAuthentication() {
        Intent data = getValidAuthenticationData();
        callback.onEvent(LockEvent.AUTHENTICATION, data);
        Authentication authentication = authenticationFromData(data);

        assertThat(callback.getAuthentication().getProfile(), equalTo(authentication.getProfile()));
        assertThat(callback.getAuthentication().getCredentials().getAccessToken(), equalTo(authentication.getCredentials().getAccessToken()));
        assertThat(callback.getAuthentication().getCredentials().getIdToken(), equalTo(authentication.getCredentials().getIdToken()));
        assertThat(callback.getAuthentication().getCredentials().getRefreshToken(), equalTo(authentication.getCredentials().getRefreshToken()));
        assertThat(callback.getAuthentication().getCredentials().getType(), equalTo(authentication.getCredentials().getType()));
        assertThat(callback, hasNoError());
    }

    @Test
    public void shouldCallOnErrorIfDataIsInvalid() {
        Intent data = getInvalidAuthenticationData();
        callback.onEvent(LockEvent.AUTHENTICATION, data);

        assertThat(callback, hasError());
        assertThat(callback, not(hasAuthentication()));
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

    public Intent getValidAuthenticationData() {
        Intent i = new Intent(Constants.AUTHENTICATION_ACTION);
        i.putExtra(Constants.ID_TOKEN_EXTRA, "");
        i.putExtra(Constants.ACCESS_TOKEN_EXTRA, "");
        i.putExtra(Constants.TOKEN_TYPE_EXTRA, "");
        i.putExtra(Constants.REFRESH_TOKEN_EXTRA, "");
        i.putExtra(Constants.PROFILE_EXTRA, Mockito.mock(UserProfile.class));
        return i;
    }

    public Intent getInvalidAuthenticationData() {
        Intent i = new Intent(Constants.AUTHENTICATION_ACTION);
        i.putExtra(Constants.TOKEN_TYPE_EXTRA, "");
        i.putExtra(Constants.REFRESH_TOKEN_EXTRA, "");
        i.putExtra(Constants.PROFILE_EXTRA, Mockito.mock(UserProfile.class));
        return i;
    }

    public Authentication authenticationFromData(Intent data) {
        String idToken = data.getStringExtra(Constants.ID_TOKEN_EXTRA);
        String accessToken = data.getStringExtra(Constants.ACCESS_TOKEN_EXTRA);
        String tokenType = data.getStringExtra(Constants.TOKEN_TYPE_EXTRA);
        String refreshToken = data.getStringExtra(Constants.REFRESH_TOKEN_EXTRA);
        Credentials credentials = new Credentials(idToken, accessToken, tokenType, refreshToken);
        UserProfile profile = (UserProfile) data.getSerializableExtra(Constants.PROFILE_EXTRA);

        return new Authentication(profile, credentials);
    }

}