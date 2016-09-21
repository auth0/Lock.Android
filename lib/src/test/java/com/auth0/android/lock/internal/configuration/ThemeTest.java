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

package com.auth0.android.lock.internal.configuration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.auth0.android.lock.BuildConfig;
import com.auth0.android.lock.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, resourceDir = Config.DEFAULT_RES_FOLDER)
public class ThemeTest {

    @StringRes
    static final int STRING_RES = R.string.com_auth0_lock_header_title;
    @DrawableRes
    static final int DRAWABLE_RES = R.drawable.com_auth0_lock_ic_social_auth0;
    @ColorRes
    static final int COLOR_RES = R.color.com_auth0_lock_social_unknown;
    static final int NOT_SET_RES = 0;

    Theme.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = Theme.newBuilder();
    }

    @Test
    public void shouldResolveDefaultHeaderTitle() throws Exception {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;

        final String headerTitle = theme.getHeaderTitle(context);
        assertThat(headerTitle, is(equalTo(context.getString(getLockThemeResourceId(context, R.attr.Auth0_HeaderTitle)))));
    }

    @Test
    public void shouldResolveDefaultHeaderLogo() throws Exception {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;

        final Drawable headerLogo = theme.getHeaderLogo(context);
        assertThat(headerLogo, is(equalTo(ContextCompat.getDrawable(context, getLockThemeResourceId(context, R.attr.Auth0_HeaderLogo)))));
    }

    @Test
    public void shouldResolveDefaultHeaderColor() throws Exception {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getHeaderColor(context);
        assertThat(headerColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_HeaderBackground)))));
    }

    @Test
    public void shouldResolveDefaultHeaderTitleColor() throws Exception {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;

        final int titleColor = theme.getHeaderTitleColor(context);
        assertThat(titleColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_HeaderTitleColor)))));
    }

    @Test
    public void shouldResolveDefaultPrimaryColor() throws Exception {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;

        final int primaryColor = theme.getPrimaryColor(context);
        assertThat(primaryColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_PrimaryColor)))));
    }

    @Test
    public void shouldResolveDefaultDarkPrimaryColor() throws Exception {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;

        final int darkPrimaryColor = theme.getDarkPrimaryColor(context);
        assertThat(darkPrimaryColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_DarkPrimaryColor)))));
    }


    @Test
    public void shouldResolveCustomHeaderTitle() throws Exception {
        final Theme theme = builder.withHeaderTitle(STRING_RES).build();
        final Context context = RuntimeEnvironment.application;

        final String headerTitle = theme.getHeaderTitle(context);
        final String actualTitle = context.getString(STRING_RES);
        assertThat(headerTitle, is(equalTo(actualTitle)));
    }

    @Test
    public void shouldResolveCustomHeaderLogo() throws Exception {
        final Theme theme = builder.withHeaderLogo(DRAWABLE_RES).build();
        final Context context = RuntimeEnvironment.application;

        final Drawable headerLogo = theme.getHeaderLogo(context);
        final Drawable actualLogo = ContextCompat.getDrawable(context, DRAWABLE_RES);
        assertThat(headerLogo, is(equalTo(actualLogo)));
    }

    @Test
    public void shouldResolveCustomHeaderColor() throws Exception {
        final Theme theme = builder.withHeaderColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getHeaderColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(headerColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldResolveCustomHeaderTitleColor() throws Exception {
        final Theme theme = builder.withHeaderTitleColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int titleColor = theme.getHeaderTitleColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(titleColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldResolveCustomPrimaryColor() throws Exception {
        final Theme theme = builder.withPrimaryColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getPrimaryColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(headerColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldResolveCustomDarkPrimaryColor() throws Exception {
        final Theme theme = builder.withDarkPrimaryColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getDarkPrimaryColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(headerColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldSetHeaderTitle() throws Exception {
        final Theme theme = builder.withHeaderTitle(STRING_RES)
                .build();
        assertThat(theme.getCustomHeaderTitleRes(), is(equalTo(STRING_RES)));
    }

    @Test
    public void shouldSetHeaderLogo() throws Exception {
        final Theme theme = builder.withHeaderLogo(DRAWABLE_RES)
                .build();
        assertThat(theme.getCustomHeaderLogoRes(), is(equalTo(DRAWABLE_RES)));
    }

    @Test
    public void shouldSetHeaderColor() throws Exception {
        final Theme theme = builder.withHeaderColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomHeaderColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetHeaderTitleColor() throws Exception {
        final Theme theme = builder.withHeaderTitleColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomHeaderTitleColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetPrimaryColor() throws Exception {
        final Theme theme = builder.withPrimaryColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomPrimaryColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetDarkPrimaryColor() throws Exception {
        final Theme theme = builder.withDarkPrimaryColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomDarkPrimaryColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldNotHaveCustomValues() throws Exception {
        final Theme theme = builder.build();
        assertThat(theme.getCustomHeaderTitleRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomHeaderLogoRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomHeaderColorRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomHeaderTitleColorRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomPrimaryColorRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomDarkPrimaryColorRes(), is(equalTo(NOT_SET_RES)));
    }

    private int getLockThemeResourceId(Context context, @AttrRes int attrResId) {
        TypedArray a = context.obtainStyledAttributes(com.auth0.android.lock.R.style.Lock_Theme, new int[]{attrResId});
        final int index = a.getResourceId(0, 0);
        a.recycle();
        return index;
    }

}