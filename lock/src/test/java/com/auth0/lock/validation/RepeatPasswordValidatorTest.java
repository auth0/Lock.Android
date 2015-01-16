/*
 * RepeatPasswordValidatorTest.java
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
import android.text.Editable;
import android.view.View;

import com.auth0.lock.R;
import com.auth0.lock.widget.CredentialField;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.lock.util.AuthenticationErrorDefaultMatcher.hasError;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by hernan on 12/15/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class RepeatPasswordValidatorTest {
    private static final String PASSWORD = "a very long long long password";
    private Validator validator;
    private Fragment fragment;
    private View view;
    private CredentialField field;
    private CredentialField passwordField;
    private Editable editable;

    @Before
    public void setUp() throws Exception {
        validator = new RepeatPasswordValidator(R.id.db_change_password_repeat_password_field, R.id.db_change_password_password_field, R.string.invalid_credentials_title, R.string.db_reset_password_invalid_repeat_password_message);
        fragment = mock(Fragment.class);
        view = mock(View.class);
        field = mock(CredentialField.class);
        editable = mock(Editable.class);
        passwordField = mock(CredentialField.class);
        when(fragment.getView()).thenReturn(view);
        when(view.findViewById(eq(R.id.db_change_password_repeat_password_field))).thenReturn(field);
        when(view.findViewById(eq(R.id.db_change_password_password_field))).thenReturn(passwordField);
        when(passwordField.getText()).thenReturn(editable);
        when(field.getText()).thenReturn(editable);
    }

    @Test
    public void shouldReturnNullWithValidPassword() throws Exception {
        when(editable.toString()).thenReturn(PASSWORD);
        assertThat(validator.validateFrom(fragment), is(nullValue()));
    }

    @Test
    public void shouldFailWithEmptyPassword() throws Exception {
        when(editable.toString()).thenReturn("");
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.db_reset_password_invalid_repeat_password_message));
    }

    @Test
    public void shouldFailWithNonMatchingPassword() throws Exception {
        Editable otherEditable = mock(Editable.class);
        when(field.getText()).thenReturn(otherEditable);
        when(editable.toString()).thenReturn("1234567890");
        when(otherEditable.toString()).thenReturn("qwertyuiop");
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.db_reset_password_invalid_repeat_password_message));
    }

}
