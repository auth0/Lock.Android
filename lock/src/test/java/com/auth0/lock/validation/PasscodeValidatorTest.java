/*
 * PasscodeValidatorTest.java
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

package com.auth0.lock.validation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = com.auth0.lock.BuildConfig.class, sdk = 23, manifest = "src/test/AndroidManifest.xml", resourceDir = "src/main/res")
public class PasscodeValidatorTest {

    private static final int FIELD_RESOURCE = 0;
    private static final int ERROR_TITLE_RESOURCE = 1;
    private static final int ERROR_MESSAGE_RESOURCE = 2;

    private PasscodeValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new PasscodeValidator(FIELD_RESOURCE, ERROR_TITLE_RESOURCE, ERROR_MESSAGE_RESOURCE);
    }

    @Test
    public void shouldValidatePasscode() throws Exception {
        assertThat(validator.doValidate("12345678"), is(true));
    }

    @Test
    public void shouldFailValidationWithNull() throws Exception {
        assertThat(validator.doValidate(null), is(false));
    }

    @Test
    public void shouldFailValidationWithEmptyPasscode() throws Exception {
        assertThat(validator.doValidate(""), is(false));
    }
}