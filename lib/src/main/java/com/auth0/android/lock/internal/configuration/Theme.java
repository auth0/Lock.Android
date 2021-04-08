/*
 * Theme.java
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;

import com.auth0.android.lock.R;

/**
 * Helper class to resolve Lock.Theme values.
 * <p>
 * Disclaimer: The classes in the internal package may change in the future. Don't use them directly.
 */
public class Theme implements Parcelable {

    private final int headerTitle;
    private final int headerLogo;
    private final int headerColor;
    private final int headerTitleColor;
    private final int primaryColor;
    private final int darkPrimaryColor;

    private Theme(int headerTitle, int headerLogo, int headerColor, int headerTitleColor, int primaryColor, int darkPrimaryColor) {
        this.headerTitle = headerTitle;
        this.headerLogo = headerLogo;
        this.headerColor = headerColor;
        this.headerTitleColor = headerTitleColor;
        this.primaryColor = primaryColor;
        this.darkPrimaryColor = darkPrimaryColor;
    }

    @SuppressLint("ResourceType")
    private String resolveStringResource(Context context, @StringRes int res, @AttrRes int attrName) {
        if (res > 0) {
            return context.getString(res);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrName, typedValue, true);
        return context.getString(typedValue.resourceId);
    }

    @SuppressLint("ResourceType")
    @ColorInt
    private int resolveColorResource(Context context, @ColorRes int res, @AttrRes int attrName) {
        if (res > 0) {
            return ContextCompat.getColor(context, res);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrName, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
    }

    @SuppressLint("ResourceType")
    private Drawable resolveDrawableResource(Context context, @DrawableRes int res, @AttrRes int attrName) {
        if (res > 0) {
            return ContextCompat.getDrawable(context, res);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attrName, typedValue, true);
        return ContextCompat.getDrawable(context, typedValue.resourceId);
    }

    @NonNull
    public String getHeaderTitle(@NonNull Context context) {
        return resolveStringResource(context, headerTitle, R.attr.Auth0_HeaderTitle);
    }

    @NonNull
    public Drawable getHeaderLogo(@NonNull Context context) {
        return resolveDrawableResource(context, headerLogo, R.attr.Auth0_HeaderLogo);
    }

    @ColorInt
    public int getHeaderColor(@NonNull Context context) {
        return resolveColorResource(context, headerColor, R.attr.Auth0_HeaderBackground);
    }

    @ColorInt
    public int getHeaderTitleColor(@NonNull Context context) {
        return resolveColorResource(context, headerTitleColor, R.attr.Auth0_HeaderTitleColor);
    }

    @ColorInt
    public int getPrimaryColor(@NonNull Context context) {
        return resolveColorResource(context, primaryColor, R.attr.Auth0_PrimaryColor);
    }

    @ColorInt
    public int getDarkPrimaryColor(@NonNull Context context) {
        return resolveColorResource(context, darkPrimaryColor, R.attr.Auth0_DarkPrimaryColor);
    }

    int getCustomHeaderTitleRes() {
        return headerTitle;
    }

    int getCustomHeaderLogoRes() {
        return headerLogo;
    }

    int getCustomHeaderColorRes() {
        return headerColor;
    }

    int getCustomHeaderTitleColorRes() {
        return headerTitleColor;
    }

    int getCustomPrimaryColorRes() {
        return primaryColor;
    }

    int getCustomDarkPrimaryColorRes() {
        return darkPrimaryColor;
    }

    protected Theme(@NonNull Parcel in) {
        headerTitle = in.readInt();
        headerLogo = in.readInt();
        headerColor = in.readInt();
        headerTitleColor = in.readInt();
        primaryColor = in.readInt();
        darkPrimaryColor = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(headerTitle);
        dest.writeInt(headerLogo);
        dest.writeInt(headerColor);
        dest.writeInt(headerTitleColor);
        dest.writeInt(primaryColor);
        dest.writeInt(darkPrimaryColor);
    }

    public static final Parcelable.Creator<Theme> CREATOR = new Parcelable.Creator<Theme>() {
        @Override
        public Theme createFromParcel(Parcel in) {
            return new Theme(in);
        }

        @Override
        public Theme[] newArray(int size) {
            return new Theme[size];
        }
    };


    static Builder newBuilder() {
        return new Theme.Builder();
    }

    static class Builder {

        private int headerTitleRes;
        private int headerLogoRes;
        private int headerColorRes;
        private int headerTitleColorRes;
        private int primaryColorRes;
        private int darkPrimaryColorRes;

        public Builder withHeaderTitle(@StringRes int title) {
            headerTitleRes = title;
            return this;
        }

        public Builder withHeaderLogo(@DrawableRes int logo) {
            headerLogoRes = logo;
            return this;
        }

        public Builder withHeaderColor(@ColorRes int color) {
            headerColorRes = color;
            return this;
        }

        public Builder withHeaderTitleColor(@ColorRes int color) {
            headerTitleColorRes = color;
            return this;
        }

        public Builder withPrimaryColor(@ColorRes int primary) {
            primaryColorRes = primary;
            return this;
        }

        public Builder withDarkPrimaryColor(@ColorRes int darkPrimary) {
            darkPrimaryColorRes = darkPrimary;
            return this;
        }

        public Theme build() {
            return new Theme(headerTitleRes, headerLogoRes, headerColorRes, headerTitleColorRes, primaryColorRes, darkPrimaryColorRes);
        }
    }
}
