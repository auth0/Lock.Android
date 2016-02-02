/*
 * SocialButtonConfig.java
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

package com.auth0.android.lock.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.Strategy;

class SocialConfig {
    private static final String ICON_RESOURCE_FORMAT = "com_auth0_lock_social_icon_%s";
    private static final String TITLE_RESOURCE_FORMAT = "com_auth0_lock_social_%s";
    private static final String BACKGROUND_COLOR_RESOURCE_FORMAT = "com_auth0_lock_social_%s";
    private static final String TEXT_COLOR_RESOURCE_FORMAT = "com_auth0_lock_social_%s_text";

    @StringRes
    private int title;
    @ColorInt
    private int backgroundColor;
    @ColorInt
    private int textColor;
    @DrawableRes
    private int icon;

    public SocialConfig(Context context, @NonNull Strategy strategy) {
        if (strategy.getType() != Strategies.Type.SOCIAL) {
            throw new IllegalArgumentException("Only SOCIAL Strategies can have a SocialConfig");
        }

        generateResourcesForStrategy(context, strategy.getName());
    }

    private void generateResourcesForStrategy(Context context, String strategyName) {
        final Resources resources = context.getResources();
        final String pkgName = context.getPackageName();
        strategyName = strategyName.replace("-", "_");

        icon = resources.getIdentifier(String.format(ICON_RESOURCE_FORMAT, strategyName), "drawable", pkgName);
        icon = icon == 0 ? R.drawable.com_auth0_lock_social_icon_auth0 : icon;

        title = resources.getIdentifier(String.format(TITLE_RESOURCE_FORMAT, strategyName), "string", pkgName);
        title = title == 0 ? R.string.com_auth0_lock_social_unknown_placeholder : title;

        int backgroundColorRes = resources.getIdentifier(String.format(BACKGROUND_COLOR_RESOURCE_FORMAT, strategyName), "color", pkgName);
        backgroundColorRes = backgroundColorRes == 0 ? R.color.com_auth0_lock_social_unknown : backgroundColorRes;

        int textColorRes = resources.getIdentifier(String.format(TEXT_COLOR_RESOURCE_FORMAT, strategyName), "color", pkgName);
        textColorRes = textColorRes == 0 ? R.color.com_auth0_lock_social_unknown_text : textColorRes;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            backgroundColor = resources.getColor(backgroundColorRes, context.getTheme());
            textColor = resources.getColor(textColorRes, context.getTheme());
        } else {
            //noinspection deprecation
            backgroundColor = resources.getColor(backgroundColorRes);
            //noinspection deprecation
            textColor = resources.getColor(textColorRes);
        }
    }

    @StringRes
    public int getTitle() {
        return title;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @ColorInt
    public int getTextColor() {
        return textColor;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }
}
