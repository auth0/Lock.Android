/*
 * ResetPasswordValidatorTest.java
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

import android.support.v4.app.Fragment;

import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.lock.util.AuthenticationErrorDefaultMatcher.hasError;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ResetPasswordValidatorTest {

    private Validator validator;
    private Fragment fragment;
    private Validator emailValidator;
    private Validator passwordValidator;
    private Validator repeatValidator;

    @Before
    public void setUp() throws Exception {
        fragment = mock(Fragment.class);
        emailValidator = mock(Validator.class);
        passwordValidator = mock(Validator.class);
        repeatValidator = mock(Validator.class);
        validator = new ResetPasswordValidator(emailValidator, passwordValidator, repeatValidator, R.string.invalid_credentials_message);
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(null);
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(null);
        when(repeatValidator.validateFrom(eq(fragment))).thenReturn(null);
    }

    @Test
    public void shouldReturnNullWhenAllValidationPass() throws Exception {
        assertThat(validator.validateFrom(fragment), is(nullValue()));
    }

    @Test
    public void shouldReturnEmailErrorOnly() throws Exception {
        AuthenticationError error = mock(AuthenticationError.class);
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(error);
        assertThat(validator.validateFrom(fragment), equalTo(error));
    }

    @Test
    public void shouldReturnPasswordErrorOnly() throws Exception {
        AuthenticationError error = mock(AuthenticationError.class);
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(error);
        assertThat(validator.validateFrom(fragment), equalTo(error));
    }

    @Test
    public void shouldReturnRepeatPasswordErrorOnly() throws Exception {
        AuthenticationError error = mock(AuthenticationError.class);
        when(repeatValidator.validateFrom(eq(fragment))).thenReturn(error);
        assertThat(validator.validateFrom(fragment), equalTo(error));
    }

    @Test
    public void shouldReturnInvalidRepeatPasswordWhenPasswordsValidationFails() throws Exception {
        AuthenticationError passwordError = mock(AuthenticationError.class);
        AuthenticationError repeatError = mock(AuthenticationError.class);
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(passwordError);
        when(repeatValidator.validateFrom(eq(fragment))).thenReturn(repeatError);
        assertThat(validator.validateFrom(fragment), equalTo(repeatError));
    }

    @Test
    public void shouldReturnInvalidCredentialsError() throws Exception {
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(mock(AuthenticationError.class));
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(mock(AuthenticationError.class));
        when(repeatValidator.validateFrom(eq(fragment))).thenReturn(mock(AuthenticationError.class));
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.invalid_credentials_message));
    }

    @Test
    public void shouldReturnInvalidCredentialsErrorWhenEmailAdnPasswordFails() throws Exception {
        when(emailValidator.validateFrom(eq(fragment))).thenReturn(mock(AuthenticationError.class));
        when(passwordValidator.validateFrom(eq(fragment))).thenReturn(mock(AuthenticationError.class));
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.invalid_credentials_message));
    }

}
