/*
 * SignUpValidatorTest.java
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

package com.auth0.lock.validation;

import android.app.Fragment;

import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.lock.util.AuthenticationErrorDefaultMatcher.hasError;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by hernan on 12/16/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class SignUpValidatorTest {

    private Validator validator;

    @Mock
    private Validator emailValidator;
    @Mock
    private Validator passwordValidator;
    @Mock
    private Fragment fragment;
    @Mock
    private AuthenticationError emailError;
    @Mock
    private AuthenticationError passwordError;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        validator = new SignUpValidator(emailValidator, passwordValidator);
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(null);
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(null);
    }

    @Test
    public void shouldReturnNullOnSuccess() throws Exception {
        assertThat(validator.validateFrom(fragment), is(nullValue()));
    }

    @Test
    public void shouldReturnEmailErrorOnly() throws Exception {
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(emailError);
        assertThat(validator.validateFrom(fragment), equalTo(emailError));
    }

    @Test
    public void shouldReturnPasswordErrorOnly() throws Exception {
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(passwordError);
        assertThat(validator.validateFrom(fragment), equalTo(passwordError));
    }

    @Test
    public void shouldReturnCredentialErrorWhenBothFails() throws Exception {
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(emailError);
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(passwordError);
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.invalid_credentials_message));
    }
}
