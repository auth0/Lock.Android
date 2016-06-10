/*
 * ThemeTest.java
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

package com.auth0.android.lock;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = android.support.v7.appcompat.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ThemeTest {

    @StringRes
    static final int STRING_RES = 1;
    @DrawableRes
    static final int DRAWABLE_RES = 2;
    @ColorRes
    static final int COLOR_RES = 3;

    Theme.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = Theme.newBuilder();
    }

    @Test
    public void shouldSetHeaderTitle() throws Exception {
        final Theme theme = builder.withHeaderTitle(STRING_RES)
                .build();
        assertThat(theme.getHeaderTitle(null), is(equalTo(STRING_RES)));
    }

    @Test
    public void shouldSetHeaderColor() throws Exception {
        final Theme theme = builder.withHeaderColor(COLOR_RES)
                .build();
        assertThat(theme.getHeaderColor(null), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetHeaderLogo() throws Exception {
        final Theme theme = builder.withHeaderLogo(DRAWABLE_RES)
                .build();
        assertThat(theme.getHeaderLogo(null), is(equalTo(DRAWABLE_RES)));
    }

    @Test
    public void shouldSetPrimaryColor() throws Exception {
        final Theme theme = builder.withPrimaryColor(COLOR_RES)
                .build();
        assertThat(theme.getPrimaryColor(null), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetDarkPrimaryColor() throws Exception {
        final Theme theme = builder.withDarkPrimaryColor(COLOR_RES)
                .build();
        assertThat(theme.getDarkPrimaryColor(null), is(equalTo(COLOR_RES)));
    }
}