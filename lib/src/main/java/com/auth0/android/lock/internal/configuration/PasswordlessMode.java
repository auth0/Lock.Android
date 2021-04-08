/*
 * PasswordlessMode.java
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

package com.auth0.android.lock.internal.configuration;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.internal.configuration.PasswordlessMode.DISABLED;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_LINK;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_LINK;

@IntDef({DISABLED, SMS_LINK, SMS_CODE, EMAIL_LINK, EMAIL_CODE})
@Retention(RetentionPolicy.SOURCE)
public @interface PasswordlessMode {
    int DISABLED = 0;
    int SMS_LINK = 1;
    int SMS_CODE = 2;
    int EMAIL_LINK = 3;
    int EMAIL_CODE = 4;
}