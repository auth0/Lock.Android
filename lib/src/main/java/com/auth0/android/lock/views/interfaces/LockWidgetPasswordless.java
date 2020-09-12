/*
 * LockWidgetPasswordless.java
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

package com.auth0.android.lock.views.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public interface LockWidgetPasswordless extends LockWidgetOAuth {

    void onCountryCodeChangeRequest();

    void onPasswordlessCodeSent(@NonNull String emailOrNumber);

    /**
     * Change the Header Title to the given value and update the visibility depending on the Contextual Header Title flag.
     *
     * @param titleRes the string resource to use as title.
     */
    void updateHeaderTitle(@StringRes int titleRes);

    /**
     * Return the Header Title to the default text and visibility depending on the Contextual Header Title flag.
     */
    void resetHeaderTitle();
}
