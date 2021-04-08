/*
 * Country.java
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

package com.auth0.android.lock.adapters;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Country implements Comparable<Country> {
    private final String isoCode;
    private final String dialCode;

    public Country(@NonNull String isoCode, @NonNull String dialCode) {
        this.isoCode = isoCode;
        this.dialCode = dialCode;
    }

    @NonNull
    public String getDialCode() {
        return dialCode;
    }

    @NonNull
    public String getIsoCode() {
        return isoCode;
    }

    @NonNull
    public String getDisplayName() {
        Locale locale = new Locale("", isoCode);
        return locale.getDisplayName();
    }

    @Override
    public int compareTo(@NonNull Country another) {
        return getDisplayName().compareToIgnoreCase(another.getDisplayName());
    }
}
