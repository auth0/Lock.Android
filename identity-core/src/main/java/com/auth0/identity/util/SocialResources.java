/*
 * SocialResources.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.identity.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public abstract class SocialResources {
    private static final String TEXT_COLOR_KEY_FORMAT = "color/com_auth0_social_%s_text";
    private static final String COLOR_KEY_FORMAT = "color/com_auth0_social_%s";
    private static final String TEXT_KEY_FORMAT = "string/com_auth0_social_%s";
    private static final String ICON_KEY_FORMAT = "string/com_auth0_social_icon_%s";
    private static final String SOCIAL_FONT_FILE_NAME = "z-social.ttf";

    public static Typeface socialFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), SOCIAL_FONT_FILE_NAME);
    }

    public static int textColorForSocialService(Context context, String service) {
        String colorIdentifier = String.format(TEXT_COLOR_KEY_FORMAT, normalizeServiceName(service));
        int resId = resourceFromIdentifier(context, colorIdentifier);
        return resId == 0 ? Color.BLACK : context.getResources().getColor(resId);
    }

    public static int colorForSocialService(Context context, String service) {
        String colorIdentifier = String.format(COLOR_KEY_FORMAT, normalizeServiceName(service));
        int resId = resourceFromIdentifier(context, colorIdentifier);
        return resId == 0 ? Color.BLACK : context.getResources().getColor(resId);
    }

    public static int titleForSocialService(Context context, String service) {
        String titleIdentifier = String.format(TEXT_KEY_FORMAT, normalizeServiceName(service));
        return resourceFromIdentifier(context, titleIdentifier);
    }

    public static int iconForSocialService(Context context, String service) {
        String iconIdentifier = String.format(ICON_KEY_FORMAT, normalizeServiceName(service));
        return resourceFromIdentifier(context, iconIdentifier);
    }

    private static String normalizeServiceName(String name) {
        return name.replace('-', '_');
    }

    private static int resourceFromIdentifier(Context context, String identifier) {
        return context.getResources().getIdentifier(identifier, null, context.getPackageName());
    }

}
