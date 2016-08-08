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
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.json.Strategy;

class SocialConfig {
    private static final String TAG = SocialConfig.class.getSimpleName();
    private static final String ICON_RESOURCE_FORMAT = "com_auth0_lock_ic_social_%s";
    private static final String NAME_RESOURCE_FORMAT = "com_auth0_lock_social_%s";
    private static final String BACKGROUND_COLOR_RESOURCE_FORMAT = "com_auth0_lock_social_%s";

    private String name;
    @ColorInt
    private int backgroundColor;
    @DrawableRes
    private int icon;

    public SocialConfig(Context context, @NonNull Strategy strategy) {
        if (strategy.getType() != Strategies.Type.SOCIAL) {
            Log.e(TAG, "Invalid Strategy: Only SOCIAL Strategies can have a SocialConfig");
            throw new IllegalArgumentException("Only SOCIAL Strategies can have a SocialConfig");
        }

        generateResourcesForStrategy(context, strategy.getName());
    }

    private void generateResourcesForStrategy(Context context, String strategyName) {
        final Resources resources = context.getResources();
        final String pkgName = context.getPackageName();
        final String xmlSafeStrategyName = strategyName.replace("-", "_");

        icon = resources.getIdentifier(String.format(ICON_RESOURCE_FORMAT, xmlSafeStrategyName), "drawable", pkgName);
        icon = icon == 0 ? R.drawable.com_auth0_lock_ic_social_auth0 : icon;

        int nameRes = resources.getIdentifier(String.format(NAME_RESOURCE_FORMAT, xmlSafeStrategyName), "string", pkgName);
        name = nameRes == 0 ? strategyName : context.getString(nameRes);

        int backgroundColorRes = resources.getIdentifier(String.format(BACKGROUND_COLOR_RESOURCE_FORMAT, xmlSafeStrategyName), "color", pkgName);
        backgroundColorRes = backgroundColorRes == 0 ? R.color.com_auth0_lock_social_unknown : backgroundColorRes;

        backgroundColor = ContextCompat.getColor(context, backgroundColorRes);
    }

    public String getName() {
        return name;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }
}
