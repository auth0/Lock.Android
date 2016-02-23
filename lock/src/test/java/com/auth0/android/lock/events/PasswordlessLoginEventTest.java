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

import com.auth0.Auth0;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.AuthenticationRequest;
import com.auth0.request.ParameterizableRequest;

import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PasswordlessLoginEventTest {

    private static final String CLIENT_ID = "client_id";
    private static final String DOMAIN = "domain";
    private static final String EMAIL = "an@email.com";
    private static final String CODE = "123456";
    private static final String CODE_MODE = "code";

    private static final String EMAIL_KEY = "email";
    private static final String SEND_KEY = "send";
    private static final String CONNECTION_KEY = "connection";

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
        Auth0 account = new Auth0(CLIENT_ID, DOMAIN);
        AuthenticationAPIClient client = new AuthenticationAPIClient(account);
        PasswordlessLoginEvent event = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_CODE, EMAIL);
        ParameterizableRequest<Void> codeRequest = event.getCodeRequest(client);
        Map<String, Object> parameters = codeRequest.getParameterBuilder().asDictionary();

        assertThat(codeRequest, is(not(nullValue())));
        assertThat((String) parameters.get(EMAIL_KEY), is(equalTo(EMAIL)));
        assertThat((String) parameters.get(SEND_KEY), is(equalTo(CODE_MODE)));
        assertThat(parameters, IsMapContaining.hasKey(CONNECTION_KEY));
    }

    @Test
    public void shouldGetLoginRequest() throws Exception {
        Auth0 account = new Auth0(CLIENT_ID, DOMAIN);
        AuthenticationAPIClient client = new AuthenticationAPIClient(account);
        PasswordlessLoginEvent event = new PasswordlessLoginEvent(PasswordlessMode.EMAIL_LINK, EMAIL);
        AuthenticationRequest loginRequest = event.getLoginRequest(client);

        assertThat(loginRequest, is(not(nullValue())));
    }
}