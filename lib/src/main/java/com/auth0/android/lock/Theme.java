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

package com.auth0.android.lock;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.TypedValue;

public class Theme implements Parcelable {

    private final int headerTitle;
    private final int headerLogo;
    private final int headerColor;
    private final int primaryColor;
    private final int darkPrimaryColor;

    private Theme(int headerTitle, int headerLogo, int headerColor, int primaryColor, int darkPrimaryColor) {
        this.headerTitle = headerTitle;
        this.headerLogo = headerLogo;
        this.headerColor = headerColor;
        this.primaryColor = primaryColor;
        this.darkPrimaryColor = darkPrimaryColor;
    }

    @AnyRes
    private int getAttributeValue(Resources.Theme theme, int resId) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }

    @StringRes
    public int getHeaderTitle(Resources.Theme theme) {
        return headerTitle <= 0 ? getAttributeValue(theme, R.attr.Auth0_HeaderTitle) : headerTitle;
    }

    @DrawableRes
    public int getHeaderLogo(Resources.Theme theme) {
        return headerLogo <= 0 ? getAttributeValue(theme, R.attr.Auth0_HeaderLogo) : headerLogo;
    }

    @ColorRes
    public int getHeaderColor(Resources.Theme theme) {
        return headerColor <= 0 ? getAttributeValue(theme, R.attr.Auth0_HeaderBackground) : headerColor;
    }

    @ColorRes
    public int getPrimaryColor(Resources.Theme theme) {
        return primaryColor <= 0 ? getAttributeValue(theme, R.attr.Auth0_PrimaryColor) : primaryColor;
    }

    @ColorRes
    public int getDarkPrimaryColor(Resources.Theme theme) {
        return darkPrimaryColor <= 0 ? getAttributeValue(theme, R.attr.Auth0_DarkPrimaryColor) : darkPrimaryColor;
    }

    protected Theme(Parcel in) {
        headerTitle = in.readInt();
        headerLogo = in.readInt();
        headerColor = in.readInt();
        primaryColor = in.readInt();
        darkPrimaryColor = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(headerTitle);
        dest.writeInt(headerLogo);
        dest.writeInt(headerColor);
        dest.writeInt(primaryColor);
        dest.writeInt(darkPrimaryColor);
    }

    @SuppressWarnings("unused")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Theme theme = (Theme) o;

        if (headerTitle != theme.headerTitle) return false;
        if (headerLogo != theme.headerLogo) return false;
        if (headerColor != theme.headerColor) return false;
        if (primaryColor != theme.primaryColor) return false;
        return darkPrimaryColor == theme.darkPrimaryColor;

    }

    @Override
    public int hashCode() {
        int result = headerTitle;
        result = 31 * result + headerLogo;
        result = 31 * result + headerColor;
        result = 31 * result + primaryColor;
        result = 31 * result + darkPrimaryColor;
        return result;
    }

    public static Builder newBuilder() {
        return new Theme.Builder();
    }

    public static class Builder {

        private int headerTitleRes;
        private int headerLogoRes;
        private int headerColorRes;
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

        public Builder withHeaderColor(@ColorRes int header) {
            headerColorRes = header;
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
            return new Theme(headerTitleRes, headerLogoRes, headerColorRes, primaryColorRes, darkPrimaryColorRes);
        }
    }
}
