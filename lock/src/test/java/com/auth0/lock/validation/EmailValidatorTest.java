/*
 * EmailValidatorTest.java
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
public class EmailValidatorTest {

    private Validator validator;
    private Fragment fragment;
    private View view;
    private CredentialField field;
    private Editable editable;

    @Before
    public void setUp() throws Exception {
        validator = new EmailValidator(R.id.db_change_password_username_field, R.string.invalid_credentials_title, R.string.invalid_email_message);
        fragment = mock(Fragment.class);
        view = mock(View.class);
        field = mock(CredentialField.class);
        editable = mock(Editable.class);
        when(fragment.getView()).thenReturn(view);
        when(view.findViewById(eq(R.id.db_change_password_username_field))).thenReturn(field);
        when(field.getText()).thenReturn(editable);
    }

    @Test
    public void shouldReturnNullWithValidEmail() throws Exception {
        when(editable.toString()).thenReturn("mail@mail.com");
        assertThat(validator.validateFrom(fragment), is(nullValue()));
    }

    @Test
    public void shouldFailWithEmptyEmail() throws Exception {
        when(editable.toString()).thenReturn("");
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.invalid_email_message));
    }

    @Test
    public void shouldFailWithInvalidEmail() throws Exception {
        when(editable.toString()).thenReturn("pepe@p");
        assertThat(validator.validateFrom(fragment), hasError(R.string.invalid_credentials_title, R.string.invalid_email_message));
    }

}
