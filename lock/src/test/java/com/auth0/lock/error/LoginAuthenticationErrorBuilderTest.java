/*
 * LoginAuthenticationErrorBuilderTest.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.lock.error;

import com.auth0.android.BuildConfig;
import com.auth0.api.APIClientException;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.lock.util.AuthenticationErrorDefaultMatcher.hasDefaultTitleAndMessage;
import static com.auth0.lock.util.AuthenticationErrorDefaultMatcher.hasMessage;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/test/AndroidManifest.xml", resourceDir = "../../src/main/res")
public class LoginAuthenticationErrorBuilderTest {

    private AuthenticationErrorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new LoginAuthenticationErrorBuilder();
    }

    @Test
    public void shouldBuildAuthenticationError() throws Exception {
        assertThat(builder.buildFrom(new RuntimeException()), is(notNullValue()));
    }

    @Test
    public void shouldContainThrowable() throws Exception {
        Throwable throwable = new RuntimeException();
        assertThat(builder.buildFrom(throwable).getThrowable(), equalTo(throwable));
    }

    @Test
    public void shouldContainBasicMessages() throws Exception {
        AuthenticationError error = builder.buildFrom(new RuntimeException());
        assertThat(error, hasDefaultTitleAndMessage());
    }

    @Test
    public void shouldReturnInvalidCredentialsMessage() throws Exception {
        Map<String, Object> errors = new HashMap<>();
        errors.put(AuthenticationErrorBuilder.ERROR_KEY, "invalid_user_password");
        Throwable exception = new APIClientException("error", 400, errors);
        AuthenticationError error = builder.buildFrom(exception);
        assertThat(error, hasMessage(R.string.com_auth0_db_login_invalid_credentials_error_message));
        assertThat(error.getThrowable(), equalTo(exception));
    }

    @Test
    public void shouldReturnUnauthorizedMessage() throws Exception {
        Map<String, Object> errors = new HashMap<>();
        errors.put(AuthenticationErrorBuilder.ERROR_KEY, "unauthorized");
        Throwable exception = new APIClientException("error", 401, errors);
        AuthenticationError error = builder.buildFrom(exception);
        assertThat(error, hasMessage(R.string.com_auth0_db_login_unauthorized_error_message));
        assertThat(error.getThrowable(), equalTo(exception));
    }

    @Test
    public void shouldReturnCustomErrorMessage() throws Exception {
        Map<String, Object> errors = new HashMap<>();
        errors.put(AuthenticationErrorBuilder.ERROR_KEY, "other_error");
        errors.put("error_description", "custom error");
        Throwable exception = new APIClientException("error", 401, errors);
        AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessage(RuntimeEnvironment.application), equalTo("custom error"));
        assertThat(error.getThrowable(), equalTo(exception));
    }

}
