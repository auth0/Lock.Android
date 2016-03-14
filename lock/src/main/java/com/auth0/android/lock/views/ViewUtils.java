/*
 * ViewUtils.java
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
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

abstract class ViewUtils {

    static final int CORNER_RADIUS = 5;

    enum Corners {ONLY_LEFT, ONLY_RIGHT, ALL}

    static float dipToPixels(Resources resources, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
    }

    @ColorInt
    static int obtainColor(Context context, int colorRes) {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(colorRes, context.getTheme());
        } else {
            //noinspection deprecation
            color = context.getResources().getColor(colorRes);
        }
        return color;
    }

    static ShapeDrawable getRoundedBackground(Resources resources, @ColorInt int color, Corners corners) {
        float r = ViewUtils.dipToPixels(resources, CORNER_RADIUS);
        float[] outerR = new float[0];
        switch (corners) {
            case ONLY_LEFT:
                outerR = new float[]{r, r, 0, 0, 0, 0, r, r};
                break;
            case ONLY_RIGHT:
                outerR = new float[]{0, 0, r, r, r, r, 0, 0};
                break;
            case ALL:
                outerR = new float[]{r, r, r, r, r, r, r, r};
                break;
        }

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        return drawable;
    }
}
