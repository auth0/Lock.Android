/*
 * DatabaseSignUpEventTest.java
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

import com.auth0.android.auth0.lib.authentication.AuthenticationAPIClient;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class DatabaseSignUpEventTest {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    @Test
    public void shouldSetAllValues() throws Exception {
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);

        Assert.assertThat(event.getEmail(), is(equalTo(EMAIL)));
        Assert.assertThat(event.getPassword(), is(equalTo(PASSWORD)));
        Assert.assertThat(event.getUsername(), is(equalTo(USERNAME)));
    }

    @Test
    public void shouldSetNullUsername() throws Exception {
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);

        Assert.assertThat(event.getEmail(), is(equalTo(EMAIL)));
        Assert.assertThat(event.getPassword(), is(equalTo(PASSWORD)));
        Assert.assertThat(event.getUsername(), is(nullValue()));
    }

    @Test
    public void shouldGetSignUpRequestWithUsername() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        event.getSignUpRequest(client);
        Mockito.verify(client).signUp(EMAIL, PASSWORD, USERNAME);
    }

    @Test
    public void shouldGetSignUpRequestWithoutUsername() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.getSignUpRequest(client);
        Mockito.verify(client).signUp(EMAIL, PASSWORD);
    }

    @Test
    public void shouldGetCreateUserRequestWithUsername() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, USERNAME);
        event.getCreateUserRequest(client);
        Mockito.verify(client).createUser(EMAIL, PASSWORD, USERNAME);
    }

    @Test
    public void shouldGetCreateUserRequestWithoutUsername() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent(EMAIL, PASSWORD, null);
        event.getCreateUserRequest(client);
        Mockito.verify(client).createUser(EMAIL, PASSWORD);
    }

}