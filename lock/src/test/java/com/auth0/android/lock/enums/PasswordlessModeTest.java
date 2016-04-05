/*
 * PasswordlessModeTest.java
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

package com.auth0.android.lock.enums;

import com.auth0.android.lock.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class PasswordlessModeTest {

    @Test
    public void shouldReturnNullPasswordlessModeOnNegativeOrdinal() throws Exception {
        PasswordlessMode mode = PasswordlessMode.from(-1);
        assertThat(mode, is(nullValue()));
    }

    @Test
    public void shouldReturnNullPasswordlessModeOnOrdinalGreaterThanLength() throws Exception {
        PasswordlessMode mode = PasswordlessMode.from(4);
        assertThat(mode, is(nullValue()));
    }

    @Test
    public void shouldReturnSmsLinkPasswordlessMode() throws Exception {
        PasswordlessMode mode = PasswordlessMode.from(0);
        assertThat(mode, is(not(nullValue())));
        assertThat(mode, is(equalTo(PasswordlessMode.SMS_LINK)));
    }

    @Test
    public void shouldReturnSmsCodePasswordlessMode() throws Exception {
        PasswordlessMode mode = PasswordlessMode.from(1);
        assertThat(mode, is(not(nullValue())));
        assertThat(mode, is(equalTo(PasswordlessMode.SMS_CODE)));
    }

    @Test
    public void shouldReturnEmailLinkPasswordlessMode() throws Exception {
        PasswordlessMode mode = PasswordlessMode.from(2);
        assertThat(mode, is(not(nullValue())));
        assertThat(mode, is(equalTo(PasswordlessMode.EMAIL_LINK)));
    }

    @Test
    public void shouldReturnEmailCodePasswordlessMode() throws Exception {
        PasswordlessMode mode = PasswordlessMode.from(3);
        assertThat(mode, is(not(nullValue())));
        assertThat(mode, is(equalTo(PasswordlessMode.EMAIL_CODE)));
    }
}