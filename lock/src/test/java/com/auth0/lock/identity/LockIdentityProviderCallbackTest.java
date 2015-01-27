/*
 * LockIdentityProviderCallbackTest.java
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

package com.auth0.lock.identity;

import android.app.Dialog;

import com.auth0.core.Token;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.IdentityProviderAuthenticationEvent;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.event.SystemErrorEvent;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LockIdentityProviderCallbackTest {

    private static final String ACCESS_TOKEN = "I.O.U an access token";
    private static final String SERVICE_NAME = "facebook";

    @Mock
    private Bus bus;
    @Mock
    private Token token;
    @Mock
    private Dialog dialog;
    @Mock
    private Throwable cause;

    private LockIdentityProviderCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        callback = new LockIdentityProviderCallback(bus);
    }

    @Test
    public void shouldSendAuthenticationEventWithToken() throws Exception {
        callback.onSuccess(token);
        ArgumentCaptor<IdentityProviderAuthenticationEvent> captor = ArgumentCaptor.forClass(IdentityProviderAuthenticationEvent.class);
        verify(bus).post(captor.capture());
        assertThat(captor.getValue().getToken(), equalTo(token));
    }

    @Test
    public void shouldSendSystemErrorEventWithDialog() throws Exception {
        callback.onFailure(dialog);
        ArgumentCaptor<SystemErrorEvent> captor = ArgumentCaptor.forClass(SystemErrorEvent.class);
        verify(bus).post(captor.capture());
        assertThat(captor.getValue().getErrorDialog(), equalTo(dialog));
    }

    @Test
    public void shouldSendAuthenticationErrorEvent() throws Exception {
        callback.onFailure(R.string.db_login_error_title, R.string.db_login_error_message, cause);
        ArgumentCaptor<AuthenticationError> captor = ArgumentCaptor.forClass(AuthenticationError.class);
        verify(bus).post(captor.capture());
        final AuthenticationError event = captor.getValue();
        assertThat(event.getThrowable(), equalTo(cause));
    }

    @Test
    public void shouldSendSocialCredentialsEvent() throws Exception {
        callback.onSuccess(SERVICE_NAME, ACCESS_TOKEN);
        ArgumentCaptor<SocialCredentialEvent> captor = ArgumentCaptor.forClass(SocialCredentialEvent.class);
        verify(bus).post(captor.capture());
        final SocialCredentialEvent event = captor.getValue();
        assertThat(event.getService(), equalTo(SERVICE_NAME));
        assertThat(event.getAccessToken(), equalTo(ACCESS_TOKEN));
    }
}