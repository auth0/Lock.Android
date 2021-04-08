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
import android.graphics.drawable.Drawable;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;

import com.auth0.android.lock.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
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
    public void setUp() {
        builder = Theme.newBuilder();
    }

    @Test
    public void shouldResolveDefaultHeaderTitle() {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;
        context.setTheme(R.style.Lock_Theme);

        final String headerTitle = theme.getHeaderTitle(context);
        assertThat(headerTitle, is(equalTo(context.getString(getLockThemeResourceId(context, R.attr.Auth0_HeaderTitle)))));
    }

    @Test
    public void shouldResolveDefaultHeaderLogo() {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;
        context.setTheme(R.style.Lock_Theme);
        Drawable drawable1 = theme.getHeaderLogo(context);
        Drawable drawable2 = ContextCompat.getDrawable(context, getLockThemeResourceId(context, R.attr.Auth0_HeaderLogo));
        int d1 = shadowOf(drawable1).getCreatedFromResId();
        int d2 = shadowOf(drawable2).getCreatedFromResId();
        Assert.assertThat(d1, is(equalTo(d2)));
    }

    @Test
    public void shouldResolveDefaultHeaderColor() {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;
        context.setTheme(R.style.Lock_Theme);

        final int headerColor = theme.getHeaderColor(context);
        assertThat(headerColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_HeaderBackground)))));
    }

    @Test
    public void shouldResolveDefaultHeaderTitleColor() {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;
        context.setTheme(R.style.Lock_Theme);

        final int titleColor = theme.getHeaderTitleColor(context);
        assertThat(titleColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_HeaderTitleColor)))));
    }

    @Test
    public void shouldResolveDefaultPrimaryColor() {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;
        context.setTheme(R.style.Lock_Theme);

        final int primaryColor = theme.getPrimaryColor(context);
        assertThat(primaryColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_PrimaryColor)))));
    }

    @Test
    public void shouldResolveDefaultDarkPrimaryColor() {
        final Theme theme = builder.build();
        final Context context = RuntimeEnvironment.application;
        context.setTheme(R.style.Lock_Theme);

        final int darkPrimaryColor = theme.getDarkPrimaryColor(context);
        assertThat(darkPrimaryColor, is(equalTo(ContextCompat.getColor(context, getLockThemeResourceId(context, R.attr.Auth0_DarkPrimaryColor)))));
    }


    @Test
    public void shouldResolveCustomHeaderTitle() {
        final Theme theme = builder.withHeaderTitle(STRING_RES).build();
        final Context context = RuntimeEnvironment.application;

        final String headerTitle = theme.getHeaderTitle(context);
        final String actualTitle = context.getString(STRING_RES);
        assertThat(headerTitle, is(equalTo(actualTitle)));
    }

    @Test
    public void shouldResolveCustomHeaderLogo() {
        final Theme theme = builder.withHeaderLogo(DRAWABLE_RES).build();
        final Context context = RuntimeEnvironment.application;
        Drawable drawable1 = theme.getHeaderLogo(context);
        Drawable drawable2 = ContextCompat.getDrawable(context, DRAWABLE_RES);
        int d1 = shadowOf(drawable1).getCreatedFromResId();
        int d2 = shadowOf(drawable2).getCreatedFromResId();
        Assert.assertThat(d1, is(equalTo(d2)));
    }

    @Test
    public void shouldResolveCustomHeaderColor() {
        final Theme theme = builder.withHeaderColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getHeaderColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(headerColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldResolveCustomHeaderTitleColor() {
        final Theme theme = builder.withHeaderTitleColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int titleColor = theme.getHeaderTitleColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(titleColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldResolveCustomPrimaryColor() {
        final Theme theme = builder.withPrimaryColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getPrimaryColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(headerColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldResolveCustomDarkPrimaryColor() {
        final Theme theme = builder.withDarkPrimaryColor(COLOR_RES).build();
        final Context context = RuntimeEnvironment.application;

        final int headerColor = theme.getDarkPrimaryColor(context);
        final int actualColor = ContextCompat.getColor(context, COLOR_RES);
        assertThat(headerColor, is(equalTo(actualColor)));
    }

    @Test
    public void shouldSetHeaderTitle() {
        final Theme theme = builder.withHeaderTitle(STRING_RES)
                .build();
        assertThat(theme.getCustomHeaderTitleRes(), is(equalTo(STRING_RES)));
    }

    @Test
    public void shouldSetHeaderLogo() {
        final Theme theme = builder.withHeaderLogo(DRAWABLE_RES)
                .build();
        assertThat(theme.getCustomHeaderLogoRes(), is(equalTo(DRAWABLE_RES)));
    }

    @Test
    public void shouldSetHeaderColor() {
        final Theme theme = builder.withHeaderColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomHeaderColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetHeaderTitleColor() {
        final Theme theme = builder.withHeaderTitleColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomHeaderTitleColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetPrimaryColor() {
        final Theme theme = builder.withPrimaryColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomPrimaryColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldSetDarkPrimaryColor() {
        final Theme theme = builder.withDarkPrimaryColor(COLOR_RES)
                .build();
        assertThat(theme.getCustomDarkPrimaryColorRes(), is(equalTo(COLOR_RES)));
    }

    @Test
    public void shouldNotHaveCustomValues() {
        final Theme theme = builder.build();
        assertThat(theme.getCustomHeaderTitleRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomHeaderLogoRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomHeaderColorRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomHeaderTitleColorRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomPrimaryColorRes(), is(equalTo(NOT_SET_RES)));
        assertThat(theme.getCustomDarkPrimaryColorRes(), is(equalTo(NOT_SET_RES)));
    }

    private int getLockThemeResourceId(Context context, @AttrRes int attrResId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrResId, typedValue, true);
        return typedValue.resourceId;
    }

}