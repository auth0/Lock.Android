/*
 * PasswordlessLoginEventTest.java
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

package com.auth0.android.lock.events;

import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.PasswordlessType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class PasswordlessLoginEventTest {

    private static final String CLIENT_ID = "client_id";
    private static final String DOMAIN = "domain";
    private static final String EMAIL = "an@email.com";
    private static final String CODE = "123456";
    private static final String CODE_MODE = "code";

    private static final String EMAIL_KEY = "email";
    private static final String SEND_KEY = "send";
    private static final String CONNECTION_KEY = "connection";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldHaveNullCodeByDefault() throws Exception {
        PasswordlessLoginEvent event = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_LINK, EMAIL);

        Assert.assertThat(event.getEmailOrNumber(), is(equalTo(EMAIL)));
        Assert.assertThat(event.getMode(), is(equalTo(PasswordlessMode.EMAIL_LINK)));
        Assert.assertThat(event.getCode(), is(nullValue()));
    }

    @Test
    public void shouldSetTheCode() throws Exception {
        PasswordlessLoginEvent event = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_CODE, EMAIL, CODE);

        Assert.assertThat(event.getEmailOrNumber(), is(equalTo(EMAIL)));
        Assert.assertThat(event.getMode(), is(equalTo(PasswordlessMode.EMAIL_CODE)));
        Assert.assertThat(event.getCode(), is(equalTo(CODE)));
    }

    @Test
    public void shouldGetCodeRequest() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        PasswordlessLoginEvent emailCodeEvent = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_CODE, EMAIL);
        emailCodeEvent.getCodeRequest(client);
        verify(client).passwordlessWithEmail(EMAIL, PasswordlessType.CODE);

        PasswordlessLoginEvent emailLinkEvent = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_LINK, EMAIL);
        emailLinkEvent.getCodeRequest(client);
        verify(client).passwordlessWithEmail(EMAIL, PasswordlessType.LINK_ANDROID);
    }

    @Test
    public void shouldGetLoginRequest() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        PasswordlessLoginEvent emailCodeEvent = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_CODE, EMAIL, CODE);
        emailCodeEvent.getLoginRequest(client);
        verify(client).loginWithEmail(EMAIL, CODE);

        PasswordlessLoginEvent emailLinkEvent = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_LINK, EMAIL);
        emailLinkEvent.getLoginRequest(client);
        verify(client).loginWithEmail(EMAIL, CODE);
    }
}